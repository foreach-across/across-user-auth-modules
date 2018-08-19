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

import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalService;
import com.foreach.across.modules.user.business.InternalUserDirectory;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.business.UserDirectory;
import com.foreach.across.modules.user.services.UserDirectoryService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class TestUserDetailsService
{
	@Mock
	private SecurityPrincipalService securityPrincipalService;

	@Mock
	private UserDirectoryService userDirectoryService;

	private UserDetailsService userDetailsService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks( this );
		userDetailsService = new UserDetailsServiceImpl( securityPrincipalService, userDirectoryService );
	}

	@Test
	public void usernameLookupIsByDirectoryUniquePrincipalNames() {
		UserDirectory defaultDir = new InternalUserDirectory();
		defaultDir.setId( UserDirectory.DEFAULT_INTERNAL_DIRECTORY_ID );

		UserDirectory otherDir = new InternalUserDirectory();
		otherDir.setId( 2L );

		when( userDirectoryService.getActiveUserDirectories() ).thenReturn( Arrays.asList( otherDir, defaultDir ) );

		User expectedUserDetails = new User();
		when( securityPrincipalService.getPrincipalByName( "myname" ) ).thenReturn( Optional.of( expectedUserDetails ) );

		UserDetails details = userDetailsService.loadUserByUsername( "myname" );
		assertSame( expectedUserDetails, details );

		verify( securityPrincipalService ).getPrincipalByName( "2@@@myname" );
	}

	@Test(expected = UsernameNotFoundException.class)
	public void usernameNotFoundExceptionIsThrown() {
		userDetailsService.loadUserByUsername( "myname" );
	}
}
