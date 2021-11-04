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

import com.foreach.across.modules.ldap.LdapModuleSettings;
import com.foreach.across.modules.ldap.business.LdapConnector;
import com.foreach.across.modules.ldap.business.LdapConnectorSettings;
import com.foreach.across.modules.ldap.business.LdapUserDirectory;
import com.foreach.across.modules.ldap.events.LdapEntityDeletedEvent;
import com.foreach.across.modules.ldap.events.LdapEntityProcessedEvent;
import com.foreach.across.modules.ldap.services.properties.LdapConnectorSettingsService;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipalAuthenticationToken;
import com.foreach.across.modules.spring.security.infrastructure.services.CloseableAuthentication;
import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalService;
import com.foreach.across.modules.user.business.*;
import com.foreach.across.modules.user.services.GroupService;
import com.foreach.across.modules.user.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.ldap.core.DirContextAdapter;

import java.util.*;

/**
 * @author Marc Vanbrabant
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class LdapSynchronizationServiceImpl implements LdapSynchronizationService
{
	private final UserService userService;
	private final GroupService groupService;
	private final LdapConnectorSettingsService ldapConnectorSettingsService;
	private final LdapSearchService ldapSearchService;
	private final SecurityPrincipalService securityPrincipalService;
	private final ApplicationEventPublisher eventPublisher;
	private final LdapPropertiesService ldapPropertiesService;
	private final LdapModuleSettings ldapModuleSettings;
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
		if ( ldapModuleSettings.isDeleteUsersAndGroupsWhenDeletedFromLdapSource() ) {
			CollectionUtils.subtract( userService.findAll( QUser.user.userDirectory.eq( ldapUserDirectory ) ), users )
			               .forEach( user -> {
				               user.getGroups().clear();
				               userService.save( user );
				               try {
					               eventPublisher.publishEvent( new LdapEntityDeletedEvent<>( user ) );
					               userService.delete( user.getId() );
				               }
				               catch ( Exception e ) {
					               LOG.error(
							               "Could not delete user '{}', maybe you have entities referencing this user?",
							               e );
				               }
			               } );
		}
	}

	private void removeDeletedGroups( LdapUserDirectory ldapUserDirectory, Map<String, Group> groups ) {
		if ( ldapModuleSettings.isDeleteUsersAndGroupsWhenDeletedFromLdapSource() ) {
			CollectionUtils.subtract( groupService.findAll( QGroup.group.userDirectory.eq( ldapUserDirectory ) ),
			                          groups.values() ).forEach( group -> {
				userService.findAll( QUser.user.groups.contains( group ) ).forEach( user -> {
					user.getGroups().remove( group );
					userService.save( user );
				} );
				try {
					eventPublisher.publishEvent( new LdapEntityDeletedEvent<>( group ) );
					groupService.delete( group.getId() );
				}
				catch ( Exception e ) {
					LOG.error( "Could not delete group '{}', maybe you have entities referencing this group?", e );
				}
			} );
		}
	}

	private CloseableAuthentication authenticateAsConnector( LdapConnector connector ) {
		if ( connector.getSynchronizationPrincipalName() != null ) {
			return securityPrincipalService.getPrincipalByName( connector.getSynchronizationPrincipalName() )
			                               .map( principal -> new CloseableAuthentication( new SecurityPrincipalAuthenticationToken( principal ) ) )
			                               .orElse( null );
		}
		return null;
	}

	Set<User> performUserSynchronization( LdapUserDirectory userDirectory ) {
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
								User user = createNewUserWithBasicProperties( userDirectory, name );
								updateUserBasedOnLdapInformation( itemsInLdap, ldapConnectorSettings, adapter, user );
							}
							else {
								if ( users.size() > 1 ) {
									LOG.debug( "Multiple users found for for name {}", name );
								}
								users.forEach( user -> updateUserBasedOnLdapInformation( itemsInLdap, ldapConnectorSettings, adapter, user ) );
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

	private User createNewUserWithBasicProperties( LdapUserDirectory userDirectory, String name ) {
		User user = new User();
		user.setUsername( name );
		user.setEmailConfirmed( true );
		user.setPassword( UUID.randomUUID().toString() );
		user.setUserDirectory( userDirectory );
		return user;
	}

	private void updateUserBasedOnLdapInformation( Set<User> itemsInLdap, LdapConnectorSettings ldapConnectorSettings, DirContextAdapter adapter, User user ) {
		try {
			user.setEmail( adapter.getStringAttribute( ldapConnectorSettings.getUserEmail() ) );
			user.setFirstName( adapter.getStringAttribute( ldapConnectorSettings.getFirstName() ) );
			user.setLastName( adapter.getStringAttribute( ldapConnectorSettings.getLastName() ) );
			user.setDisplayName( adapter.getStringAttribute( ldapConnectorSettings.getDiplayName() ) );
			user.setDeleted( false );
			setRestrictions( user, ldapConnectorSettings, adapter );

			userService.save( user );

			itemsInLdap.add( user );
			ldapPropertiesService.saveLdapProperties( user, adapter );
		}
		catch ( Exception e ) {
			LOG.error( "Something went wrong trying to update existing user {}", user.getDisplayName(), e );
		}
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
										i -> StringUtils.equalsIgnoreCase( i.getKey(), member ) ).findFirst().ifPresent(
										g -> groupsForUser.add( g.getValue() ) );
							}
						}

						if ( StringUtils.isNotBlank( name ) ) {
							users.stream().filter( i -> StringUtils.equalsIgnoreCase( i.getUsername(), name ) )
							     .findFirst().ifPresent( user -> {
								                             user.setGroups( groupsForUser );
								                             userService.save( user );
								                             eventPublisher.publishEvent( new LdapEntityProcessedEvent<>( user, true, adapter ) );
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
								eventPublisher.publishEvent( new LdapEntityProcessedEvent<>( group, false, adapter ) );
								itemsInLdap.putIfAbsent( adapter.getNameInNamespace(), group );
							}
							else {
								Group group = groups.iterator().next();
								eventPublisher.publishEvent( new LdapEntityProcessedEvent<>( group, true, adapter ) );
								itemsInLdap.putIfAbsent( adapter.getNameInNamespace(), group );
							}

						}
						return adapter.getStringAttribute( ldapConnectorSettings.getUsername() );
					} );

		}
		return itemsInLdap;
	}
}
