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
import com.foreach.across.modules.ldap.business.LdapConnectorType;
import com.foreach.across.modules.ldap.business.LdapUserDirectory;
import com.foreach.across.modules.ldap.events.LdapEntityDeletedEvent;
import com.foreach.across.modules.ldap.services.properties.LdapConnectorSettingsService;
import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalService;
import com.foreach.across.modules.user.business.QUser;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.services.GroupService;
import com.foreach.across.modules.user.services.UserDirectoryService;
import com.foreach.across.modules.user.services.UserService;
import com.foreach.common.spring.properties.PropertyTypeRegistry;
import com.querydsl.core.types.Predicate;
import lombok.Getter;
import org.apache.directory.shared.ldap.entry.Entry;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.security.ldap.server.ApacheDSContainer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * @author Marc Vanbrabant
 * @since 1.0.0
 */
@ExtendWith(SpringExtension.class)
@DirtiesContext
@ContextConfiguration(classes = TestLdapConnectorSynchronization.Config.class)
public class TestLdapConnectorSynchronization
{
	@Autowired
	private ApacheDSContainer ldapContainer;

	@Autowired
	private LdapSynchronizationServiceImpl ldapSynchronizationService;

	@Autowired
	private LdapConnector ldapConnector;

	@Autowired
	private LdapConnectorSettingsService ldapConnectorSettingsService;

	@Autowired
	private ConversionService conversionService;

	@Autowired
	private UserService userService;

	@Autowired
	private UserDirectoryService userDirectoryService;

	@Autowired
	private LdapModuleSettings ldapModuleSettings;

	@Autowired
	private Config.EventListener applicationListener;

	@BeforeEach
	public void resetMocks() {
		reset( userService, userDirectoryService, ldapModuleSettings );
	}

	@Test
	public void apacheDsContainerHasLoadedLdifData() throws Exception {
		Entry result = ldapContainer.getService().getAdminSession().lookup( new LdapDN( "dc=foreach,dc=com" ) );
		assertNotNull( result );
		assertEquals( "0.9.2342.19200300.100.1.25=foreach,0.9.2342.19200300.100.1.25=com",
		              result.getDn().getNormName() );
	}

	@Test
	public void ldapUserIsSynchronizedForAllUsers() {
		PropertyTypeRegistry<String> registry = new PropertyTypeRegistry<>();
		registry.setDefaultConversionService( conversionService );
		LdapConnectorSettings ldapConnectorSettings = new LdapConnectorSettings( ldapConnector.getId(),
		                                                                         registry,
		                                                                         () -> ldapConnector
				                                                                         .getLdapConnectorType()
				                                                                         .getSettings() );
		when( ldapConnectorSettingsService.getProperties( ldapConnector.getId() ) ).thenReturn( ldapConnectorSettings );
		LdapUserDirectory ldapUserDirectory = new LdapUserDirectory();
		ldapUserDirectory.setLdapConnector( ldapConnector );
		ldapSynchronizationService.synchronizeData( ldapUserDirectory );

		verify( userService, times( 300 ) ).save( any( User.class ) );
	}

	@Test
	public void ldapUserMarkedDeletedWhenRemovedFromLdapSource() {
		when( ldapModuleSettings.isDeleteUsersAndGroupsWhenDeletedFromLdapSource() ).thenReturn( true );
		PropertyTypeRegistry<String> registry = new PropertyTypeRegistry<>();
		registry.setDefaultConversionService( conversionService );
		LdapConnectorSettings ldapConnectorSettings = new LdapConnectorSettings( ldapConnector.getId(),
		                                                                         registry,
		                                                                         () -> ldapConnector
				                                                                         .getLdapConnectorType()
				                                                                         .getSettings() );

		when( ldapConnectorSettingsService.getProperties( ldapConnector.getId() ) ).thenReturn( ldapConnectorSettings );
		LdapUserDirectory ldapUserDirectory = new LdapUserDirectory();
		ldapUserDirectory.setLdapConnector( ldapConnector );

		User userDeletedFromLdapSource = new User();
		userDeletedFromLdapSource.setUsername( "test" );
		userDeletedFromLdapSource.setId( 3333L );

		when( userService.findAll( QUser.user.userDirectory.eq( ldapUserDirectory ) ) ).thenReturn(
				Collections.singleton( userDeletedFromLdapSource ) );

		doReturn( Collections.emptySet() ).when( ldapSynchronizationService ).performUserSynchronization(
				ldapUserDirectory );
		ldapSynchronizationService.synchronizeData( ldapUserDirectory );

		List<LdapEntityDeletedEvent<User>> events = applicationListener.getLdapEntityDeletedEvent();
		assertNotNull( events );
		assertEquals( 1, events.size() );
		assertEquals( userDeletedFromLdapSource, events.get( 0 ).getEntity() );
		verify( userService ).delete( 3333L );
	}

	@Test
	public void ldapSynchronizationFails() {
		when( userService.findAll( any( Predicate.class ) ) ).thenThrow( new RuntimeException( "Failure" ) );
		LdapUserDirectory ldapUserDirectory = new LdapUserDirectory();
		ldapUserDirectory.setLdapConnector( ldapConnector );
		ldapSynchronizationService.synchronizeData( ldapUserDirectory );

		verify( userService, times( 0 ) ).save( any( User.class ) );
	}

	@Configuration
	protected static class Config
	{
		@Bean
		public LdapSynchronizationService ldapSynchronizationService( ApplicationEventPublisher eventPublisher ) {
			return spy( new LdapSynchronizationServiceImpl( userService(), mock( GroupService.class ),
			                                                ldapConnectorSettingsService(), ldapSearchService(),
			                                                mock( SecurityPrincipalService.class ),
			                                                eventPublisher,
			                                                mock( LdapPropertiesService.class ),
			                                                ldapModuleSettings() ) );
		}

		@Bean
		public UserService userService() {
			return mock( UserService.class );
		}

		@Bean
		public LdapConnectorSettingsService ldapConnectorSettingsService() {
			return mock( LdapConnectorSettingsService.class );
		}

		@Bean
		public UserDirectoryService userDirectoryService() {
			return mock( UserDirectoryService.class );
		}

		@Bean
		public LdapSearchService ldapSearchService() {
			return new LdapSearchServiceImpl();
		}

		@Bean
		public LdapModuleSettings ldapModuleSettings() {
			return mock( LdapModuleSettings.class );
		}

		@Bean
		public EventListener applicationListener() {
			return new EventListener();
		}

		@Bean
		public LdapConnector openDsConnector() {
			LdapConnector ldapConnector = new LdapConnector();
			ldapConnector.setId( 1L );
			ldapConnector.setUsername( "uid=admin,ou=system" );
			ldapConnector.setPassword( "secret" );
			ldapConnector.setBaseDn( "dc=foreach,dc=com" );
			ldapConnector.setHostName( "127.0.0.1" );
			ldapConnector.setPort( 53389 );
			ldapConnector.setLdapConnectorType( LdapConnectorType.OPENDS );

			LdapUserDirectory ldapUserDirectory = new LdapUserDirectory();
			ldapUserDirectory.setName( "OpenDS User Directory" );
			ldapUserDirectory.setId( 2L );
			ldapUserDirectory.setActive( true );
			ldapUserDirectory.setLdapConnector( ldapConnector );
			return ldapConnector;
		}

		@Bean
		public ApacheDSContainer apacheDSContainer() throws Exception {
			return new ApacheDSContainer( "dc=foreach,dc=com", "classpath:ldif/opends.ldif" );
		}

		@Bean
		public ConversionService conversionService() {
			return new DefaultConversionService();
		}

		@Getter
		public static final class EventListener implements ApplicationListener<PayloadApplicationEvent<LdapEntityDeletedEvent<User>>>
		{
			private List<LdapEntityDeletedEvent<User>> ldapEntityDeletedEvent = new ArrayList<>();

			@Override
			public void onApplicationEvent( PayloadApplicationEvent<LdapEntityDeletedEvent<User>> event ) {
				ldapEntityDeletedEvent.add( event.getPayload() );
			}
		}
	}
}
