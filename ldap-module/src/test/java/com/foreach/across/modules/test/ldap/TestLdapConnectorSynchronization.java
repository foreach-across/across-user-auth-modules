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

package com.foreach.across.modules.test.ldap;

import com.foreach.across.modules.ldap.business.LdapConnector;
import com.foreach.across.modules.ldap.business.LdapConnectorSettings;
import com.foreach.across.modules.ldap.business.LdapConnectorType;
import com.foreach.across.modules.ldap.business.LdapUserDirectory;
import com.foreach.across.modules.ldap.services.LdapSearchService;
import com.foreach.across.modules.ldap.services.LdapSearchServiceImpl;
import com.foreach.across.modules.ldap.services.LdapSynchronizationService;
import com.foreach.across.modules.ldap.services.LdapSynchronizationServiceImpl;
import com.foreach.across.modules.ldap.services.properties.LdapConnectorSettingsService;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.services.UserDirectoryService;
import com.foreach.across.modules.user.services.UserService;
import com.foreach.common.spring.properties.PropertyTypeRegistry;
import com.foreach.common.test.MockedLoader;
import org.apache.directory.shared.ldap.entry.Entry;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.security.ldap.server.ApacheDSContainer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.naming.NamingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Marc Vanbrabant
 * @since 1.0.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(loader = MockedLoader.class, classes = TestLdapConnectorSynchronization.Config.class)
public class TestLdapConnectorSynchronization
{
	@Autowired
	private ApacheDSContainer ldapContainer;

	@Autowired
	private LdapSynchronizationService ldapSynchronizationService;

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
	private ApacheDSContainer apacheDSContainer;

	@Before
	public void resetMocks() throws NamingException {
		reset( userService, userDirectoryService );
	}

	@Test
	public void testThatApacheDsContainerHasLoadedLdifData() throws Exception {
		Entry result = ldapContainer.getService().getAdminSession().lookup( new LdapDN( "dc=foreach,dc=com" ) );
		assertNotNull( result );
		assertEquals( "0.9.2342.19200300.100.1.25=foreach,0.9.2342.19200300.100.1.25=com",
		              result.getDn().getNormName() );
	}

	@Test
	public void testThatLdapUserIsSynchronizedForAllUsers() throws Exception {
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

		verify( userService, times( 150 ) ).save( any( User.class ) );
	}

	@Test
	public void testThatLdapSynchronizationFails() throws Exception {
		when( userService.findAll( any() ) ).thenThrow( new RuntimeException( "Failure" ) );
		LdapUserDirectory ldapUserDirectory = new LdapUserDirectory();
		ldapUserDirectory.setLdapConnector( ldapConnector );
		ldapSynchronizationService.synchronizeData( ldapUserDirectory );

		verify( userService, times( 0 ) ).save( any( User.class ) );
	}

	@Configuration
	protected static class Config
	{
		@Bean
		public LdapSynchronizationService ldapSynchronizationService() {
			return new LdapSynchronizationServiceImpl();
		}

		@Bean
		public LdapSearchService ldapSearchService() {
			return new LdapSearchServiceImpl();
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
	}
}
