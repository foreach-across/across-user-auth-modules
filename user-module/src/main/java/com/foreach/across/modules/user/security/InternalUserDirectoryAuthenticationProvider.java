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

import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipalUserDetails;
import com.foreach.across.modules.user.business.InternalUserDirectory;
import com.foreach.across.modules.user.business.User;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

/**
 * Authenticates a user against a {@link InternalUserDirectory}.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class InternalUserDirectoryAuthenticationProvider extends AbstractUserInDirectoryAuthenticationProvider
{
	/**
	 * The plaintext password used to perform
	 * PasswordEncoder#matches(CharSequence, String)}  on when the user is
	 * not found to avoid SEC-2056.
	 */
	private static final String USER_NOT_FOUND_PASSWORD = "userNotFoundPassword";

	//~ Instance fields ================================================================================================

	private PasswordEncoder passwordEncoder;

	/**
	 * The password used to perform
	 * {@link PasswordEncoder#matches(CharSequence, String)} on when the user is
	 * not found to avoid SEC-2056. This is necessary, because some
	 * {@link PasswordEncoder} implementations will short circuit if the password is not
	 * in a valid format.
	 */
	private volatile String userNotFoundEncodedPassword;

	public InternalUserDirectoryAuthenticationProvider() {
		setThrowExceptionIfUserNotFound( false );
		setPasswordEncoder( PasswordEncoderFactories.createDelegatingPasswordEncoder() );
	}

	@Override
	protected void doAfterPropertiesSet() throws Exception {
		Assert.isTrue( userDirectory instanceof InternalUserDirectory, "Only InternalUserDirectory types are supported" );
	}

	@Override
	protected UserDetails buildUserDetails( User user, UsernamePasswordAuthenticationToken authentication ) throws AuthenticationException {
		// todo: passwordencoder, check hash etc
		return new SecurityPrincipalUserDetails( user.getSecurityPrincipalId(),
		                                         user.getUsername(),
		                                         user.getPassword(),
		                                         user.isEnabled(),
		                                         user.isAccountNonExpired(),
		                                         user.isCredentialsNonExpired(),
		                                         user.isAccountNonLocked(),
		                                         user.getAuthorities()
		);
	}

	@Override
	@SuppressWarnings("deprecation")
	protected void additionalAuthenticationChecks( UserDetails userDetails,
	                                               UsernamePasswordAuthenticationToken authentication ) throws AuthenticationException {
		if ( authentication.getCredentials() == null ) {
			LOG.debug( "Authentication failed: no credentials provided" );

			throw new BadCredentialsException( messages.getMessage(
					"AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials" ) /*, userDetails*/ );
		}

		String presentedPassword = authentication.getCredentials().toString();

		if ( !passwordEncoder.matches( presentedPassword, userDetails.getPassword() ) ) {
			LOG.debug( "Authentication failed: password does not match stored value" );

			throw new BadCredentialsException( messages.getMessage(
					"AbstractUserDetailsAuthenticationProvider.badCredentials",
					"Bad credentials" ) /*, userDetails */ );
		}
	}

	protected PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	/**
	 * Sets the PasswordEncoder instance to be used to encode and validate passwords. If
	 * not set, the password will be compared using {@link PasswordEncoderFactories#createDelegatingPasswordEncoder()}
	 *
	 * @param passwordEncoder must be an instance of one of the {@code PasswordEncoder}
	 *                        types.
	 */
	public void setPasswordEncoder( PasswordEncoder passwordEncoder ) {
		Assert.notNull( passwordEncoder, "passwordEncoder cannot be null" );
		this.passwordEncoder = passwordEncoder;
		this.userNotFoundEncodedPassword = null;
	}
}
