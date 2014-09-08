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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Additional OAuth endpoint for invalidating an access token.
 *
 * @author Andy Somers, Arne Vandamme
 */
@FrameworkEndpoint
public class InvalidateTokenEndpoint
{
	@Autowired
	private TokenStore tokenStore;

	@RequestMapping("/oauth/invalidate")
	public ResponseEntity<OAuth2TokenDto> invalidateToken(
			OAuth2Authentication authentication,
			@RequestParam(value = "access_token") String token
	) {
		OAuth2TokenDto response = new OAuth2TokenDto( token );
		OAuth2Authentication storedAuthentication = null;

		if ( StringUtils.isNotEmpty( token ) ) {
			OAuth2AccessToken accessToken = tokenStore.readAccessToken( token );
			if ( accessToken != null ) {
				storedAuthentication = tokenStore.readAuthentication( accessToken );

				if ( storedAuthentication != null && StringUtils.equals(
						authentication.getOAuth2Request().getClientId(),
						storedAuthentication.getOAuth2Request().getClientId() ) ) {
					OAuth2RefreshToken refreshToken = accessToken.getRefreshToken();
					tokenStore.removeAccessToken( accessToken );
					if ( refreshToken != null ) {
						tokenStore.removeRefreshToken( refreshToken );
					}
				}
				else {
					// Trying to invalidate token from another client, not allowed
					return new ResponseEntity<>( HttpStatus.FORBIDDEN );
				}
			}
			else {
				OAuth2RefreshToken refreshToken = tokenStore.readRefreshToken( token );
				if ( refreshToken != null ) {
					storedAuthentication = tokenStore.readAuthenticationForRefreshToken( refreshToken );

					if ( storedAuthentication != null && StringUtils.equals(
							authentication.getOAuth2Request().getClientId(),
							storedAuthentication.getOAuth2Request().getClientId() ) ) {
						tokenStore.removeRefreshToken( refreshToken );
					}
					else {
						return new ResponseEntity<>( HttpStatus.FORBIDDEN );
					}
				}
			}
		}

		if ( authentication.equals( storedAuthentication ) ) {
			SecurityContextHolder.clearContext();
		}

		return new ResponseEntity<>( response, HttpStatus.OK );
	}

}
