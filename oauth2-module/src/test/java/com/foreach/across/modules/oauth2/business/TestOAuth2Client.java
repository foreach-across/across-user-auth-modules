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
package com.foreach.across.modules.oauth2.business;

import com.foreach.across.modules.user.business.Group;
import com.foreach.across.modules.user.business.Role;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * @author Arne Vandamme
 */
public class TestOAuth2Client
{
	@Test
	public void nullValuesAreSameAsEmpty() {
		OAuth2Client client = new OAuth2Client();
		client.setRoles( Collections.singleton( new Role( "one" ) ) );
		client.setAuthorizedGrantTypes( Collections.singleton( "semi" ) );
		client.setRegisteredRedirectUri( Collections.singleton( "http://urltwo" ) );
		client.setResourceIds( Collections.singleton( "movies" ) );
		client.setOAuth2ClientScopes( Collections.singleton( new OAuth2ClientScope() ) );

		assertFalse( client.getRoles().isEmpty() );
		assertFalse( client.getAuthorizedGrantTypes().isEmpty() );
		assertFalse( client.getRegisteredRedirectUri().isEmpty() );
		assertFalse( client.getResourceIds().isEmpty() );
		assertFalse( client.getOAuth2ClientScopes().isEmpty() );

		client.setRoles( null );
		assertTrue( client.getRoles().isEmpty() );
		assertFalse( client.getAuthorizedGrantTypes().isEmpty() );
		assertFalse( client.getRegisteredRedirectUri().isEmpty() );
		assertFalse( client.getResourceIds().isEmpty() );
		assertFalse( client.getOAuth2ClientScopes().isEmpty() );

		client.setAuthorizedGrantTypes( null );
		assertTrue( client.getRoles().isEmpty() );
		assertTrue( client.getAuthorizedGrantTypes().isEmpty() );
		assertFalse( client.getRegisteredRedirectUri().isEmpty() );
		assertFalse( client.getResourceIds().isEmpty() );
		assertFalse( client.getOAuth2ClientScopes().isEmpty() );

		client.setRegisteredRedirectUri( null );
		assertTrue( client.getRoles().isEmpty() );
		assertTrue( client.getAuthorizedGrantTypes().isEmpty() );
		assertTrue( client.getRegisteredRedirectUri().isEmpty() );
		assertFalse( client.getResourceIds().isEmpty() );
		assertFalse( client.getOAuth2ClientScopes().isEmpty() );

		client.setResourceIds( null );
		assertTrue( client.getRoles().isEmpty() );
		assertTrue( client.getAuthorizedGrantTypes().isEmpty() );
		assertTrue( client.getRegisteredRedirectUri().isEmpty() );
		assertTrue( client.getResourceIds().isEmpty() );
		assertFalse( client.getOAuth2ClientScopes().isEmpty() );

		client.setOAuth2ClientScopes( null );
		assertTrue( client.getRoles().isEmpty() );
		assertTrue( client.getAuthorizedGrantTypes().isEmpty() );
		assertTrue( client.getRegisteredRedirectUri().isEmpty() );
		assertTrue( client.getResourceIds().isEmpty() );
		assertTrue( client.getOAuth2ClientScopes().isEmpty() );
	}

	@Test
	public void oauth2ClientDto() {
		OAuth2Client client = new OAuth2Client();
		client.setId( 123L );
		client.setClientId( "client" );
		client.setAuthorizedGrantTypes( Arrays.asList( "full", "semi" ) );
		client.setRegisteredRedirectUri( Arrays.asList( "http://urlone", "http://urltwo" ) );
		client.setResourceIds( Arrays.asList( "movies", "music" ) );
		client.setRoles( Arrays.asList( new Role( "role one" ), new Role( "role two" ) ) );

		OAuth2ClientScope clientScopeOne = new OAuth2ClientScope();
		clientScopeOne.setPk( new OAuth2ClientScopeId() );

		OAuth2ClientScope clientScopeTwo = new OAuth2ClientScope();
		clientScopeTwo.setPk( new OAuth2ClientScopeId() );

		client.setOAuth2ClientScopes( Arrays.asList( clientScopeOne, clientScopeTwo ) );

		OAuth2Client dto = client.toDto();
		assertEquals( client, dto );
		assertEquals( client.getClientId(), dto.getClientId() );
		assertEquals( client.getRoles(), dto.getRoles() );
		assertNotSame( client.getRoles(), dto.getRoles() );
		assertEquals( client.getAuthorizedGrantTypes(), dto.getAuthorizedGrantTypes() );
		assertNotSame( client.getAuthorizedGrantTypes(), dto.getAuthorizedGrantTypes() );
		assertEquals( client.getRegisteredRedirectUri(), dto.getRegisteredRedirectUri() );
		assertNotSame( client.getRegisteredRedirectUri(), dto.getRegisteredRedirectUri() );
		assertEquals( client.getResourceIds(), dto.getResourceIds() );
		assertNotSame( client.getResourceIds(), dto.getResourceIds() );
		assertEquals( client.getOAuth2ClientScopes(), dto.getOAuth2ClientScopes() );
		assertNotSame( client.getOAuth2ClientScopes(), dto.getOAuth2ClientScopes() );
	}

	@Ignore
	@Test
	public void principalNameIsAlwaysLowerCased() throws Exception {
		OAuth2Client client = new OAuth2Client();
		assertNull( client.getPrincipalName() );
		assertNull( client.getClientId() );

		client.setClientId( "My Client Id" );

		assertEquals( "My Client Id", client.getClientId() );
		assertEquals( "my client id", client.getPrincipalName() );

		Field principalName = ReflectionUtils.findField( OAuth2Client.class, "principalName" );
		principalName.setAccessible( true );
		principalName.set( client, "CLIENT PRINCIPAL_NAME" );

		assertEquals( "client principal_name", client.getPrincipalName() );
	}
}
