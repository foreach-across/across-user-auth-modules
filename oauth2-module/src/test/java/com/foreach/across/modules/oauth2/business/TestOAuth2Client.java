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

import com.foreach.across.modules.user.business.Role;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * @author Arne Vandamme
 */
public class TestOAuth2Client
{
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
}
