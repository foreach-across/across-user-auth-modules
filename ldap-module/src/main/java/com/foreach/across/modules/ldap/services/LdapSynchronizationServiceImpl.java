/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.foreach.across.modules.ldap.services;

import com.foreach.across.core.events.AcrossEventPublisher;
import com.foreach.across.modules.ldap.business.LdapConnector;
import com.foreach.across.modules.ldap.business.LdapConnectorSettings;
import com.foreach.across.modules.ldap.business.LdapUserDirectory;
import com.foreach.across.modules.ldap.events.LdapEntitySavedEvent;
import com.foreach.across.modules.ldap.services.properties.LdapConnectorSettingsService;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipalAuthenticationToken;
import com.foreach.across.modules.spring.security.infrastructure.services.CloseableAuthentication;
import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalService;
import com.foreach.across.modules.user.business.*;
import com.foreach.across.modules.user.services.GroupService;
import com.foreach.across.modules.user.services.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextAdapter;

import java.util.*;

/**
 * @author Marc Vanbrabant
 * @since 1.0.0
 */
public class LdapSynchronizationServiceImpl implements LdapSynchronizationService
{
	private static final Logger LOG = LoggerFactory.getLogger( LdapSynchronizationService.class );

	@Autowired
	private UserService userService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private LdapConnectorSettingsService ldapConnectorSettingsService;

	@Autowired
	private LdapSearchService ldapSearchService;

	@Autowired
	private SecurityPrincipalService securityPrincipalService;

	@Autowired
	private AcrossEventPublisher eventPublisher;

	@Autowired
	private LdapPropertiesService ldapPropertiesService;

	private Set<LdapUserDirectory> busy = new HashSet<>();

	public boolean synchronizeData( LdapUserDirectory ldapUserDirectory ) {

		if ( !busy.contains( ldapUserDirectory ) ) {
			busy.add( ldapUserDirectory );

			LOG.info( "Synchronizing directory {}", ldapUserDirectory.getName() );

			StopWatch watch = new StopWatch();
			watch.start();
			int totalUsersSynchronized = 0, totalGroupsSynchronized = 0;

			CloseableAuthentication authentication = authenticateAsConnector( ldapUserDirectory.getLdapConnector() );

			try {
				Set<User> users = performUserSynchronization( ldapUserDirectory );
				Map<String, Group> groups = performGroupSynchronization( ldapUserDirectory );
				synchronizeUserMemberships( ldapUserDirectory, users, groups );
				removeDeletedGroups( ldapUserDirectory, groups );
				removeDeletedUsers( ldapUserDirectory, users );
				totalUsersSynchronized += users.size();
				totalGroupsSynchronized += groups.size();

			}
			catch ( Exception ie ) {
				LOG.error( "Failed to synchronize directory {}", ldapUserDirectory.getName(), ie );
			}
			finally {
				if ( authentication != null ) {
					authentication.close();
				}
			}

			watch.stop();
			LOG.info( "Synchronization of directory {} finished, users synchronized: {}, groups synchronized: {}",
			          ldapUserDirectory.getName(), totalUsersSynchronized, totalGroupsSynchronized );
			LOG.info( "Elapsed time: {}", watch.toString() );

			busy.remove( ldapUserDirectory );

			return true;
		}

		LOG.debug( "Skipping synchronization of directory {}, still busy", ldapUserDirectory.getName() );
		return false;
	}

	private void removeDeletedUsers( LdapUserDirectory ldapUserDirectory, Set<User> users ) {
		CollectionUtils.subtract( userService.findAll( QUser.user.userDirectory.eq( ldapUserDirectory ) ), users )
		               .forEach( u -> {
			               u.getGroups().clear();
			               userService.save( u );
			               userService.delete( u.getId() );
		               } );
	}

	private void removeDeletedGroups( LdapUserDirectory ldapUserDirectory, Map<String, Group> groups ) {
		CollectionUtils.subtract( groupService.findAll( QGroup.group.userDirectory.eq( ldapUserDirectory ) ),
		                          groups.values() ).forEach( g -> {
			userService.findAll( QUser.user.groups.contains( g ) ).forEach( u -> {
				u.getGroups().remove( g );
				userService.save( u );
			} );
			groupService.delete( g.getId() );
		} );
	}

	private CloseableAuthentication authenticateAsConnector( LdapConnector connector ) {
		if ( connector.getSynchronizationPrincipalName() != null ) {
			SecurityPrincipal principal = securityPrincipalService.getPrincipalByName(
					connector.getSynchronizationPrincipalName() );
			if ( principal != null ) {
				return new CloseableAuthentication( new SecurityPrincipalAuthenticationToken( principal ) );
			}
		}
		return null;
	}

	private Set<User> performUserSynchronization( LdapUserDirectory userDirectory ) {
		LdapConnector connector = userDirectory.getLdapConnector();
		Set<User> itemsInLdap = new HashSet<>();
		if ( connector != null ) {
			LdapConnectorSettings ldapConnectorSettings = ldapConnectorSettingsService.getProperties(
					connector.getId() );

			QUser query = QUser.user;

			ldapSearchService.performSearch( connector, connector.getAdditionalUserDn(),
			                                 ldapSearchService.getUserFilter( ldapConnectorSettings ), ctx -> {
				DirContextAdapter adapter = (DirContextAdapter) ctx;

				String name = adapter.getStringAttribute( ldapConnectorSettings.getUsername() );

				if ( StringUtils.isNotBlank( name ) ) {
					Collection<User> users = userService.findAll( query.username.equalsIgnoreCase( name ) );
					if ( users.isEmpty() ) {
						User user = new User();
						user.setUsername( name );
						user.setEmail( adapter.getStringAttribute( ldapConnectorSettings.getUserEmail() ) );
						user.setEmailConfirmed( true );
						user.setFirstName( adapter.getStringAttribute( ldapConnectorSettings.getFirstName() ) );
						user.setLastName( adapter.getStringAttribute( ldapConnectorSettings.getLastName() ) );
						user.setDisplayName( adapter.getStringAttribute( ldapConnectorSettings.getDiplayName() ) );
						user.setPassword( UUID.randomUUID().toString() );
						user.setUserDirectory( userDirectory );
						setRestrictions( user, ldapConnectorSettings, adapter );
						userService.save( user );
						itemsInLdap.add( user );
						ldapPropertiesService.saveLdapProperties( user, adapter );
					}
					else {
						users.forEach( user -> {
							user.setEmail( adapter.getStringAttribute(
									ldapConnectorSettings.getUserEmail() ) );
							user.setFirstName( adapter.getStringAttribute(
									ldapConnectorSettings.getFirstName() ) );
							user.setLastName( adapter.getStringAttribute(
									ldapConnectorSettings.getLastName() ) );
							user.setDisplayName( adapter.getStringAttribute(
									ldapConnectorSettings.getDiplayName() ) );
							user.setDeleted( false );
							setRestrictions( user, ldapConnectorSettings, adapter );
							userService.save( user );
							itemsInLdap.add( user );
							ldapPropertiesService.saveLdapProperties( user, adapter );
						} );
					}
				}

				return adapter.getStringAttribute( ldapConnectorSettings.getUsername() );
			} );

			// Mark users as deleted that are not in AD anymore
			Collection<User> deletedUsers = userService.findAll(
					query.notIn( itemsInLdap ).and( query.userDirectory.eq( userDirectory ) ) );
			deletedUsers.forEach( user -> {
				user.setDeleted( true );
				userService.save( user );
			} );

		}
		return itemsInLdap;
	}

	private void synchronizeUserMemberships( LdapUserDirectory userDirectory,
	                                         Set<User> users,
	                                         Map<String, Group> groups ) {
		LdapConnector connector = userDirectory.getLdapConnector();
		if ( connector != null ) {
			LdapConnectorSettings ldapConnectorSettings = ldapConnectorSettingsService.getProperties(
					connector.getId() );

			ldapSearchService.performSearch( connector, connector.getAdditionalUserDn(),
			                                 ldapSearchService.getUserFilter( ldapConnectorSettings ), ctx -> {
				DirContextAdapter adapter = (DirContextAdapter) ctx;

				String name = adapter.getStringAttribute( ldapConnectorSettings.getUsername() );
				String[] memberOf = adapter.getStringAttributes( ldapConnectorSettings.getUserMemberOf() );

				Set<Group> groupsForUser = new HashSet<>();
				if ( memberOf != null ) {
					for ( String member : memberOf ) {
						groups.entrySet().stream().filter(
								i -> i.getKey().equalsIgnoreCase( member ) ).findFirst().ifPresent(
								g -> groupsForUser.add( g.getValue() ) );
					}
				}

				if ( StringUtils.isNotBlank( name ) ) {
					users.stream().filter( i -> i.getUsername().equalsIgnoreCase( name ) )
					     .findFirst().ifPresent( user -> {
						                             user.setGroups( groupsForUser );
						                             userService.save( user );
						                             eventPublisher.publish( new LdapEntitySavedEvent<>( user, adapter ) );
					                             }
					);

				}

				return adapter.getStringAttribute( ldapConnectorSettings.getUsername() );
			} );
		}
	}

	private void setRestrictions( User user, LdapConnectorSettings ldapConnectorSettings, DirContextAdapter adapter ) {
		String userAccountControlProperty = adapter.getStringAttribute( ldapConnectorSettings.getUserAccountControl() );
		if ( StringUtils.isNumeric( userAccountControlProperty ) ) {
			int userAccountControl = Integer.parseInt( userAccountControlProperty );

			// For codes see: https://support.microsoft.com/en-us/kb/305144
			if ( ( userAccountControl & 2 ) == 2 ) {
				user.getRestrictions().add( UserRestriction.DISABLED );
			}
			if ( ( userAccountControl & 16 ) == 16 ) {
				user.getRestrictions().add( UserRestriction.LOCKED );
			}
			if ( ( userAccountControl & 8388608 ) == 8388608 ) {
				user.getRestrictions().add( UserRestriction.EXPIRED );
			}
		}
	}

	private Map<String, Group> performGroupSynchronization( LdapUserDirectory userDirectory ) {
		LdapConnector connector = userDirectory.getLdapConnector();
		Map<String, Group> itemsInLdap = new HashMap<>();
		if ( connector != null ) {
			LdapConnectorSettings ldapConnectorSettings = ldapConnectorSettingsService.getProperties(
					connector.getId() );

			QGroup query = QGroup.group;

			ldapSearchService.performSearch( connector, connector.getAdditionalGroupDn(),
			                                 ldapSearchService.getGroupFilter( ldapConnectorSettings ), ctx -> {
				DirContextAdapter adapter = (DirContextAdapter) ctx;
				String name = adapter.getStringAttribute( ldapConnectorSettings.getGroupName() );

				if ( StringUtils.isNotBlank( name ) ) {
					Collection<Group> groups = groupService.findAll( query.name.equalsIgnoreCase( name ) );
					if ( groups.isEmpty() ) {
						Group group = new Group();
						group.setName( adapter.getStringAttribute( ldapConnectorSettings.getGroupName() ) );
						group.setUserDirectory( userDirectory );
						groupService.save( group );
						ldapPropertiesService.saveLdapProperties( group, adapter );
						eventPublisher.publish( new LdapEntitySavedEvent<>( group, adapter ) );
						itemsInLdap.putIfAbsent( adapter.getNameInNamespace(), group );
					}
					else {
						itemsInLdap.putIfAbsent( adapter.getNameInNamespace(), groups.iterator().next() );
					}

				}
				return adapter.getStringAttribute( ldapConnectorSettings.getUsername() );
			} );

		}
		return itemsInLdap;
	}
}
