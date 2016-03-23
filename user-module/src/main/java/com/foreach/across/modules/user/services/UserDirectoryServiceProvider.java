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

import com.foreach.across.modules.user.business.BasicSecurityPrincipal;
import com.foreach.across.modules.user.business.UserDirectory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.validation.Validator;

/**
 * SPI for custom {@link UserDirectory} implementations that have their own authentication and entity validation rules.
 * The {@link UserDirectoryServiceProviderManager} is responsible for keeping track of the different provider instances
 * and providing access to them.
 *
 * @author Arne Vandamme
 * @see UserDirectoryServiceProviderManager
 * @since 1.2.0
 */
public interface UserDirectoryServiceProvider
{
	/**
	 * Returns {@code true} if the {@link UserDirectory} implementation type is supported by this provider.
	 *
	 * @param userDirectoryClass user directory implementation
	 * @return {@code true} if the user directory type is supported
	 */
	boolean supports( Class<? extends UserDirectory> userDirectoryClass );

	/**
	 * Get the {@link AuthenticationProvider} required for authenticating principals for this directory.
	 *
	 * @param userDirectory instance
	 * @return provider instance
	 */
	AuthenticationProvider getAuthenticationProvider( UserDirectory userDirectory );

	Validator getValidator( UserDirectory userDirectory,
	                        Class<? extends BasicSecurityPrincipal> securityPrincipalClass );
}
