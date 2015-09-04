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
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Arrays;

import static org.junit.Assert.*;

public class TestUser
{
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

		Field username = ReflectionUtils.findField( User.class, "username");
		username.setAccessible( true );
		username.set( user, "Some Username" );

		Field email = ReflectionUtils.findField( User.class, "email");
		email.setAccessible( true );
		email.set( user, "someEmail@localHOST" );

		Field principalName = ReflectionUtils.findField( User.class, "principalName");
		principalName.setAccessible( true );
		principalName.set( user, "PRINCIPAL_NAME" );

		assertEquals( "someemail@localhost" , user.getEmail() );
		assertEquals( "some username", user.getUsername() );
		assertEquals( "principal_name", user.getPrincipalName() );
	}

	@Test
	public void hasPermissions() {
		Role roleOne = new Role( "role one" );
		roleOne.getPermissions().add( new Permission( "perm three" ) );

		Role roleTwo = new Role( "role two" );
		roleTwo.getPermissions().addAll( Arrays.asList( new Permission( "perm one" ), new Permission( "perm two" ) ) );

		User user = new User();
		user.getRoles().addAll( Arrays.asList( roleOne, roleTwo ) );

		assertFalse( user.hasRole( "some role" ) );
		assertFalse( user.hasRole( new Role( "some role" ) ) );

		assertTrue( user.hasRole( "role one" ) );
		assertTrue( user.hasRole( new Role( "role two" ) ) );

		assertFalse( user.hasPermission( "perm" ) );
		assertTrue( user.hasPermission( "perm three" ) );
		assertTrue( user.hasPermission( "perm one" ) );
		assertTrue( user.hasPermission( "perm two" ) );
		assertTrue( user.hasPermission( new Permission( "perm three" ) ) );
		assertTrue( user.hasPermission( new Permission( "perm one" ) ) );
		assertTrue( user.hasPermission( new Permission( "perm two" ) ) );
	}

	@Test
	public void userInGroupHasPermissionChecksGroup() {
		Permission permA = new Permission( "PermA" );
		Permission permB = new Permission( "PermB" );

		Role roleA = new Role();
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
}
