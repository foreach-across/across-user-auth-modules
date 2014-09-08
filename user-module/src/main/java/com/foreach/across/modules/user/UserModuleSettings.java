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
package com.foreach.across.modules.user;

import com.foreach.across.core.AcrossModuleSettings;
import com.foreach.across.core.AcrossModuleSettingsRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserModuleSettings extends AcrossModuleSettings
{
	/**
	 * Optional PasswordEncoder instance to be used.
	 * <p/>
	 * PasswordEncoder instance
	 *
	 * @see org.springframework.security.crypto.password.PasswordEncoder
	 */
	public static final String PASSWORD_ENCODER = "userModule.passwordEncoder";

	/**
	 * Specifies whether to use the email for login and registration instead of username.
	 * <p/>
	 * True/False
	 */
	public static final String USE_EMAIL_AS_USERNAME = "userModule.useEmailAsUsername";

	/**
	 * Specifies whether the email field is unique, must be true when useEmailAsUsername is True.
	 * <p/>
	 * True/False
	 */
	public static final String REQUIRE_EMAIL_UNIQUE = "userModule.requireEmailUnique";

	@Override
	protected void registerSettings( AcrossModuleSettingsRegistry registry ) {
		registry.register( PASSWORD_ENCODER, PasswordEncoder.class, null,
		                   "PasswordEncoder instance that should be used." );
		registry.register( USE_EMAIL_AS_USERNAME, Boolean.class, false,
		                   "Specifies whether to use the email for login and registration instead of username." );
		registry.register( REQUIRE_EMAIL_UNIQUE, Boolean.class, false,
		                   "Specifies whether the email field is unique, must be true when useEmailAsUsername is True." );
	}

	public boolean isUseEmailAsUsername() {
		return getProperty( USE_EMAIL_AS_USERNAME, Boolean.class );
	}

	public boolean isRequireUniqueEmail() {
		return getProperty( REQUIRE_EMAIL_UNIQUE, Boolean.class );
	}
}