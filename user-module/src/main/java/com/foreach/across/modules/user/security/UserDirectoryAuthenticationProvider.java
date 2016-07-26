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

package com.foreach.across.modules.user.security;

import com.foreach.across.modules.hibernate.aop.EntityInterceptorAdapter;
import com.foreach.across.modules.user.business.UserDirectory;
import com.foreach.across.modules.user.services.UserDirectoryService;
import com.foreach.across.modules.user.services.UserDirectoryServiceProvider;
import com.foreach.across.modules.user.services.UserDirectoryServiceProviderManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * {@link AuthenticationProvider} implementation that delegates to other {@link AuthenticationProvider} instances
 * that represent a single {@link com.foreach.across.modules.user.business.UserDirectory}.  The different providers
 * will be tried in the directory order until a provider returns a non-null authentication.
 *
 * @author Arne Vandamme
 * @see com.foreach.across.modules.user.services.UserDirectoryServiceProviderManager
 * @since 2.0.0
 */
// TODO: hook up to entity modified event once available
@Component
public class UserDirectoryAuthenticationProvider extends EntityInterceptorAdapter<UserDirectory> implements AuthenticationProvider
{
	private final UserDirectoryService userDirectoryService;
	private final UserDirectoryServiceProviderManager userDirectoryServiceProviderManager;

	private List<AuthenticationProvider> directoryAuthenticationProviders;

	@Autowired
	public UserDirectoryAuthenticationProvider( UserDirectoryService userDirectoryService,
	                                            UserDirectoryServiceProviderManager userDirectoryServiceProviderManager ) {
		this.userDirectoryService = userDirectoryService;
		this.userDirectoryServiceProviderManager = userDirectoryServiceProviderManager;
	}

	@Override
	public Authentication authenticate( Authentication authentication ) throws AuthenticationException {
		Collection<AuthenticationProvider> providers = retrieveDirectoryAuthenticationProviders();

		for ( AuthenticationProvider provider : providers ) {
			Authentication auth = provider.authenticate( authentication );
			if ( auth != null ) {
				return auth;
			}
		}

		throw new BadCredentialsException( "Bad credentials" );
	}

	private Collection<AuthenticationProvider> retrieveDirectoryAuthenticationProviders() {
		if ( directoryAuthenticationProviders == null ) {
			List<AuthenticationProvider> authenticationProviders = new ArrayList<>();
			userDirectoryService.getActiveUserDirectories().forEach(
					dir -> {
						UserDirectoryServiceProvider serviceProvider
								= userDirectoryServiceProviderManager.getServiceProvider( dir );
						if ( serviceProvider != null ) {
							AuthenticationProvider authenticationProvider
									= serviceProvider.getAuthenticationProvider( dir );
							if ( authenticationProvider != null ) {
								authenticationProviders.add( authenticationProvider );
							}
						}
					}
			);
			directoryAuthenticationProviders = authenticationProviders;
		}

		return directoryAuthenticationProviders;
	}

	@Override
	public boolean supports( Class<?> authentication ) {
		return ( UsernamePasswordAuthenticationToken.class.isAssignableFrom( authentication ) );
	}

	/**
	 * Forces a reload of the internal directory authentication providers.
	 * This method should be called whenever directories have been modified or directory ordering has changed.
	 */
	public void reload() {
		directoryAuthenticationProviders = null;
	}

	@Override
	public boolean handles( Class<?> entityClass ) {
		return UserDirectory.class.isAssignableFrom( entityClass );
	}

	@Override
	public void beforeUpdate( UserDirectory entity ) {
		reload();
	}

	@Override
	public void afterDelete( UserDirectory entity ) {
		reload();
	}

	@Override
	public void afterCreate( UserDirectory entity ) {
		reload();
	}

	@Override
	public void afterDeleteAll( Class<?> entityClass ) {
		reload();
	}
}
