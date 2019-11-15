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
import com.foreach.across.modules.ldap.services.LdapAuthenticationProvider;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.business.UserDirectory;
import com.foreach.across.modules.user.services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.ldap.server.ApacheDSContainer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Marc Vanbrabant
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration
public class TestLdapAuthenticationProvider
{
	@Autowired
	private LdapAuthenticationProvider ldapAuthenticationProvider;
	@Autowired
	private UserService userService;
	@Autowired
	private LdapUserDirectory ldapUserDirectory;

	@Before
	public void resetMocks() {
		reset( userService );
		ldapAuthenticationProvider.setThrowExceptionIfUserNotFound( false );
		ldapAuthenticationProvider.setSearchFilter( null );
	}

	@Test(expected = InternalAuthenticationServiceException.class)
	public void testThatUnknownUserThrowsExceptionWhenThrowExceptionIfUserNotFoundIsTrue() throws Exception {
		ldapAuthenticationProvider.setThrowExceptionIfUserNotFound( true );
		Authentication
				authentication = ldapAuthenticationProvider.authenticate(
				new UsernamePasswordAuthenticationToken( "username", "password" ) );
		assertTrue( "shouldn't come here", false );
	}

	@Test
	public void testThatUnknownUserThrowsExceptionWhenThrowExceptionIfUserNotFoundIsFalse() throws Exception {
		Authentication
				authentication = ldapAuthenticationProvider.authenticate(
				new UsernamePasswordAuthenticationToken( "username", "password" ) );
		assertNull( authentication );
	}

	@Test
	public void knownUserGetsAuthenticatedAsSecurityPrincipal() throws Exception {
		User user = new User();
		user.setUsername( "abergin" );
		user.setPassword( "inflict" );
		when( userService.getUserByUsername( "abergin", ldapUserDirectory ) ).thenReturn( Optional.of( user ) );
		LdapConnectorSettings ldapConnectorSettings = mock( LdapConnectorSettings.class );
		when( ldapConnectorSettings.getUserObjectFilterForUser() ).thenReturn( "foo" );
		ldapAuthenticationProvider.setSearchFilter( "(&(objectclass=inetorgperson)(uid={0}))" );
		Authentication
				authentication = ldapAuthenticationProvider.authenticate(
				new UsernamePasswordAuthenticationToken( "abergin", "inflict" ) );
		assertNotNull( authentication );
		assertEquals( user.getSecurityPrincipalId(), authentication.getPrincipal() );
	}

	@Configuration
	protected static class Config
	{
		@Bean
		public LdapAuthenticationProvider ldapAuthenticationProvider() {
			LdapAuthenticationProvider ldapAuthenticationProvider = new LdapAuthenticationProvider();
			ldapAuthenticationProvider.setUserService( userService() );
			ldapAuthenticationProvider.setLdapContextSource( ldapConnector() );
			ldapAuthenticationProvider.setUserDirectory( ldapUserDirectory( ldapConnector() ) );
			return ldapAuthenticationProvider;
		}

		@Bean
		public LdapConnector ldapConnector() {
			LdapConnector ldapConnector = new LdapConnector();
			ldapConnector.setId( 1L );
			ldapConnector.setUsername( "uid=admin,ou=system" );
			ldapConnector.setPassword( "secret" );
			ldapConnector.setBaseDn( "dc=foreach,dc=com" );
			ldapConnector.setHostName( "127.0.0.1" );
			ldapConnector.setAdditionalUserDn( "ou=People" );
			ldapConnector.setPort( 53389 );
			ldapConnector.setLdapConnectorType( LdapConnectorType.OPENDS );
			return ldapConnector;
		}

		@Bean
		public UserDirectory ldapUserDirectory( LdapConnector ldapConnector ) {
			LdapUserDirectory ldapUserDirectory = new LdapUserDirectory();
			ldapUserDirectory.setLdapConnector( ldapConnector );
			return ldapUserDirectory;
		}

		@Bean
		public ApacheDSContainer apacheDSContainer() throws Exception {
			return new ApacheDSContainer( "dc=foreach,dc=com", "classpath:ldif/opends.ldif" );
		}

		@Bean
		public UserService userService() {
			return mock( UserService.class );
		}
	}
}
