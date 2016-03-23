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
import com.foreach.across.modules.user.business.InternalUserDirectory;
import com.foreach.across.modules.user.business.UserDirectory;
import com.foreach.across.modules.user.security.InternalUserDirectoryAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Validator;

/**
 * Default {@link UserDirectoryServiceProvider} that supports the
 * {@link com.foreach.across.modules.user.business.InternalUserDirectory}.
 *
 * @author Arne Vandamme
 * @since 1.2.0
 */
public class InternalUserDirectoryServiceProvider implements UserDirectoryServiceProvider
{
	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder userPasswordEncoder;

	@Override
	public boolean supports( Class<? extends UserDirectory> userDirectoryClass ) {
		return InternalUserDirectory.class.isAssignableFrom( userDirectoryClass );
	}

	@Override
	public AuthenticationProvider getAuthenticationProvider( UserDirectory userDirectory ) {
		try {
			InternalUserDirectoryAuthenticationProvider authenticationProvider = new InternalUserDirectoryAuthenticationProvider();
			authenticationProvider.setPasswordEncoder( userPasswordEncoder );
			authenticationProvider.setUserDirectory( userDirectory );
			authenticationProvider.setUserService( userService );
			authenticationProvider.afterPropertiesSet();

			return authenticationProvider;
		}
		catch ( Exception e ) {
			throw new RuntimeException( e );
		}
	}

	@Override
	public Validator getValidator( UserDirectory userDirectory,
	                               Class<? extends BasicSecurityPrincipal> securityPrincipalClass ) {
		return null;
	}
}
