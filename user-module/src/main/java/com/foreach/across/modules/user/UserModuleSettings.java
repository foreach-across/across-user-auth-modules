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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.crypto.password.PasswordEncoder;

@ConfigurationProperties("userModule")
@SuppressWarnings("unused")
public class UserModuleSettings
{
	public static final String PASSWORD_ENCODER = "userModule.passwordEncoder";
	public static final String USE_EMAIL_AS_USERNAME = "userModule.useEmailAsUsername";
	public static final String REQUIRE_EMAIL_UNIQUE = "userModule.requireEmailUnique";
	public static final String ENABLE_DEFAULT_ACLS = "userModule.enableDefaultAcls";

	/**
	 * Optional PasswordEncoder instance to be used.
	 */
	private PasswordEncoder passwordEncoder;

	/**
	 * Specifies whether to use the email for login and registration instead of username.
	 */
	private boolean useEmailAsUsername;

	/**
	 * Specifies whether the email field is unique, must be true when useEmailAsUsername is true.
	 */
	private boolean requireEmailUnique;

	/**
	 * Specifies whether default ACLs should be created via interceptors, such as for Groups.
	 */
	private boolean enableDefaultAcls;

	/**
	 * Default SpEL expression (eg. property) for the display label of a User entity.
	 */
	private String userLabelExpression = "label";

	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	public void setPasswordEncoder( PasswordEncoder passwordEncoder ) {
		this.passwordEncoder = passwordEncoder;
	}

	public boolean isUseEmailAsUsername() {
		return useEmailAsUsername;
	}

	public void setUseEmailAsUsername( boolean useEmailAsUsername ) {
		this.useEmailAsUsername = useEmailAsUsername;
	}

	public boolean isRequireEmailUnique() {
		return requireEmailUnique || isUseEmailAsUsername();
	}

	public void setRequireEmailUnique( boolean requireEmailUnique ) {
		this.requireEmailUnique = requireEmailUnique;
	}

	public boolean isEnableDefaultAcls() {
		return enableDefaultAcls;
	}

	public void setEnableDefaultAcls( boolean enableDefaultAcls ) {
		this.enableDefaultAcls = enableDefaultAcls;
	}

	public String getUserLabelExpression() {
		return userLabelExpression;
	}

	public void setUserLabelExpression( String userLabelExpression ) {
		this.userLabelExpression = userLabelExpression;
	}
}