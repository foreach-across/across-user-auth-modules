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
import com.foreach.across.modules.ldap.business.LdapConnectorType;
import com.foreach.across.modules.ldap.config.LdapDirectorySettingsConfiguration;
import com.foreach.across.modules.ldap.services.LdapAuthenticationProvider;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.services.UserService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.ldap.server.ApacheDSContainer;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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

	@Before
	public void resetMocks() {
		reset( userService );
	}

	@Test(expected = BadCredentialsException.class)
	public void testThatUnknownUserDoesNotGetAuthenticated() throws Exception {
		Authentication
				authentication = ldapAuthenticationProvider.authenticate(
				new UsernamePasswordAuthenticationToken( "username", "password" ) );
		assertNull( authentication );
	}

	@Test
	@Ignore
	public void testThatKnownUserGetsAuthenticated() throws Exception {
		User user = new User();
		when( userService.getUserByUsername( "abergin" ) ).thenReturn( user );
		Authentication
				authentication = ldapAuthenticationProvider.authenticate(
				new UsernamePasswordAuthenticationToken( "abergin", "inflict" ) );
		assertNotNull( authentication );
		assertTrue( authentication.getPrincipal() instanceof LdapUserDetails );
		LdapUserDetails details = (LdapUserDetails) authentication.getPrincipal();
		assertEquals( "abergin", details.getUsername() );
		assertEquals( "inflict", details.getPassword() );
	}

	@Configuration
	@Import(LdapDirectorySettingsConfiguration.class)
	protected static class Config
	{
		@Bean
		public LdapAuthenticationProvider ldapAuthenticationProvider() {
			LdapConnector ldapConnector = new LdapConnector();
			ldapConnector.setId( 1L );
			ldapConnector.setUsername( "uid=admin,ou=system" );
			ldapConnector.setPassword( "secret" );
			ldapConnector.setBaseDn( "dc=foreach,dc=com" );
			ldapConnector.setHostName( "127.0.0.1" );
			ldapConnector.setAdditionalUserDn( "ou=People" );
			ldapConnector.setPort( 53389 );
			ldapConnector.setLdapConnectorType( LdapConnectorType.OPENDS );

			LdapAuthenticationProvider ldapAuthenticationProvider = new LdapAuthenticationProvider();
			ldapAuthenticationProvider.setUserService( userService() );
			ldapAuthenticationProvider.setLdapContextSource( ldapConnector );
			return ldapAuthenticationProvider;
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
