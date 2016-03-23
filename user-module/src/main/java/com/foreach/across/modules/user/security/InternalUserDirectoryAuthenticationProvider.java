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

import com.foreach.across.modules.user.business.InternalUserDirectory;
import com.foreach.across.modules.user.business.User;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.PlaintextPasswordEncoder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

/**
 * Authenticates a user against a {@link InternalUserDirectory}.
 *
 * @author Arne Vandamme
 * @since 1.2.0
 */
public class InternalUserDirectoryAuthenticationProvider extends AbstractUserInDirectoryAuthenticationProvider
{
	/**
	 * The plaintext password used to perform {@link PasswordEncoder#isPasswordValid(String, String, Object)} on when the user is
	 * not found to avoid SEC-2056.
	 */
	private static final String USER_NOT_FOUND_PASSWORD = "userNotFoundPassword";

	//~ Instance fields ================================================================================================

	private PasswordEncoder passwordEncoder;

	/**
	 * The password used to perform {@link PasswordEncoder#isPasswordValid(String, String, Object)} on when the user is
	 * not found to avoid SEC-2056. This is necessary, because some {@link PasswordEncoder} implementations will short circuit if the
	 * password is not in a valid format.
	 */
	private String userNotFoundEncodedPassword;

	private SaltSource saltSource;

	public InternalUserDirectoryAuthenticationProvider() {
		setPasswordEncoder( new PlaintextPasswordEncoder() );
	}

	@Override
	protected void doAfterPropertiesSet() throws Exception {
		Assert.isTrue( userDirectory instanceof InternalUserDirectory,
		               "Only InternalUserDirectory types are supported" );
	}

	@Override
	protected UserDetails buildUserDetails( User user,
	                                        UsernamePasswordAuthenticationToken authentication ) throws AuthenticationException {
		// todo: passwordencoder, check hash etc
		return user;
	}

	@Override
	@SuppressWarnings("deprecation")
	protected void additionalAuthenticationChecks( UserDetails userDetails,
	                                               UsernamePasswordAuthenticationToken authentication ) throws AuthenticationException {
		Object salt = null;

		if ( this.saltSource != null ) {
			salt = this.saltSource.getSalt( userDetails );
		}

		if ( authentication.getCredentials() == null ) {
			LOG.debug( "Authentication failed: no credentials provided" );

			throw new BadCredentialsException( messages.getMessage(
					"AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials" ), userDetails );
		}

		String presentedPassword = authentication.getCredentials().toString();

		if ( !passwordEncoder.isPasswordValid( userDetails.getPassword(), presentedPassword, salt ) ) {
			LOG.debug( "Authentication failed: password does not match stored value" );

			throw new BadCredentialsException( messages.getMessage(
					"AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials" ), userDetails );
		}
	}

	/**
	 * Sets the PasswordEncoder instance to be used to encode and validate passwords.
	 * If not set, the password will be compared as plain text.
	 * <p>
	 * For systems which are already using salted password which are encoded with a previous release, the encoder
	 * should be of type {@code org.springframework.security.authentication.encoding.PasswordEncoder}. Otherwise,
	 * the recommended approach is to use {@code org.springframework.security.crypto.password.PasswordEncoder}.
	 *
	 * @param passwordEncoder must be an instance of one of the {@code PasswordEncoder} types.
	 */
	public void setPasswordEncoder( Object passwordEncoder ) {
		Assert.notNull( passwordEncoder, "passwordEncoder cannot be null" );

		if ( passwordEncoder instanceof PasswordEncoder ) {
			setPasswordEncoder( (PasswordEncoder) passwordEncoder );
			return;
		}

		if ( passwordEncoder instanceof org.springframework.security.crypto.password.PasswordEncoder ) {
			final org.springframework.security.crypto.password.PasswordEncoder delegate =
					(org.springframework.security.crypto.password.PasswordEncoder) passwordEncoder;
			setPasswordEncoder( new PasswordEncoder()
			{
				public String encodePassword( String rawPass, Object salt ) {
					checkSalt( salt );
					return delegate.encode( rawPass );
				}

				public boolean isPasswordValid( String encPass, String rawPass, Object salt ) {
					checkSalt( salt );
					return delegate.matches( rawPass, encPass );
				}

				private void checkSalt( Object salt ) {
					Assert.isNull( salt, "Salt value must be null when used with crypto module PasswordEncoder" );
				}
			} );

			return;
		}

		throw new IllegalArgumentException( "passwordEncoder must be a PasswordEncoder instance" );
	}

	private void setPasswordEncoder( PasswordEncoder passwordEncoder ) {
		Assert.notNull( passwordEncoder, "passwordEncoder cannot be null" );

		this.userNotFoundEncodedPassword = passwordEncoder.encodePassword( USER_NOT_FOUND_PASSWORD, null );
		this.passwordEncoder = passwordEncoder;
	}

	protected PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	/**
	 * The source of salts to use when decoding passwords. <code>null</code>
	 * is a valid value, meaning the <code>DaoAuthenticationProvider</code>
	 * will present <code>null</code> to the relevant <code>PasswordEncoder</code>.
	 * <p>
	 * Instead, it is recommended that you use an encoder which uses a random salt and combines it with
	 * the password field. This is the default approach taken in the
	 * {@code org.springframework.security.crypto.password} package.
	 *
	 * @param saltSource to use when attempting to decode passwords via the <code>PasswordEncoder</code>
	 */
	public void setSaltSource( SaltSource saltSource ) {
		this.saltSource = saltSource;
	}

	protected SaltSource getSaltSource() {
		return saltSource;
	}
}
