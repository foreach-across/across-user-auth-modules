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
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class TestRole
{
	@Test
	public void caseIsIgnored() {
		Role left = new Role( "some role" );
		left.setDescription( "description left" );

		GrantedAuthority right = new Permission( "role_some_role" );

		assertEquals( left, right );
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
		assertTrue( role.hasPermission( "one" ) );
		assertTrue( role.hasPermission( new Permission( "TWO" ) ) );
		assertTrue( role.hasPermission( three ) );
	}

	@Test
	public void authorityMatching() {
		Role role = new Role( "ROLE_ADMIN" );
		role.setId( 123L );

		Set<GrantedAuthority> actuals = new HashSet<>();
		actuals.add( role );

		AuthorityMatcher matcher = AuthorityMatcher.allOf( "ROLE_ADMIN" );
		assertTrue( matcher.matches( actuals ) );

		matcher = AuthorityMatcher.allOf( role );
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
	public void rolesAreEqualById() {
		Role roleOne = new Role( "ROLE_TEST" );
		Role roleTwo = new Role( "ROLE_TEST" );
		assertNotEquals( roleOne, roleTwo );

		roleOne.setId( 123L );
		roleTwo.setId( 456L );
		assertNotEquals( roleOne, roleTwo );

		roleTwo.setId( 123L );
		assertEquals( roleOne, roleTwo );

		roleTwo.setAuthority( "ROLE_OTHER" );
		assertEquals( roleOne, roleTwo );
	}

	@Test
	public void nonRoleAuthoritiesAreEqualByAuthorityValue() {
		GrantedAuthority perm = new Permission( "ROLE_TEST" );

		assertEquals( perm, new Role( "ROLE_TEST" ) );
		assertNotEquals( perm, new Role( "ROLE_OTHER" ) );
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
		assertEquals( "ROLE_MY_ROLE", role.getAuthority() );

		role.setAuthority( "role_other_role" );
		assertEquals( "ROLE_OTHER_ROLE", role.getAuthority() );

		role.setAuthority( "ROLE_TEST ME\tWELL" );
		assertEquals( "ROLE_TEST_ME_WELL", role.getAuthority() );
	}
}
