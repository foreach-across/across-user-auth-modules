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