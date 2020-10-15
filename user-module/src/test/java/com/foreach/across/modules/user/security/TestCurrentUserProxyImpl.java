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

import com.foreach.across.modules.spring.security.infrastructure.services.CurrentSecurityPrincipalProxy;
import com.foreach.across.modules.user.business.Group;
import com.foreach.across.modules.user.business.Permission;
import com.foreach.across.modules.user.business.Role;
import com.foreach.across.modules.user.business.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
@ExtendWith(MockitoExtension.class)
public class TestCurrentUserProxyImpl
{
	@Spy
	@InjectMocks
	private CurrentUserProxy proxy = new CurrentUserProxyImpl();

	@Mock
	private CurrentSecurityPrincipalProxy currentSecurityPrincipalProxy;

	@Mock
	private User user;

	@BeforeEach
	public void before() {
		SecurityContextHolder.clearContext();
	}

	@AfterEach
	public void after() {
		SecurityContextHolder.clearContext();
	}

	@Test
	public void isAuthenticatedGoesToOtherProxy() {
		assertFalse( proxy.isAuthenticated() );
		when( currentSecurityPrincipalProxy.isAuthenticated() ).thenReturn( true );
		assertTrue( proxy.isAuthenticated() );
	}

	@Test
	public void getUserGoesToProxy() {
		assertNull( proxy.getUser() );
		when( currentSecurityPrincipalProxy.getPrincipal( User.class ) ).thenReturn( user );
		assertSame( user, proxy.getUser() );
	}

	@Test
	public void idReturnedOnlyIfAuthenticated() {
		assertNull( proxy.getId() );

		when( user.getId() ).thenReturn( 123L );
		when( currentSecurityPrincipalProxy.getPrincipal( User.class ) ).thenReturn( user );
		assertEquals( Long.valueOf( 123 ), proxy.getId() );
	}

	@Test
	public void emailReturnedOnlyIfAuthenticated() {
		assertNull( proxy.getEmail() );

		when( user.getEmail() ).thenReturn( "test" );
		when( currentSecurityPrincipalProxy.getPrincipal( User.class ) ).thenReturn( user );
		assertEquals( "test", proxy.getEmail() );
	}

	@Test
	public void usernameReturnedOnlyIfAuthenticated() {
		assertNull( proxy.getUsername() );

		when( user.getUsername() ).thenReturn( "test" );
		when( currentSecurityPrincipalProxy.getPrincipal( User.class ) ).thenReturn( user );
		assertEquals( "test", proxy.getUsername() );
	}

	@Test
	public void memberOfOnlyCalledWhenAuthenticated() {
		Group g = mock( Group.class );

		assertFalse( proxy.isMemberOf( g ) );
		verify( user, never() ).isMemberOf( g );

		when( user.isMemberOf( g ) ).thenReturn( true );
		when( currentSecurityPrincipalProxy.getPrincipal( User.class ) ).thenReturn( user );

		assertTrue( proxy.isMemberOf( g ) );
	}

	@Test
	public void hasRoleByNameCalledWhenAuthenticated() {
		assertFalse( proxy.hasRole( "myrole" ) );
		verify( user, never() ).hasRole( "myrole" );

		when( user.hasRole( "myrole" ) ).thenReturn( true );
		when( currentSecurityPrincipalProxy.getPrincipal( User.class ) ).thenReturn( user );

		assertTrue( proxy.hasRole( "myrole" ) );
	}

	@Test
	public void hasRoleCalledWhenAuthenticated() {
		Role r = mock( Role.class );

		assertFalse( proxy.hasRole( r ) );
		verify( user, never() ).hasRole( r );

		when( user.hasRole( r ) ).thenReturn( true );
		when( currentSecurityPrincipalProxy.getPrincipal( User.class ) ).thenReturn( user );

		assertTrue( proxy.hasRole( r ) );
	}

	@Test
	public void hasPermissionByNameCalledWhenAuthenticated() {
		assertFalse( proxy.hasPermission( "mypermission" ) );
		verify( user, never() ).hasPermission( "mypermission" );

		when( user.hasPermission( "mypermission" ) ).thenReturn( true );
		when( currentSecurityPrincipalProxy.getPrincipal( User.class ) ).thenReturn( user );

		assertTrue( proxy.hasPermission( "mypermission" ) );
	}

	@Test
	public void hasPermissionCalledWhenAuthenticated() {
		Permission p = mock( Permission.class );

		assertFalse( proxy.hasPermission( p ) );
		verify( user, never() ).hasPermission( p );

		when( user.hasPermission( p ) ).thenReturn( true );
		when( currentSecurityPrincipalProxy.getPrincipal( User.class ) ).thenReturn( user );

		assertTrue( proxy.hasPermission( p ) );
	}

	@Test
	public void hasAuthorityDelegatesToPrincipalProxy() {
		assertFalse( proxy.hasAuthority( "some auth" ) );
		assertFalse( proxy.hasAuthority( new SimpleGrantedAuthority( "some auth" ) ) );

		when( currentSecurityPrincipalProxy.hasAuthority( "some auth" ) ).thenReturn( true );
		when( currentSecurityPrincipalProxy.hasAuthority( new SimpleGrantedAuthority( "some auth" ) ) ).thenReturn( true );
		assertTrue( proxy.hasAuthority( "some auth" ) );
		assertTrue( proxy.hasAuthority( new SimpleGrantedAuthority( "some auth" ) ) );
	}
}
