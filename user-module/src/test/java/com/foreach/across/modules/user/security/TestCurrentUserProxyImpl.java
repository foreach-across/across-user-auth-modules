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

import com.foreach.across.modules.user.business.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
@RunWith(MockitoJUnitRunner.class)
public class TestCurrentUserProxyImpl
{
	@Spy
	private CurrentUserProxyImpl proxy = new CurrentUserProxyImpl();

	@Mock
	private User user;

	@Before
	public void before() {
		SecurityContextHolder.clearContext();
	}

	@After
	public void after() {
		SecurityContextHolder.clearContext();
	}

	@Test
	public void onlyAuthenticatedIfUserPrincipalOnValidAuthentication() {
		assertFalse( proxy.isAuthenticated() );

		Authentication auth = mock( Authentication.class );
		SecurityContextImpl ctx = new SecurityContextImpl();
		ctx.setAuthentication( auth );
		SecurityContextHolder.setContext( ctx );
		assertFalse( proxy.isAuthenticated() );

		when( auth.isAuthenticated() ).thenReturn( true );
		when( auth.getPrincipal() ).thenReturn( mock( MachinePrincipal.class ) );
		assertFalse( proxy.isAuthenticated() );

		when( auth.getPrincipal() ).thenReturn( user );
		assertTrue( proxy.isAuthenticated() );

		when( auth.isAuthenticated() ).thenReturn( false );
		assertFalse( proxy.isAuthenticated() );
	}

	@Test
	public void userReturnedOnlyIfAuthenticated() {
		Authentication auth = mock( Authentication.class );
		SecurityContextImpl ctx = new SecurityContextImpl();
		ctx.setAuthentication( auth );
		SecurityContextHolder.setContext( ctx );

		when( auth.getPrincipal() ).thenReturn( user );

		doReturn( true ).when( proxy ).isAuthenticated();
		assertSame( user, proxy.getUser() );

		doReturn( false ).when( proxy ).isAuthenticated();
		assertNull( proxy.getUser() );
	}

	@Test
	public void idReturnedOnlyIfAuthenticated() {
		doReturn( false ).when( proxy ).isAuthenticated();
		assertNull( proxy.getId() );

		when( user.getId() ).thenReturn( 123L );
		doReturn( true ).when( proxy ).isAuthenticated();
		doReturn( user ).when( proxy ).getUser();

		assertEquals( Long.valueOf( 123 ), proxy.getId() );
	}

	@Test
	public void emailReturnedOnlyIfAuthenticated() {
		doReturn( false ).when( proxy ).isAuthenticated();
		assertNull( proxy.getEmail() );

		when( user.getEmail() ).thenReturn( "test" );
		doReturn( true ).when( proxy ).isAuthenticated();
		doReturn( user ).when( proxy ).getUser();

		assertEquals( "test", proxy.getEmail() );
	}

	@Test
	public void usernameReturnedOnlyIfAuthenticated() {
		doReturn( false ).when( proxy ).isAuthenticated();
		assertNull( proxy.getUsername() );

		when( user.getUsername() ).thenReturn( "test" );
		doReturn( true ).when( proxy ).isAuthenticated();
		doReturn( user ).when( proxy ).getUser();

		assertEquals( "test", proxy.getUsername() );
	}

	@Test
	public void memberOfOnlyCalledWhenAuthenticated() {
		Group g = mock( Group.class );

		doReturn( false ).when( proxy ).isAuthenticated();
		assertFalse( proxy.isMemberOf( g ) );
		verify( user, never() ).isMemberOf( g );

		when( user.isMemberOf( g ) ).thenReturn( true );
		doReturn( true ).when( proxy ).isAuthenticated();
		doReturn( user ).when( proxy ).getUser();

		assertTrue( proxy.isMemberOf( g ) );
	}

	@Test
	public void hasRoleByNameCalledWhenAuthenticated() {
		doReturn( false ).when( proxy ).isAuthenticated();
		assertFalse( proxy.hasRole( "myrole" ) );
		verify( user, never() ).hasRole( "myrole" );

		when( user.hasRole( "myrole" ) ).thenReturn( true );
		doReturn( true ).when( proxy ).isAuthenticated();
		doReturn( user ).when( proxy ).getUser();

		assertTrue( proxy.hasRole( "myrole" ) );
	}

	@Test
	public void hasRoleCalledWhenAuthenticated() {
		Role r = mock( Role.class );

		doReturn( false ).when( proxy ).isAuthenticated();
		assertFalse( proxy.hasRole( r ) );
		verify( user, never() ).hasRole( r );

		when( user.hasRole( r ) ).thenReturn( true );
		doReturn( true ).when( proxy ).isAuthenticated();
		doReturn( user ).when( proxy ).getUser();

		assertTrue( proxy.hasRole( r ) );
	}

	@Test
	public void hasPermissionByNameCalledWhenAuthenticated() {
		doReturn( false ).when( proxy ).isAuthenticated();
		assertFalse( proxy.hasPermission( "mypermission" ) );
		verify( user, never() ).hasPermission( "mypermission" );

		when( user.hasPermission( "mypermission" ) ).thenReturn( true );
		doReturn( true ).when( proxy ).isAuthenticated();
		doReturn( user ).when( proxy ).getUser();

		assertTrue( proxy.hasPermission( "mypermission" ) );
	}

	@Test
	public void hasPermissionCalledWhenAuthenticated() {
		Permission p = mock( Permission.class );

		doReturn( false ).when( proxy ).isAuthenticated();
		assertFalse( proxy.hasPermission( p ) );
		verify( user, never() ).hasPermission( p );

		when( user.hasPermission( p ) ).thenReturn( true );
		doReturn( true ).when( proxy ).isAuthenticated();
		doReturn( user ).when( proxy ).getUser();

		assertTrue( proxy.hasPermission( p ) );
	}

	@Test
	public void hasAuthorityDelegatesToAuthentication() {
		assertFalse( proxy.hasAuthority( "some auth" ) );
		assertFalse( proxy.hasAuthority( new SimpleGrantedAuthority( "some auth" ) ) );

		Authentication auth = mock( Authentication.class );
		SecurityContextImpl ctx = new SecurityContextImpl();
		ctx.setAuthentication( auth );
		SecurityContextHolder.setContext( ctx );

		Collection<? extends GrantedAuthority> authorities = Collections.singleton( new SimpleGrantedAuthority( "some auth" ) );
		when( auth.getAuthorities() ).thenAnswer( invocation -> authorities );

		doReturn( false ).when( proxy ).isAuthenticated();
		assertFalse( proxy.hasAuthority( "some auth" ) );
		assertFalse( proxy.hasAuthority( new SimpleGrantedAuthority( "some auth" ) ) );
		verify( auth, never() ).getAuthorities();

		doReturn( true ).when( proxy ).isAuthenticated();
		assertTrue( proxy.hasAuthority( "some auth" ) );
		assertTrue( proxy.hasAuthority( new SimpleGrantedAuthority( "some auth" ) ) );
	}
}
