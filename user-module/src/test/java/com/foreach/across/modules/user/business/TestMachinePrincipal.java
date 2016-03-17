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
public class TestMachinePrincipal
{
	@Test
	public void nullValuesAreSameAsEmpty() {
		MachinePrincipal machinePrincipal = new MachinePrincipal();
		machinePrincipal.setRoles( Collections.singleton( new Role( "one" ) ) );
		machinePrincipal.setGroups( Collections.singleton( new Group( "group one" ) ) );

		assertFalse( machinePrincipal.getRoles().isEmpty() );
		assertFalse( machinePrincipal.getGroups().isEmpty() );

		machinePrincipal.setRoles( null );
		assertTrue( machinePrincipal.getRoles().isEmpty() );
		assertFalse( machinePrincipal.getGroups().isEmpty() );

		machinePrincipal.setGroups( null );
		assertTrue( machinePrincipal.getRoles().isEmpty() );
		assertTrue( machinePrincipal.getGroups().isEmpty() );
	}

	@Test
	public void machinePrincipalDto() {
		MachinePrincipal machinePrincipal = new MachinePrincipal();
		machinePrincipal.setId( 123L );
		machinePrincipal.setName( "machine principal" );
		machinePrincipal.setGroups( Arrays.asList( new Group( "groupOne" ), new Group( "groupTwo" ) ) );
		machinePrincipal.setRoles( Arrays.asList( new Role( "one" ), new Role( "two" ) ) );

		MachinePrincipal dto = machinePrincipal.toDto();
		assertEquals( machinePrincipal, dto );
		assertEquals( machinePrincipal.getName(), dto.getName() );
		assertEquals( machinePrincipal.getRoles(), dto.getRoles() );
		assertNotSame( machinePrincipal.getRoles(), dto.getRoles() );
		assertEquals( machinePrincipal.getGroups(), dto.getGroups() );
		assertNotSame( machinePrincipal.getGroups(), dto.getGroups() );
	}

	@Test
	public void principalNameAndNameAreAlwaysLowerCased() throws Exception {
		MachinePrincipal machinePrincipal = new MachinePrincipal();
		assertNull( machinePrincipal.getPrincipalName() );
		assertNull( machinePrincipal.getName() );

		machinePrincipal.setName( "Some Principal" );

		assertEquals( "some principal", machinePrincipal.getName() );
		assertEquals( "some principal", machinePrincipal.getPrincipalName() );

		Field name = ReflectionUtils.findField( MachinePrincipal.class, "name" );
		name.setAccessible( true );
		name.set( machinePrincipal, "OTHER Name" );

		Field principalName = ReflectionUtils.findField( MachinePrincipal.class, "principalName" );
		principalName.setAccessible( true );
		principalName.set( machinePrincipal, "PRINCIPAL_NAME" );

		assertEquals( "other name", machinePrincipal.getName() );
		assertEquals( "principal_name", machinePrincipal.getPrincipalName() );
	}

	@Test
	public void principalNameIsPrefixedWithUserDirectoryIfNonDefault() {
		MachinePrincipal machinePrincipal = new MachinePrincipal();
		machinePrincipal.setName( "my.PrincipalName" );
		assertEquals( "my.principalname", machinePrincipal.getPrincipalName() );

		UserDirectory defaultDir = new UserDirectory();
		defaultDir.setId( UserDirectory.DEFAULT_INTERNAL_DIRECTORY_ID );

		machinePrincipal.setUserDirectory( defaultDir );
		assertEquals( "my.principalname", machinePrincipal.getPrincipalName() );

		UserDirectory neg = new UserDirectory();
		neg.setId( -399L );
		machinePrincipal.setUserDirectory( neg );
		assertEquals( "-399,my.principalname", machinePrincipal.getPrincipalName() );

		UserDirectory other = new UserDirectory();
		other.setId( 2L );
		machinePrincipal.setUserDirectory( other );
		assertEquals( "2,my.principalname", machinePrincipal.getPrincipalName() );

		machinePrincipal.setName( "otherPrincipalName" );
		assertEquals( "2,otherprincipalname", machinePrincipal.getPrincipalName() );

		UserDirectory third = new UserDirectory();
		third.setId( 40L );
		machinePrincipal.setUserDirectory( third );
		assertEquals( "40,otherprincipalname", machinePrincipal.getPrincipalName() );

		machinePrincipal.setUserDirectory( defaultDir );
		assertEquals( "otherprincipalname", machinePrincipal.getPrincipalName() );
	}
}
