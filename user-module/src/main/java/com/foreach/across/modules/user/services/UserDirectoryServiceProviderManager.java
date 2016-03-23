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

package com.foreach.across.modules.user.services;

import com.foreach.across.modules.user.business.UserDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collection;

/**
 * Tracks different {@link UserDirectoryServiceProvider} beans and will return the specific provider that
 * matches a {@link UserDirectory}.
 *
 * @author Arne Vandamme
 * @since 1.2.0
 */
@Service
public class UserDirectoryServiceProviderManager
{
	private final Collection<UserDirectoryServiceProvider> serviceProviders;

	@Autowired
	public UserDirectoryServiceProviderManager( Collection<UserDirectoryServiceProvider> userDirectoryServiceProviders ) {
		this.serviceProviders = userDirectoryServiceProviders;
	}

	/**
	 * Alternative for {@link #getServiceProvider(Class)} with a non-null instance.
	 * This method can return {@code null} if no provider is available, but this usually
	 * constitutes a configuration problem.
	 *
	 * @param userDirectory instance
	 * @return provider or {@code null} if none available
	 */
	public UserDirectoryServiceProvider getServiceProvider( UserDirectory userDirectory ) {
		Assert.notNull( userDirectory );
		return getServiceProvider( userDirectory.getClass() );
	}

	/**
	 * Get a {@link UserDirectoryServiceProvider} for the given user directory type.
	 * This method can return {@code null} if no provider is available, but this usually
	 * constitutes a configuration problem.
	 *
	 * @param userDirectoryClass type of the user directory
	 * @return provider or {@code null} if none available
	 */
	public UserDirectoryServiceProvider getServiceProvider( Class<? extends UserDirectory> userDirectoryClass ) {
		for ( UserDirectoryServiceProvider serviceProvider : serviceProviders ) {
			if ( serviceProvider.supports( userDirectoryClass ) ) {
				return serviceProvider;
			}
		}
		return null;
	}
}
