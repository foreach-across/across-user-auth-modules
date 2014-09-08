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

import static org.junit.Assert.*;

public class TestRole
{
	@Test
	public void equals() {
		Role left = new Role( "some role" );
		left.setDescription( "description left" );

		Role right = new Role( "some role" );

		assertEquals( left, right );
		assertNotEquals( left, new Role( "other role" ) );
	}

	@Test
	public void caseIsIgnored() {
		Role left = new Role( "some role" );
		left.setDescription( "description left" );

		Role right = new Role( "SOME role" );

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
}
