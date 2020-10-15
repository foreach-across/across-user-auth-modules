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

import com.foreach.across.modules.user.business.UserDirectory;
import com.foreach.across.modules.user.services.UserDirectoryService;
import com.foreach.across.modules.user.services.UserDirectoryServiceProvider;
import com.foreach.across.modules.user.services.UserDirectoryServiceProviderManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class TestUserDirectoryAuthenticationProvider
{
	private final UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken( "user", "pwd" );

	@Mock
	private UserDirectoryService userDirectoryService;

	@Mock
	private UserDirectoryServiceProviderManager userDirectoryServiceProviderManager;

	private UserDirectoryAuthenticationProvider authenticationProvider;

	@BeforeEach
	public void initMocks() {
		MockitoAnnotations.initMocks( this );
		authenticationProvider = new UserDirectoryAuthenticationProvider( userDirectoryService,
		                                                                  userDirectoryServiceProviderManager );
	}

	@Test
	public void badCredentialsExceptionIfNoUserFound() {
		assertThrows( BadCredentialsException.class, () -> {
			authenticationProvider.authenticate( auth );
		} );
	}

	@Test
	public void providerAcceptsUsernamePasswordAuthentication() {
		assertTrue( authenticationProvider.supports( UsernamePasswordAuthenticationToken.class ) );
		assertFalse( authenticationProvider.supports( AnonymousAuthenticationToken.class ) );
	}

	@Test
	public void providersWillBeBuiltOnFirstRequestOnly() {
		silent( () -> authenticationProvider.authenticate( auth ) );
		silent( () -> authenticationProvider.authenticate( auth ) );

		verify( userDirectoryService, times( 1 ) ).getActiveUserDirectories();
	}

	@Test
	public void reloadForcesProvidersToBeRebuilt() {
		silent( () -> authenticationProvider.authenticate( auth ) );
		silent( () -> authenticationProvider.authenticate( auth ) );

		verify( userDirectoryService, times( 1 ) ).getActiveUserDirectories();

		authenticationProvider.reload();
		silent( () -> authenticationProvider.authenticate( auth ) );
		silent( () -> authenticationProvider.authenticate( auth ) );

		verify( userDirectoryService, times( 2 ) ).getActiveUserDirectories();
	}

	@Test
	public void directoryWithoutServiceProviderIsIgnored() {
		UserDirectory dirOne = mock( UserDirectory.class );

		when( userDirectoryService.getActiveUserDirectories() ).thenReturn( Collections.singletonList( dirOne ) );
		when( userDirectoryServiceProviderManager.getServiceProvider( dirOne ) ).thenReturn( null );

		Authentication actual = silent( () -> authenticationProvider.authenticate( auth ) );
		assertNull( actual );
		verify( userDirectoryServiceProviderManager ).getServiceProvider( dirOne );
	}

	@Test
	public void directoryWithoutAuthenticationProviderIsIgnored() {
		UserDirectory dirOne = mock( UserDirectory.class );
		UserDirectoryServiceProvider spOne = mock( UserDirectoryServiceProvider.class );

		when( userDirectoryService.getActiveUserDirectories() ).thenReturn( Collections.singletonList( dirOne ) );
		when( userDirectoryServiceProviderManager.getServiceProvider( dirOne ) ).thenReturn( spOne );
		when( spOne.getAuthenticationProvider( dirOne ) ).thenReturn( null );

		Authentication actual = silent( () -> authenticationProvider.authenticate( auth ) );
		assertNull( actual );
		verify( spOne ).getAuthenticationProvider( dirOne );
	}

	@Test
	public void providersWillBeLoopedInOrderUntilOneReturnsNonNull() {
		AuthenticationProvider one = mock( AuthenticationProvider.class );
		AuthenticationProvider two = mock( AuthenticationProvider.class );

		UserDirectory dirOne = mock( UserDirectory.class );
		UserDirectory dirTwo = mock( UserDirectory.class );

		UserDirectoryServiceProvider spOne = mock( UserDirectoryServiceProvider.class );
		UserDirectoryServiceProvider spTwo = mock( UserDirectoryServiceProvider.class );

		when( userDirectoryService.getActiveUserDirectories() ).thenReturn( Arrays.asList( dirOne, dirTwo ) );
		when( userDirectoryServiceProviderManager.getServiceProvider( dirOne ) ).thenReturn( spOne );
		when( userDirectoryServiceProviderManager.getServiceProvider( dirTwo ) ).thenReturn( spTwo );

		when( spOne.getAuthenticationProvider( dirOne ) ).thenReturn( one );
		when( spTwo.getAuthenticationProvider( dirTwo ) ).thenReturn( two );

		Authentication actual = silent( () -> authenticationProvider.authenticate( auth ) );
		assertNull( actual );
		verify( one ).authenticate( auth );
		verify( two ).authenticate( auth );

		reset( one, two );
		Authentication expected = mock( Authentication.class );
		when( one.authenticate( auth ) ).thenReturn( expected );
		actual = authenticationProvider.authenticate( auth );
		assertNotNull( actual );
		assertSame( expected, actual );
		verify( two, never() ).authenticate( auth );
	}

	public Authentication silent( Supplier<Authentication> supplier ) {
		try {
			return supplier.get();
		}
		catch ( BadCredentialsException ignore ) {
			return null;
		}
	}
}
