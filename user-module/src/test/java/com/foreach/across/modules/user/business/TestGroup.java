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

import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Arne Vandamme
 */
public class TestGroup
{
	@Test
	public void authorityString() {
		assertNull( Group.authorityString( null ) );
		assertEquals( "GROUP_Administrative personel", Group.authorityString( "Administrative personel" ) );
		assertEquals( "GROUP_managers", Group.authorityString( "GROUP_managers" ) );
		assertEquals( "GROUP_meh meh", Group.authorityString( "group_meh meh" ) );
	}

	@Test
	public void asGrantedAuthority() {
		Group group = new Group();
		assertNull( group.asGrantedAuthority() );

		group.setName( "my group" );
		assertEquals( new SimpleGrantedAuthority( "GROUP_my group" ), group.asGrantedAuthority() );
	}

	@Test
	public void nullValuesForRolesIsSameAsEmpty() {
		Group group = new Group();
		group.setRoles( Collections.singleton( new Role( "one" ) ) );

		assertFalse( group.getRoles().isEmpty() );

		group.setRoles( null );
		assertTrue( group.getRoles().isEmpty() );
	}

	@Test
	public void groupDto() {
		Group group = new Group();
		group.setId( 123L );
		group.setName( "group" );
		group.setRoles( Arrays.asList( new Role( "one" ), new Role( "two" ) ) );

		Group dto = group.toDto();
		assertEquals( group, dto );
		assertEquals( group.getName(), dto.getName() );
		assertEquals( group.getRoles(), dto.getRoles() );
		assertNotSame( group.getRoles(), dto.getRoles() );
	}

	@Test
	public void principalNameIsAlwaysLowerCased() throws Exception {
		Group group = new Group();
		assertNull( group.getPrincipalName() );
		assertNull( group.getName() );

		group.setName( "Some Group" );

		assertEquals( "Some Group", group.getName() );
		assertEquals( "group:some group", group.getPrincipalName() );

		Field principalName = ReflectionUtils.findField( Group.class, "principalName" );
		principalName.setAccessible( true );
		principalName.set( group, "GROUP:PRINCIPAL_NAME" );

		assertEquals( "group:principal_name", group.getPrincipalName() );
	}

	@Test
	public void principalNameIsgroupPrefixedWithUserDirectoryIfNonDefault() {
		Group group = new Group();
		group.setName( "Some Group" );

		UserDirectory defaultDir = new InternalUserDirectory();
		defaultDir.setId( UserDirectory.DEFAULT_INTERNAL_DIRECTORY_ID );

		group.setUserDirectory( defaultDir );
		assertEquals( "group:some group", group.getPrincipalName() );

		UserDirectory neg = new InternalUserDirectory();
		neg.setId( -399L );
		group.setUserDirectory( neg );
		assertEquals( "-399,group:some group", group.getPrincipalName() );

		UserDirectory other = new InternalUserDirectory();
		other.setId( 2L );
		group.setUserDirectory( other );
		assertEquals( "2,group:some group", group.getPrincipalName() );

		group.setName( "Renamed Group" );
		assertEquals( "2,group:renamed group", group.getPrincipalName() );

		UserDirectory third = new InternalUserDirectory();
		third.setId( 40L );
		group.setUserDirectory( third );
		assertEquals( "40,group:renamed group", group.getPrincipalName() );

		group.setUserDirectory( defaultDir );
		assertEquals( "group:renamed group", group.getPrincipalName() );
	}

	@Test
	public void retrieveAuthorities() {
		Role role = new Role( "b role" );
		Permission perm = new Permission( "A permission" );

		role.addPermission( perm );

		Group g = new Group();
		g.addRole( role );

		List<? extends GrantedAuthority> authorities = new ArrayList<>( g.getAuthorities() );
		assertEquals( Arrays.asList( perm, role ), authorities );
	}
}
