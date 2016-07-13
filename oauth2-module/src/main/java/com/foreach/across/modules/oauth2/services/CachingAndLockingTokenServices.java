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

import com.foreach.across.modules.oauth2.OAuth2ModuleCache;
import com.foreach.common.concurrent.locks.CloseableObjectLock;
import com.foreach.common.concurrent.locks.ObjectLockRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * Custom extension of {@link DefaultTokenServices} that uses a cache for access token based authentication lookups
 * and applies locking to token creation if a {@link com.foreach.common.concurrent.locks.ObjectLockRepository} is set.
 */
@Transactional
public class CachingAndLockingTokenServices extends DefaultTokenServices
{
	private final Cache cache;

	private TokenStore tokenStore;
	private ObjectLockRepository<String> objectLockRepository;

	public CachingAndLockingTokenServices( CacheManager cacheManager ) {
		cache = cacheManager.getCache( OAuth2ModuleCache.ACCESS_TOKENS_TO_AUTHENTICATION );
	}

	public void setObjectLockRepository( ObjectLockRepository<String> objectLockRepository ) {
		this.objectLockRepository = objectLockRepository;
	}

	@Override
	public OAuth2AccessToken createAccessToken( OAuth2Authentication authentication ) throws AuthenticationException {
		if ( objectLockRepository != null ) {
			try (CloseableObjectLock ignore
					     = objectLockRepository.lock( Objects.toString( authentication.getPrincipal() ) )) {
				return super.createAccessToken( authentication );
			}
		}
		else {
			return super.createAccessToken( authentication );
		}
	}

	@Override
	public OAuth2AccessToken refreshAccessToken( String refreshTokenValue, TokenRequest request ) {
		try {
			if ( objectLockRepository != null ) {
				try (CloseableObjectLock ignore
						     = objectLockRepository.lock( refreshTokenValue )) {
					return super.refreshAccessToken( refreshTokenValue, request );
				}
			}
			else {
				return super.refreshAccessToken( refreshTokenValue, request );
			}
		}
		catch ( RemoveTokenException e ) {
			throw new InvalidGrantException( "User is invalid" );
		}
	}

	@Override
	public void setTokenStore( TokenStore tokenStore ) {
		super.setTokenStore( tokenStore );
		this.tokenStore = tokenStore;
	}

	@Override
	public OAuth2Authentication loadAuthentication( String accessTokenValue ) throws AuthenticationException {
		try {
			Cache.ValueWrapper valueWrapper = cache.get( accessTokenValue );
			if ( valueWrapper == null || valueWrapper.get() == null ) {
				OAuth2Authentication oAuth2Authentication = super.loadAuthentication( accessTokenValue );
				cache.put( accessTokenValue, oAuth2Authentication );
				return oAuth2Authentication;
			}
			return (OAuth2Authentication) valueWrapper.get();
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
