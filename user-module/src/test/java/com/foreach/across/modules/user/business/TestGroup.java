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
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * @author Arne Vandamme
 */
public class TestGroup
{
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

		UserDirectory defaultDir = new UserDirectory();
		defaultDir.setId( UserDirectory.DEFAULT_INTERNAL_DIRECTORY_ID );

		group.setUserDirectory( defaultDir );
		assertEquals( "group:some group", group.getPrincipalName() );

		UserDirectory neg = new UserDirectory();
		neg.setId( -399L );
		group.setUserDirectory( neg );
		assertEquals( "-399,group:some group", group.getPrincipalName() );

		UserDirectory other = new UserDirectory();
		other.setId( 2L );
		group.setUserDirectory( other );
		assertEquals( "2,group:some group", group.getPrincipalName() );

		group.setName( "Renamed Group" );
		assertEquals( "2,group:renamed group", group.getPrincipalName() );

		UserDirectory third = new UserDirectory();
		third.setId( 40L );
		group.setUserDirectory( third );
		assertEquals( "40,group:renamed group", group.getPrincipalName() );

		group.setUserDirectory( defaultDir );
		assertEquals( "group:renamed group", group.getPrincipalName() );
	}
}
