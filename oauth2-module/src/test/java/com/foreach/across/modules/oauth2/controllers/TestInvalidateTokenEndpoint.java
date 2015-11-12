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

import com.foreach.across.modules.oauth2.dto.OAuth2TokenDto;
import com.foreach.common.test.MockedLoader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.Serializable;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockedLoader.class, classes = { TestInvalidateTokenEndpoint.Config.class })
public class TestInvalidateTokenEndpoint
{
	@Autowired
	private TokenStore tokenStore;

	@Autowired
	private InvalidateTokenEndpoint invalidateTokenEndpoint;

	private OAuth2Request oAuth2Request;
	private OAuth2Authentication authentication;

	@Before
	public void setUp() {
		reset( tokenStore );

		authentication = mock( OAuth2Authentication.class );

		oAuth2Request = new OAuth2Request( Collections.<String, String>emptyMap(),
		                                   "testClient",
		                                   Collections.<GrantedAuthority>emptyList(),
		                                   true,
		                                   Collections.singleton( "full" ),
		                                   Collections.singleton( "someresource" ),
		                                   "",
		                                   Collections.<String>emptySet(),
		                                   Collections.<String, Serializable>emptyMap() );

		when( authentication.getOAuth2Request() ).thenReturn( oAuth2Request );
		when( tokenStore.readAuthentication( any( OAuth2AccessToken.class ) ) ).thenReturn( authentication );
		when( tokenStore.readAuthenticationForRefreshToken( any( OAuth2RefreshToken.class ) ) ).thenReturn(
				authentication );
	}

	@Test
	public void deleteWithNull() {
		ResponseEntity<OAuth2TokenDto> responseEntity = invalidateTokenEndpoint.invalidateToken( authentication, null );
		verify( tokenStore, never() ).readAccessToken( anyString() );
		verify( tokenStore, never() ).readRefreshToken( anyString() );
		OAuth2TokenDto body = responseEntity.getBody();
		assertNull( body.getValue() );
	}

	@Test
	public void deleteWithAccessToken() {
		String tokenValue = "FRG65SS";
		DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken( tokenValue );
		token.setRefreshToken( new DefaultOAuth2RefreshToken( "refresh" ) );

		when( tokenStore.readAccessToken( tokenValue ) ).thenReturn( token );
		ResponseEntity<OAuth2TokenDto> responseEntity = invalidateTokenEndpoint.invalidateToken( authentication,
		                                                                                         tokenValue );
		OAuth2TokenDto body = responseEntity.getBody();
		assertEquals( tokenValue, body.getValue() );
		verify( tokenStore ).removeAccessToken( token );
		verify( tokenStore, never() ).readRefreshToken( anyString() );
		verify( tokenStore, times( 1 ) ).removeRefreshToken( token.getRefreshToken() );
		verify( tokenStore, times( 1 ) ).removeAccessToken( eq( token ) );
	}

	@Test
	public void deleteWithRefreshToken() {
		String tokenValue = "FRG65SS";
		DefaultOAuth2RefreshToken token = new DefaultOAuth2RefreshToken( tokenValue );
		when( tokenStore.readRefreshToken( tokenValue ) ).thenReturn( token );
		ResponseEntity<OAuth2TokenDto> responseEntity = invalidateTokenEndpoint.invalidateToken( authentication,
		                                                                                         tokenValue );
		OAuth2TokenDto body = responseEntity.getBody();
		assertEquals( tokenValue, body.getValue() );
		verify( tokenStore, never() ).removeAccessToken( (OAuth2AccessToken) anyObject() );
		verify( tokenStore ).removeRefreshToken( token );
	}

	@Test
	public void clientCannotInvalidateTokenFromDifferentClient() {

	}

	@Configuration
	protected static class Config
	{

		@Bean
		public InvalidateTokenEndpoint oAuth2TokenController() {
			return new InvalidateTokenEndpoint( new NoOpCacheManager() );
		}
	}
}
