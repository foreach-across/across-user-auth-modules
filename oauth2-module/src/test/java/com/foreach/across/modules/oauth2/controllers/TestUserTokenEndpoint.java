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
package com.foreach.across.modules.oauth2.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.io.Serializable;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
@RunWith(MockitoJUnitRunner.class)
public class TestUserTokenEndpoint
{
	@Mock
	private UserDetailsService userDetailsService;

	@Mock
	private AuthorizationServerTokenServices authorizationServerTokenServices;

	@InjectMocks
	private UserTokenEndpoint endpoint;

	private UserDetails user;
	private OAuth2Request oAuth2Request;
	private OAuth2Authentication authentication;
	private Set<String> allowedScopes;

	@Before
	public void setUp() {
		user = mock( UserDetails.class );
		when( user.isAccountNonExpired() ).thenReturn( true );
		when( user.isAccountNonLocked() ).thenReturn( true );
		when( user.isCredentialsNonExpired() ).thenReturn( true );
		when( user.isEnabled() ).thenReturn( true );

		authentication = mock( OAuth2Authentication.class );

		allowedScopes = new HashSet<>();
		allowedScopes.add( "full" );

		oAuth2Request = new OAuth2Request( Collections.<String, String>emptyMap(),
		                                   "testClient",
		                                   Collections.<GrantedAuthority>emptyList(),
		                                   true,
		                                   allowedScopes,
		                                   Collections.singleton( "someresource" ),
		                                   "",
		                                   Collections.<String>emptySet(),
		                                   Collections.<String, Serializable>emptyMap() );

		when( authentication.getOAuth2Request() ).thenReturn( oAuth2Request );

		when( userDetailsService.loadUserByUsername( anyString() ) ).thenReturn( user );
	}

	@Test
	public void requestingPrincipalMustHaveTheRightsForTheRequestedScopes() {
		allowedScopes.add( "writeOnly" );

		ResponseEntity<Map<String, String>> response = endpoint.createUserToken( authentication, "someuser",
		                                                                         "full writeonly readonly" );

		assertEquals( HttpStatus.FORBIDDEN, response.getStatusCode() );
		assertEquals( "Scope \"readonly\" is not allowed with the current access token.",
		              response.getBody().get( "error" ) );
	}

	@Test
	public void userMustBeAllowedToLogonToCreateAToken() {
		when( user.isEnabled() ).thenReturn( false );

		ResponseEntity<Map<String, String>> response = endpoint.createUserToken( authentication, "someuser", "full" );

		assertEquals( HttpStatus.FORBIDDEN, response.getStatusCode() );
		assertEquals( "Requested user is not allowed to authenticate.", response.getBody().get( "error" ) );
	}

	@Test
	public void nonExistingUserReturnsForbidden() {
		when( userDetailsService.loadUserByUsername( "unknown" ) ).thenThrow( new UsernameNotFoundException(
				"username not found" ) );

		ResponseEntity<Map<String, String>> response = endpoint.createUserToken( authentication, "unknown", "full" );

		verify( userDetailsService ).loadUserByUsername( "unknown" );

		assertEquals( HttpStatus.FORBIDDEN, response.getStatusCode() );
		assertEquals( "Requested user does not exist.", response.getBody().get( "error" ) );
	}

	@Test
	public void validUserTokenRequest() {
		allowedScopes.add( "writeOnly" );

		OAuth2Request clientAuthentication = new OAuth2Request(
				oAuth2Request.getRequestParameters(),
				oAuth2Request.getClientId(),
				Collections.<GrantedAuthority>emptyList(),
				true,
				Collections.singleton( "full" ),
				oAuth2Request.getResourceIds(),
				oAuth2Request.getRedirectUri(),
				oAuth2Request.getResponseTypes(),
				oAuth2Request.getExtensions()
		);

		Authentication userAuthentication = new PreAuthenticatedAuthenticationToken( user, null,
		                                                                             user.getAuthorities() );

		OAuth2Authentication tokenAuth = new OAuth2Authentication( clientAuthentication, userAuthentication );

		OAuth2AccessToken token = mock( OAuth2AccessToken.class );
		when( token.getValue() ).thenReturn( "bla" );
		when( token.getTokenType() ).thenReturn( "bearer" );
		when( token.getExpiresIn() ).thenReturn( 123 );
		when( token.getScope() ).thenReturn( Collections.singleton( "full" ) );

		when( authorizationServerTokenServices.createAccessToken( tokenAuth ) ).thenReturn( token );

		ResponseEntity<Map<String, String>> response = endpoint.createUserToken( authentication, "someuser", "full" );

		assertEquals( HttpStatus.OK, response.getStatusCode() );
		Map<String, String> expectedValues = new HashMap<>();
		expectedValues.put( "token_type", "bearer" );
		expectedValues.put( "access_token", "bla" );
		expectedValues.put( "expires_in", "123" );
		expectedValues.put( "scope", "full" );
		assertEquals( expectedValues, response.getBody() );

	}
}
