package com.foreach.across.modules.user;

public class UserModuleSettings
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

	protected UserModuleSettings() {
	}
}