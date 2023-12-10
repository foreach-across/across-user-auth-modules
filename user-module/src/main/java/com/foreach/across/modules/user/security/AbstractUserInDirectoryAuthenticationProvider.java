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

import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipalId;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.business.UserDirectory;
import com.foreach.across.modules.user.services.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.util.Assert;

/**
 * Alternative for {@link org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider}
 * that fetches a {@link User} by username from a single {@link UserDirectory}.  It also allows disabling of
 * exception throwing if user is not found, making it useful for iterating over multiple providers.
 *
 * @author Arne Vandamme
 * @see UserDirectoryAuthenticationProvider
 * @since 2.0.0
 */
public abstract class AbstractUserInDirectoryAuthenticationProvider implements AuthenticationProvider, InitializingBean,
		MessageSourceAware
{
	protected final Log LOG = LogFactory.getLog( getClass() );

	//~ Instance fields ================================================================================================

	protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
	private UserCache userCache = new NullUserCache();
	private boolean forcePrincipalAsString = false;
	private UserDetailsChecker preAuthenticationChecks = new DefaultPreAuthenticationChecks();
	private UserDetailsChecker postAuthenticationChecks = new DefaultPostAuthenticationChecks();
	private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

	private boolean throwExceptionIfUserNotFound = true;
	protected UserDirectory userDirectory;
	private UserService userService;

	public void setMessageSource( MessageSource messageSource ) {
		this.messages = new MessageSourceAccessor( messageSource );
	}

	public void setUserDirectory( UserDirectory userDirectory ) {
		this.userDirectory = userDirectory;
	}

	public void setUserService( UserService userService ) {
		this.userService = userService;
	}

	public void setUserCache( UserCache userCache ) {
		this.userCache = userCache;
	}

	public void setForcePrincipalAsString( boolean forcePrincipalAsString ) {
		this.forcePrincipalAsString = forcePrincipalAsString;
	}

	/**
	 * Sets the policy will be used to verify the status of the loaded <tt>UserDetails</tt> <em>before</em>
	 * validation of the credentials takes place.
	 *
	 * @param preAuthenticationChecks strategy to be invoked prior to authentication.
	 */
	public void setPreAuthenticationChecks( UserDetailsChecker preAuthenticationChecks ) {
		this.preAuthenticationChecks = preAuthenticationChecks;
	}

	public void setPostAuthenticationChecks( UserDetailsChecker postAuthenticationChecks ) {
		this.postAuthenticationChecks = postAuthenticationChecks;
	}

	public void setAuthoritiesMapper( GrantedAuthoritiesMapper authoritiesMapper ) {
		this.authoritiesMapper = authoritiesMapper;
	}

	public void setThrowExceptionIfUserNotFound( boolean throwExceptionIfUserNotFound ) {
		this.throwExceptionIfUserNotFound = throwExceptionIfUserNotFound;
	}

	//~ Methods ========================================================================================================

	/**
	 * Allows subclasses to perform any additional checks of a returned (or cached) <code>UserDetails</code>
	 * for a given authentication request. Generally a subclass will at least compare the {@link
	 * Authentication#getCredentials()} with a {@link UserDetails#getPassword()}. If custom logic is needed to compare
	 * additional properties of <code>UserDetails</code> and/or <code>UsernamePasswordAuthenticationToken</code>,
	 * these should also appear in this method.
	 *
	 * @param userDetails    as retrieved from the {@link #buildUserDetails(User, UsernamePasswordAuthenticationToken)} or
	 *                       <code>UserCache</code>
	 * @param authentication the current request that needs to be authenticated
	 * @throws AuthenticationException AuthenticationException if the credentials could not be validated (generally a
	 *                                 <code>BadCredentialsException</code>, an <code>AuthenticationServiceException</code>)
	 */
	protected abstract void additionalAuthenticationChecks( UserDetails userDetails,
	                                                        UsernamePasswordAuthenticationToken authentication )
			throws AuthenticationException;

	public final void afterPropertiesSet() throws Exception {
		Assert.notNull( this.userCache, "A user cache must be set" );
		Assert.notNull( this.messages, "A message source must be set" );
		Assert.notNull( this.userDirectory, "A user directory must be set" );
		Assert.notNull( this.userService, "A user service must be set" );
		doAfterPropertiesSet();
	}

	public Authentication authenticate( Authentication authentication ) throws AuthenticationException {
		Assert.isInstanceOf( UsernamePasswordAuthenticationToken.class, authentication,
		                     messages.getMessage( "AbstractUserInDirectoryAuthenticationProvider.onlySupports",
		                                          "Only UsernamePasswordAuthenticationToken is supported" ) );

		// Determine username
		String username = ( authentication.getPrincipal() == null ) ? "NONE_PROVIDED" : authentication.getName();

		boolean cacheWasUsed = true;
		User user = null;
		UserDetails userDetails = this.userCache.getUserFromCache( username );

		if ( userDetails == null ) {
			cacheWasUsed = false;

			user = retrieveUser( username );

			if ( user != null ) {
				userDetails = buildUserDetails( user, (UsernamePasswordAuthenticationToken) authentication );
			}
		}

		if ( userDetails != null ) {
			try {
				preAuthenticationChecks.check( userDetails );
				additionalAuthenticationChecks( userDetails, (UsernamePasswordAuthenticationToken) authentication );
			}
			catch ( AuthenticationException exception ) {
				if ( cacheWasUsed ) {
					// There was a problem, so try again after checking
					// we're using latest data (i.e. not from the cache)
					cacheWasUsed = false;
					user = retrieveUser( username );
					if ( user != null ) {
						userDetails = buildUserDetails( user, (UsernamePasswordAuthenticationToken) authentication );
					}
					if ( userDetails != null ) {
						preAuthenticationChecks.check( userDetails );
						additionalAuthenticationChecks( userDetails,
						                                (UsernamePasswordAuthenticationToken) authentication );
					}
				}
				else {
					throw exception;
				}
			}

			if ( userDetails != null ) {
				postAuthenticationChecks.check( userDetails );

				if ( !cacheWasUsed ) {
					this.userCache.putUserInCache( userDetails );
				}

				if ( user != null ) {
					Object principalToReturn = SecurityPrincipalId.of( user.getPrincipalName() );

					if ( forcePrincipalAsString ) {
						principalToReturn = user.getPrincipalName();
					}

					return createSuccessAuthentication( principalToReturn, authentication, userDetails );
				}
				else {
					return null;
				}

			}
		}

		return null;
	}

	/**
	 * Creates a successful {@link Authentication} object.<p>Protected so subclasses can override.</p>
	 * <p>Subclasses will usually store the original credentials the user supplied (not salted or encoded
	 * passwords) in the returned <code>Authentication</code> object.</p>
	 *
	 * @param principal      that should be the principal in the returned object
	 * @param authentication that was presented to the provider for validation
	 * @param user           that was loaded by the implementation
	 * @return the successful authentication token
	 */
	protected Authentication createSuccessAuthentication( Object principal, Authentication authentication,
	                                                      UserDetails user ) {
		// Ensure we return the original credentials the user supplied,
		// so subsequent attempts are successful even with encoded passwords.
		// Also ensure we return the original getDetails(), so that future
		// authentication events after cache expiry contain the details
		UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(
				principal,
				authentication.getCredentials(),
				authoritiesMapper.mapAuthorities( user.getAuthorities() )
		);
		result.setDetails( authentication.getDetails() );

		return result;
	}

	protected void doAfterPropertiesSet() throws Exception {
	}

	/**
	 * Allows subclasses to build the <code>UserDetails</code> for a given <code>User</code>.
	 * Actual credential validation can be performed and an <code>AuthenticationException</code> thrown if
	 * validation fails.  In case the method returns {@code null} this will end in a <code>null</code>
	 * <code>Authentication</code> being returned by this provider.
	 * <p/>
	 * Note that {@link User} already implements {@link UserDetails}.  If validation is successful, that same
	 * user instance can be returned by the method implementation.
	 *
	 * @param user           The user found with the username from the authentication
	 * @param authentication The authentication request, which subclasses <em>may</em> need to perform a binding-based
	 *                       retrieval of the <code>UserDetails</code>
	 * @return the user information, can be <code>null</code> to fall back to being "unable to authenticate"
	 * @throws AuthenticationException if the credentials could not be validated
	 */
	protected abstract UserDetails buildUserDetails( User user,
	                                                 UsernamePasswordAuthenticationToken authentication )
			throws AuthenticationException;

	/**
	 * Retrieve the user instance from the user directory.
	 *
	 * @param username The username to retrieve
	 * @return user instance or {@code null} if not found
	 * @throws AuthenticationException if no user found and {@link #throwExceptionIfUserNotFound} is {@code true}
	 */
	protected User retrieveUser( String username ) throws AuthenticationException {
		try {
			User user = userService.getUserByUsername( username, userDirectory ).orElse( null );

			if ( user == null && throwExceptionIfUserNotFound ) {
				throw new BadCredentialsException( messages.getMessage(
						"AbstractUserInDirectoryAuthenticationProvider.badCredentials", "Bad credentials" ) );
			}

			return user;
		}
		catch ( Exception repositoryProblem ) {
			throw new InternalAuthenticationServiceException( repositoryProblem.getMessage(), repositoryProblem );
		}
	}

	public boolean supports( Class<?> authentication ) {
		return ( UsernamePasswordAuthenticationToken.class.isAssignableFrom( authentication ) );
	}

	private class DefaultPreAuthenticationChecks implements UserDetailsChecker
	{
		public void check( UserDetails user ) {
			if ( !user.isAccountNonLocked() ) {
				LOG.debug( "User account is locked" );

				throw new LockedException( messages.getMessage( "AbstractUserInDirectoryAuthenticationProvider.locked",
				                                                "User account is locked" ) );
			}

			if ( !user.isEnabled() ) {
				LOG.debug( "User account is disabled" );

				throw new DisabledException(
						messages.getMessage( "AbstractUserInDirectoryAuthenticationProvider.disabled",
						                     "User is disabled" ) );
			}

			if ( !user.isAccountNonExpired() ) {
				LOG.debug( "User account is expired" );

				throw new AccountExpiredException(
						messages.getMessage( "AbstractUserInDirectoryAuthenticationProvider.expired",
						                     "User account has expired" ) );
			}
		}
	}

	private class DefaultPostAuthenticationChecks implements UserDetailsChecker
	{
		public void check( UserDetails user ) {
			if ( !user.isCredentialsNonExpired() ) {
				LOG.debug( "User account credentials have expired" );

				throw new CredentialsExpiredException( messages.getMessage(
						"AbstractUserInDirectoryAuthenticationProvider.credentialsExpired",
						"User credentials have expired" ) );
			}
		}
	}
}
