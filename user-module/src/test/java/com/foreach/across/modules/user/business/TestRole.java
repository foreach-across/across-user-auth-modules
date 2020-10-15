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

package com.foreach.across.modules.user.business;

import com.foreach.across.modules.spring.security.authority.AuthorityMatcher;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TestRole
{
	@Test
	public void authorityString() {
		assertNull( Role.authorityString( null ) );
		assertEquals( "ROLE_some administrator", Role.authorityString( "some administrator" ) );
		assertEquals( "ROLE_manager", Role.authorityString( "ROLE_manager" ) );
		assertEquals( "ROLE_meh meh", Role.authorityString( "role_meh meh" ) );
	}

	@Test
	public void toStringReturnsAuthority() {
		Role r = new Role( "my Custom Role" );
		assertEquals( "ROLE_my Custom Role", r.toString() );
	}

	@Test
	public void hasPermissions() {
		Permission three = new Permission( "three" );

		Role role = new Role( "some" );
		role.getPermissions().add( new Permission( "one" ) );
		role.getPermissions().add( new Permission( "two" ) );
		role.getPermissions().add( three );

		assertFalse( role.hasPermission( "not existing" ) );
		assertFalse( role.hasPermission( new Permission( "another not existing" ) ) );
		assertFalse( role.hasPermission( new Permission( "two" ) ) );
		assertTrue( role.hasPermission( "one" ) );
		assertTrue( role.hasPermission( three ) );
	}

	@Test
	public void authorityMatching() {
		Role role = new Role( "ROLE_ADMIN" );
		role.setId( 123L );

		Set<GrantedAuthority> actuals = new HashSet<>();
		actuals.add( new SimpleGrantedAuthority( role.getAuthority() ) );

		AuthorityMatcher matcher = AuthorityMatcher.allOf( "ROLE_ADMIN" );
		assertTrue( matcher.matches( actuals ) );

		matcher = AuthorityMatcher.allOf( new SimpleGrantedAuthority( role.getAuthority() ) );
		assertTrue( matcher.matches( actuals ) );

		matcher = AuthorityMatcher.allOf( new SimpleGrantedAuthority( new Role( "ROLE_ADMIN" ).getAuthority() ) );
		assertTrue( matcher.matches( actuals ) );
	}

	@Test
	public void nullValuesAreSameAsEmpty() {
		Role role = new Role( "some role" );
		role.setPermissions( Collections.singleton( new Permission( "one" ) ) );

		assertFalse( role.getPermissions().isEmpty() );

		role.setPermissions( null );
		assertTrue( role.getPermissions().isEmpty() );
	}

	@Test
	public void roleDto() {
		Role role = new Role( "some role" );
		role.setId( 123L );
		role.setPermissions( Arrays.asList( new Permission( "one" ), new Permission( "two" ) ) );

		Role dto = role.toDto();
		assertNotNull( dto );
		assertEquals( role, dto );
		assertEquals( role.getName(), dto.getName() );
		assertEquals( role.getPermissions(), dto.getPermissions() );
		assertNotSame( role.getPermissions(), dto.getPermissions() );
	}

	@Test
	public void authorityValue() {
		Role role = new Role( "my role" );
		assertEquals( "ROLE_my role", role.getAuthority() );

		role.setAuthority( "role_other_role" );
		assertEquals( "ROLE_other_role", role.getAuthority() );

		role.setAuthority( "ROLE_some other role" );
		assertEquals( "ROLE_some other role", role.getAuthority() );
	}

	@Test
	public void constructors() {
		Role role = new Role();
		assertNull( role.getName() );
		assertNull( role.getAuthority() );

		role = new Role( "my authority" );
		assertEquals( "my authority", role.getName() );
		assertEquals( Role.authorityString( "my authority" ), role.getAuthority() );

		role = new Role( "my auth", "Manager" );
		assertEquals( "Manager", role.getName() );
		assertEquals( Role.authorityString( "my auth" ), role.getAuthority() );
	}
}
