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
package com.foreach.across.modules.oauth2.services;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

public class CustomTokenServices extends DefaultTokenServices
{
	private TokenStore tokenStore;

	@Override
	public synchronized OAuth2AccessToken createAccessToken(
			OAuth2Authentication authentication) throws AuthenticationException {
		// https://github.com/spring-projects/spring-security-oauth/issues/276
		return super.createAccessToken(authentication);
	}

	@Override
	public synchronized OAuth2AccessToken refreshAccessToken(
			String refreshTokenValue, TokenRequest request) {
		// https://github.com/spring-projects/spring-security-oauth/issues/276
		return super.refreshAccessToken(refreshTokenValue, request);
	}

	@Override
	public void setTokenStore( TokenStore tokenStore ) {
		super.setTokenStore( tokenStore );
		this.tokenStore = tokenStore;
	}

	@Override
	public OAuth2Authentication loadAuthentication( String accessTokenValue ) throws AuthenticationException {
		try {
			return super.loadAuthentication( accessTokenValue );
		}
		catch ( RemoveTokenException removeTokenException ) {
			// When the username is changed or the clientId is changed, we remove the access token so we get an invalid token exception later on
			OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken( accessTokenValue );
			if ( oAuth2AccessToken != null ) {
				tokenStore.removeAccessToken( oAuth2AccessToken );
			}
		}
		return null;
	}
}
