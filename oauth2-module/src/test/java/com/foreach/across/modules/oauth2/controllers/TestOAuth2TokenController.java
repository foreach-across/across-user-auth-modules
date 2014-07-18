package com.foreach.across.modules.oauth2.controllers;

import com.foreach.across.modules.oauth2.dto.OAuth2TokenDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestOAuth2TokenController.Configuration.class })
public class TestOAuth2TokenController
{

	@Autowired
	private TokenStore tokenStore;

	@Autowired
	private OAuth2TokenController oAuth2TokenController;

	@Before
	public void setup() {
		reset( tokenStore );
	}

	@Test
	public void deleteWithNull() {
		ResponseEntity<OAuth2TokenDto> responseEntity = oAuth2TokenController.invalidateToken( null );
		verify( tokenStore, never() ).readAccessToken( anyString() );
		verify( tokenStore, never() ).readRefreshToken( anyString() );
		OAuth2TokenDto body = responseEntity.getBody();
		assertNull( body.getValue() );
	}

	@Test
	public void deleteWithAccessToken() {
		String tokenValue = "FRG65SS";
		DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken( tokenValue );
		when( tokenStore.readAccessToken( tokenValue ) ).thenReturn( token );
		ResponseEntity<OAuth2TokenDto> responseEntity = oAuth2TokenController.invalidateToken( tokenValue );
		OAuth2TokenDto body = responseEntity.getBody();
		assertEquals( tokenValue, body.getValue() );
		verify( tokenStore ).removeAccessToken( token );
		verify( tokenStore, never() ).readRefreshToken( anyString() );
		verify( tokenStore, times( 1 ) ).removeRefreshToken( (OAuth2RefreshToken) anyObject() );
		verify( tokenStore, times( 1 ) ).removeAccessToken( eq( token ) );
	}

	@Test
	public void deleteWithRefreshToken() {
		String tokenValue = "FRG65SS";
		DefaultOAuth2RefreshToken token = new DefaultOAuth2RefreshToken( tokenValue );
		when( tokenStore.readRefreshToken( tokenValue ) ).thenReturn( token );
		ResponseEntity<OAuth2TokenDto> responseEntity = oAuth2TokenController.invalidateToken( tokenValue );
		OAuth2TokenDto body = responseEntity.getBody();
		assertEquals( tokenValue, body.getValue() );
		verify( tokenStore, never() ).removeAccessToken( (OAuth2AccessToken) anyObject() );
		verify( tokenStore ).removeRefreshToken( token );
	}

	@org.springframework.context.annotation.Configuration
	protected static class Configuration
	{
		@Bean
		public OAuth2TokenController oAuth2TokenController() {
			return new OAuth2TokenController();
		}

		@Bean
		public TokenStore tokenStore() {
			return mock( TokenStore.class );
		}

		@Bean
		public UserDetailsService userDetailsService() {
			return mock( UserDetailsService.class );
		}

		@Bean
		public AuthorizationServerTokenServices authorizationServerTokenServices() {
			return mock( AuthorizationServerTokenServices.class );
		}
	}
}
