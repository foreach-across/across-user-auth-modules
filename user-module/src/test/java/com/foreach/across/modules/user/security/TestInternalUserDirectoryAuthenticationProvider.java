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
import com.foreach.across.modules.user.business.InternalUserDirectory;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.business.UserRestriction;
import com.foreach.across.modules.user.services.UserService;
import org.junit.Test;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;

import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Marc Vanbrabant
 * @since 2.0.0
 */
public class TestInternalUserDirectoryAuthenticationProvider
{
	private InternalUserDirectory internalUserDirectory = new InternalUserDirectory();

	@Test(expected = IllegalArgumentException.class)
	public void providerWithoutUserDirectoryThrowsAssertion() throws Exception {
		InternalUserDirectoryAuthenticationProvider authenticationProvider =
				new InternalUserDirectoryAuthenticationProvider();
		authenticationProvider.afterPropertiesSet();
	}

	@Test(expected = IllegalArgumentException.class)
	public void providerWithoutUserServiceThrowsAssertion() throws Exception {
		InternalUserDirectoryAuthenticationProvider authenticationProvider =
				new InternalUserDirectoryAuthenticationProvider();
		authenticationProvider.afterPropertiesSet();
	}

	@Test(expected = InternalAuthenticationServiceException.class)
	public void providerThrowsErrorWhenUserIsNotFoundAndThrowExceptionIfUserNotFoundIsTrue() throws Exception {
		Authentication authentication = new UsernamePasswordAuthenticationToken( "username", "password" );
		InternalUserDirectoryAuthenticationProvider authenticationProvider =
				internalUserDirectoryAuthenticationProvider();
		authenticationProvider.setThrowExceptionIfUserNotFound( true );
		authenticationProvider.authenticate( authentication );
		assertTrue( "shouldn't come here", false );
	}

	@Test
	public void providerReturnsNullWhenUserIsNotFoundAndThrowExceptionIfUserNotFoundIsFalse() throws Exception {
		Authentication authentication = new UsernamePasswordAuthenticationToken( "username", "password" );
		InternalUserDirectoryAuthenticationProvider authenticationProvider =
				internalUserDirectoryAuthenticationProvider();
		Authentication unsuccessfulAuthentication = authenticationProvider.authenticate( authentication );
		assertNull( unsuccessfulAuthentication );
	}

	@Test(expected = LockedException.class)
	public void providerReturnsLockedExceptionWhenUserIsLockedAndThrowExceptionIfUserNotFoundIsFalse() throws Exception {
		Authentication authentication = new UsernamePasswordAuthenticationToken( "username", "password" );
		InternalUserDirectoryAuthenticationProvider authenticationProvider =
				internalUserDirectoryAuthenticationProvider();
		UserService userService = mock( UserService.class );
		User user = new User();
		user.setRestrictions( Collections.singletonList( UserRestriction.LOCKED ) );
		user.setUsername( "username" );
		user.setPassword( "password" );
		when( userService.getUserByUsername( "username", internalUserDirectory ) ).thenReturn( Optional.of( user ) );
		authenticationProvider.setUserService( userService );
		authenticationProvider.authenticate( authentication );
		assertTrue( "shouldn't come here", false );
	}

	@Test(expected = DisabledException.class)
	public void providerReturnsDisabledExceptionWhenUserIsLockedAndThrowExceptionIfUserNotFoundIsFalse() throws Exception {
		Authentication authentication = new UsernamePasswordAuthenticationToken( "username", "password" );
		InternalUserDirectoryAuthenticationProvider authenticationProvider =
				internalUserDirectoryAuthenticationProvider();
		UserService userService = mock( UserService.class );
		User user = new User();
		user.setRestrictions( Collections.singletonList( UserRestriction.DISABLED ) );
		user.setUsername( "username" );
		user.setPassword( "password" );
		when( userService.getUserByUsername( "username", internalUserDirectory ) ).thenReturn( Optional.of( user ) );
		authenticationProvider.setUserService( userService );
		authenticationProvider.authenticate( authentication );
		assertTrue( "shouldn't come here", false );
	}

	@Test(expected = AccountExpiredException.class)
	public void providerReturnsAccountExpiredExceptionWhenUserIsLockedAndThrowExceptionIfUserNotFoundIsFalse() throws Exception {
		Authentication authentication = new UsernamePasswordAuthenticationToken( "username", "password" );
		InternalUserDirectoryAuthenticationProvider authenticationProvider =
				internalUserDirectoryAuthenticationProvider();
		UserService userService = mock( UserService.class );
		User user = new User();
		user.setRestrictions( Collections.singletonList( UserRestriction.EXPIRED ) );
		user.setUsername( "username" );
		user.setPassword( "password" );
		when( userService.getUserByUsername( "username", internalUserDirectory ) ).thenReturn( Optional.of( user ) );
		authenticationProvider.setUserService( userService );
		authenticationProvider.authenticate( authentication );
		assertTrue( "shouldn't come here", false );
	}

	@Test(expected = CredentialsExpiredException.class)
	public void providerReturnsCredentialsExpiredExceptionWhenUserIsLockedAndThrowExceptionIfUserNotFoundIsFalse() throws Exception {
		Authentication authentication = new UsernamePasswordAuthenticationToken( "username", "password" );
		InternalUserDirectoryAuthenticationProvider authenticationProvider =
				internalUserDirectoryAuthenticationProvider();
		UserService userService = mock( UserService.class );
		User user = new User();
		user.setRestrictions( Collections.singletonList( UserRestriction.CREDENTIALS_EXPIRED ) );
		user.setUsername( "username" );
		user.setPassword( "{noop}password" );
		when( userService.getUserByUsername( "username", internalUserDirectory ) ).thenReturn( Optional.of( user ) );
		authenticationProvider.setUserService( userService );
		authenticationProvider.authenticate( authentication );
		assertTrue( "shouldn't come here", false );
	}

	@Test
	public void providerReturnsUserWhenUserIsNotFoundAndThrowExceptionIfUserNotFoundIsFalse() throws Exception {
		Authentication authentication = new UsernamePasswordAuthenticationToken( "username", "password" );
		InternalUserDirectoryAuthenticationProvider authenticationProvider =
				internalUserDirectoryAuthenticationProvider();
		UserService userService = mock( UserService.class );
		User user = new User();
		user.setUsername( "username" );
		user.setPassword( "{noop}password" );
		when( userService.getUserByUsername( "username", internalUserDirectory ) ).thenReturn( Optional.of( user ) );
		authenticationProvider.setUserService( userService );
		Authentication successfulAuthentication = authenticationProvider.authenticate( authentication );
		assertNotNull( successfulAuthentication );
		assertTrue( successfulAuthentication.getPrincipal() instanceof SecurityPrincipalId );
		assertEquals( "username", successfulAuthentication.getPrincipal().toString() );
	}

	private InternalUserDirectoryAuthenticationProvider internalUserDirectoryAuthenticationProvider() throws Exception {
		InternalUserDirectoryAuthenticationProvider authenticationProvider =
				new InternalUserDirectoryAuthenticationProvider();
		authenticationProvider.setUserDirectory( internalUserDirectory );
		UserService userService = mock( UserService.class );
		authenticationProvider.setUserService( userService );
		authenticationProvider.afterPropertiesSet();
		return authenticationProvider;
	}
}
