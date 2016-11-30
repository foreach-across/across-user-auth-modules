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
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class TestUser
{
	@Test
	public void principalNameIsLowerCasedAndPrefixedWithUserDirectoryIfNonDefault() {
		User user = new User();
		user.setUsername( "my.Username" );
		assertEquals( "my.username", user.getPrincipalName() );

		UserDirectory defaultDir = new InternalUserDirectory();
		defaultDir.setId( UserDirectory.DEFAULT_INTERNAL_DIRECTORY_ID );

		user.setUserDirectory( defaultDir );
		assertEquals( "my.username", user.getPrincipalName() );

		UserDirectory neg = new InternalUserDirectory();
		neg.setId( -399L );
		user.setUserDirectory( neg );
		assertEquals( "-399@@@my.username", user.getPrincipalName() );

		UserDirectory other = new InternalUserDirectory();
		other.setId( 2L );
		user.setUserDirectory( other );
		assertEquals( "2@@@my.username", user.getPrincipalName() );

		user.setUsername( "otherUserName" );
		assertEquals( "2@@@otherusername", user.getPrincipalName() );

		UserDirectory third = new InternalUserDirectory();
		third.setId( 40L );
		user.setUserDirectory( third );
		assertEquals( "40@@@otherusername", user.getPrincipalName() );

		user.setUserDirectory( defaultDir );
		assertEquals( "otherusername", user.getPrincipalName() );
	}

	@Test
	public void usernameAndEmailIsAlwaysLowerCased() throws Exception {
		User user = new User();
		assertNull( user.getPrincipalName() );
		assertNull( user.getUsername() );
		assertNull( user.getEmail() );

		user.setEmail( "someEmail@localHOST" );
		user.setUsername( "Some Username" );

		assertEquals( "someemail@localhost", user.getEmail() );
		assertEquals( "some username", user.getUsername() );
		assertEquals( "some username", user.getPrincipalName() );

		Field username = ReflectionUtils.findField( User.class, "username" );
		username.setAccessible( true );
		username.set( user, "Some Username" );

		Field email = ReflectionUtils.findField( User.class, "email" );
		email.setAccessible( true );
		email.set( user, "someEmail@localHOST" );

		Field principalName = ReflectionUtils.findField( User.class, "principalName" );
		principalName.setAccessible( true );
		principalName.set( user, "PRINCIPAL_NAME" );

		assertEquals( "someemail@localhost", user.getEmail() );
		assertEquals( "some username", user.getUsername() );
		assertEquals( "principal_name", user.getPrincipalName() );
	}

	@Test
	public void hasPermissions() {
		Permission permissionThree = new Permission( "perm three" );
		Role roleOne = new Role( "role one" );
		roleOne.setId( 123L );
		roleOne.getPermissions().add( permissionThree );

		Role roleTwo = new Role( "role two" );
		roleTwo.getPermissions().addAll( Arrays.asList( new Permission( "perm one" ), new Permission( "perm two" ) ) );

		User user = new User();
		user.getRoles().addAll( Arrays.asList( roleOne, roleTwo ) );

		assertFalse( user.hasRole( "some role" ) );
		assertFalse( user.hasRole( new Role( "some role" ) ) );

		assertTrue( user.hasRole( "role one" ) );
		assertFalse( user.hasRole( new Role( "role two" ) ) );
		assertTrue( user.hasRole( roleTwo ) );

		assertFalse( user.hasPermission( "perm" ) );
		assertTrue( user.hasPermission( "perm three" ) );
		assertTrue( user.hasPermission( "perm one" ) );
		assertTrue( user.hasPermission( "perm two" ) );
		assertTrue( user.hasPermission( permissionThree ) );
		assertFalse( user.hasPermission( new Permission( "perm one" ) ) );
		assertFalse( user.hasPermission( new Permission( "perm two" ) ) );
	}

	@Test
	public void userInGroupHasPermissionChecksGroup() {
		Permission permA = new Permission( "PermA" );
		Permission permB = new Permission( "PermB" );

		Role roleA = new Role();
		roleA.setAuthority( "RoleA" );
		roleA.setName( "RoleA" );
		roleA.getPermissions().addAll( Arrays.asList( permA, permB ) );

		Group groupA = new Group();
		groupA.setName( "GroupA" );
		groupA.addRole( roleA );

		User userA = new User();
		userA.addRole( roleA );
		User userB = new User();
		userB.addGroup( groupA );

		assertTrue( userA.hasPermission( permA ) );
		assertTrue( userA.hasPermission( permB ) );
		assertTrue( userB.hasPermission( permA ) );
		assertTrue( userB.hasPermission( permB ) );
	}

	@Test
	public void nullValuesAreSameAsEmpty() {
		User user = new User();
		user.setRoles( Collections.singleton( new Role( "one" ) ) );
		user.setGroups( Collections.singleton( new Group( "group one" ) ) );
		user.setRestrictions( Collections.singleton( UserRestriction.EXPIRED ) );

		assertFalse( user.getRoles().isEmpty() );
		assertFalse( user.getGroups().isEmpty() );
		assertFalse( user.getRestrictions().isEmpty() );

		user.setRoles( null );
		assertTrue( user.getRoles().isEmpty() );
		assertFalse( user.getGroups().isEmpty() );
		assertFalse( user.getRestrictions().isEmpty() );

		user.setGroups( null );
		assertTrue( user.getRoles().isEmpty() );
		assertTrue( user.getGroups().isEmpty() );
		assertFalse( user.getRestrictions().isEmpty() );

		user.setRestrictions( null );
		assertTrue( user.getRoles().isEmpty() );
		assertTrue( user.getGroups().isEmpty() );
		assertTrue( user.getRestrictions().isEmpty() );
	}

	@Test
	public void userDto() {
		User user = new User();
		user.setId( 123L );
		user.setUsername( "someUser" );
		user.setGroups( Arrays.asList( new Group( "groupOne" ), new Group( "groupTwo" ) ) );
		user.setRoles( Arrays.asList( new Role( "one" ), new Role( "two" ) ) );
		user.setRestrictions( Arrays.asList( UserRestriction.CREDENTIALS_EXPIRED, UserRestriction.DISABLED ) );

		User dto = user.toDto();
		assertEquals( user, dto );
		assertEquals( user.getUsername(), dto.getUsername() );
		assertEquals( user.getRoles(), dto.getRoles() );
		assertNotSame( user.getRoles(), dto.getRoles() );
		assertEquals( user.getGroups(), dto.getGroups() );
		assertNotSame( user.getGroups(), dto.getGroups() );
		assertEquals( user.getRestrictions(), dto.getRestrictions() );
		assertNotSame( user.getRestrictions(), dto.getRestrictions() );
	}

	@Test
	public void userLabel() {
		User user = new User();
		assertEquals( "", user.getLabel() );

		user.setUsername( "uname" );
		assertEquals( "uname", user.getLabel() );

		user.setFirstName( "John" );
		assertEquals( "John", user.getLabel() );

		user.setFirstName( "" );
		user.setLastName( "Doe" );
		assertEquals( "Doe", user.getLabel() );

		user.setFirstName( "John" );
		assertEquals( "John Doe", user.getLabel() );

		user.setDisplayName( "johnny" );
		assertEquals( "johnny", user.getLabel() );
	}

	@Test
	public void retrieveAuthorities() {
		Role role = new Role( "b role" );
		Permission perm = new Permission( "A permission" );
		role.addPermission( perm );

		Role groupRole = new Role( "a role" );
		Permission groupPerm = new Permission( "B permission" );
		groupRole.addPermission( groupPerm );

		Group g = new Group();
		g.setName( "my group" );
		g.addRole( groupRole );

		User u = new User();
		u.addRole( role );
		u.addGroup( g );

		List<? extends GrantedAuthority> authorities = new ArrayList<>( u.getAuthorities() );
		assertEquals( Arrays.asList( perm, groupPerm, g.asGrantedAuthority(), groupRole, role ), authorities );
	}
}
