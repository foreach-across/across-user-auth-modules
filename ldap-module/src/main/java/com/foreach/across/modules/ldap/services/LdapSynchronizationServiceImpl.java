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

import com.foreach.across.modules.ldap.business.LdapConnector;
import com.foreach.across.modules.ldap.business.LdapConnectorSettings;
import com.foreach.across.modules.ldap.services.properties.LdapConnectorSettingsService;
import com.foreach.across.modules.user.business.*;
import com.foreach.across.modules.user.services.GroupService;
import com.foreach.across.modules.user.services.UserDirectoryService;
import com.foreach.across.modules.user.services.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.HardcodedFilter;

import javax.naming.directory.SearchControls;
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
	private UserDirectoryService userDirectoryService;

	private Set<LdapConnector> busy = new HashSet<>();

	public boolean synchronizeData( LdapConnector connector ) {
		if ( !busy.contains( connector ) ) {
			busy.add( connector );

			LOG.info( "Synchronizing directory {}", connector.getHostName() );

			StopWatch watch = new StopWatch();
			watch.start();
			int totalUsersSynchronized = 0, totalGroupsSynchronized = 0;

			try {
				totalUsersSynchronized += performUserSynchronization( connector );
				totalGroupsSynchronized += performGroupSynchronization( connector );

			}
			catch ( Exception ie ) {
				LOG.error( "Failed to synchronize directory {}", connector.getHostName(), ie );
			}

			watch.stop();
			LOG.info( "Synchronization of directory {} finished, users synchronized: {}, groups synchronized: {}",
			          connector.getHostName(), totalUsersSynchronized, totalGroupsSynchronized );
			LOG.info( "Elapsed time: {}", watch.toString() );

			busy.remove( connector );

			return true;
		}

		LOG.debug( "Skipping synchronization of directory {}, still busy", connector.getHostName() );
		return false;
	}

	private int performUserSynchronization( LdapConnector connector ) {
		LdapConnectorSettings ldapConnectorSettings = ldapConnectorSettingsService.getProperties( connector.getId() );
		UserDirectory userDirectory = getUserDirectory( ldapConnectorSettings );

		AndFilter andFilter = new AndFilter();
		andFilter.and( new EqualsFilter( "objectclass", ldapConnectorSettings.getUserObjectClass() ) );
		andFilter.and( new HardcodedFilter( ldapConnectorSettings.getUserObjectFilter() ) );

		List<User> itemsInLdap = new ArrayList<>();
		QUser query = QUser.user;

		performSearch( connector, andFilter, ctx -> {
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
					user.setRestrictions( Collections.singleton( UserRestriction.DISABLED ) );
					user.setPassword( UUID.randomUUID().toString() );
					user.setUserDirectory( userDirectory );
					userService.save( user );
					itemsInLdap.add( user );
				}
				else {
					users.stream().forEach( user -> {
						user.setEmail( adapter.getStringAttribute( ldapConnectorSettings.getUserEmail() ) );
						user.setFirstName( adapter.getStringAttribute( ldapConnectorSettings.getFirstName() ) );
						user.setLastName( adapter.getStringAttribute( ldapConnectorSettings.getLastName() ) );
						user.setDisplayName( adapter.getStringAttribute( ldapConnectorSettings.getDiplayName() ) );
						user.setDeleted( false );
						userService.save( user );
						itemsInLdap.add( user );
					} );
				}
			}

			return adapter.getStringAttribute( ldapConnectorSettings.getUsername() );
		} );
		// Mark users as deleted that are not in AD anymore
		Collection<User> deletedUsers = userService.findAll(
				query.notIn( itemsInLdap ).and( query.userDirectory.eq( userDirectory ) ) );
		deletedUsers.stream().forEach( user -> {
			user.setDeleted( true );
			userService.save( user );
		} );

		return itemsInLdap.size();
	}

	private int performGroupSynchronization( LdapConnector connector ) {
		LdapConnectorSettings ldapConnectorSettings = ldapConnectorSettingsService.getProperties( connector.getId() );
		UserDirectory userDirectory = getUserDirectory( ldapConnectorSettings );

		AndFilter andFilter = new AndFilter();
		andFilter.and( new EqualsFilter( "objectclass", ldapConnectorSettings.getGroupObjectClass() ) );
		andFilter.and( new HardcodedFilter( ldapConnectorSettings.getGroupObjectFilter() ) );

		List<Group> itemsInLdap = new ArrayList<>();
		QGroup query = QGroup.group;

		performSearch( connector, andFilter, ctx -> {
			DirContextAdapter adapter = (DirContextAdapter) ctx;
			String name = adapter.getStringAttribute( ldapConnectorSettings.getGroupName() );

			if ( StringUtils.isNotBlank( name ) ) {
				Collection<Group> groups = groupService.findAll( query.name.equalsIgnoreCase( name ) );
				if ( groups.isEmpty() ) {
					Group group = new Group();
					group.setName( adapter.getStringAttribute( ldapConnectorSettings.getGroupName() ) );
					group.setUserDirectory( userDirectory );
					groupService.save( group );
					itemsInLdap.add( group );
				}
			}

			return adapter.getStringAttribute( ldapConnectorSettings.getUsername() );
		} );

		//TODO: implement group deletion?

		return itemsInLdap.size();
	}

	private UserDirectory getUserDirectory( LdapConnectorSettings ldapConnectorSettings ) {
		Optional<UserDirectory> userDirectory = userDirectoryService.getUserDirectories().stream().filter(
				item -> Objects
						.equals(
								item.getId(),
								Long.valueOf( ldapConnectorSettings.getValue( "user_directory_id" ) ) )

		).findFirst();
		return userDirectory.isPresent() ? userDirectory.get() : userDirectoryService.getDefaultUserDirectory();
	}

	private void performSearch( LdapConnector connector, Filter filter, ContextMapper<String> ctx ) {
		SearchControls controls = new SearchControls();
		controls.setSearchScope( SearchControls.SUBTREE_SCOPE );
		controls.setTimeLimit( connector.getSearchTimeout() );
		controls.setCountLimit( 0 );
		controls.setReturningObjFlag( true );
		controls.setReturningAttributes( null );

		//TODO: detect if connector is pageable, or store the page size on the connector?
		PagedResultsDirContextProcessor processor = new PagedResultsDirContextProcessor( 20 );

		LdapTemplate ldapTemplate = ldapTemplate( connector );

		do {
			ldapTemplate.search( "", filter.encode(), controls, ctx, processor );
			processor = new PagedResultsDirContextProcessor( processor.getPageSize(), processor.getCookie() );
		}
		while ( processor.getCookie().getCookie() != null );
	}

	private LdapTemplate ldapTemplate( LdapConnector connector ) {
		LdapContextSource source = new LdapContextSource();
		source.setPooled( true );
		source.setUrl( "ldap://" + connector.getHostName() + ":" + connector.getPort() );
		source.setBase( connector.getBaseDn() );
		source.setUserDn( connector.getUsername() );
		source.setPassword( connector.getPassword() );
		source.afterPropertiesSet();
		LdapTemplate ldapTemplate = new LdapTemplate( source );
		// TODO: put this in a setting? Microsoft Active Directory cannot follow referrals when in the root context
		ldapTemplate.setIgnorePartialResultException( true );
		return ldapTemplate;
	}
}
