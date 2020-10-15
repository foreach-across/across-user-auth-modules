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

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class TestPermissionGroup
{
	@Test
	public void nullValuesAreSameAsEmpty() {
		PermissionGroup permissionGroup = new PermissionGroup();
		permissionGroup.setPermissions( Collections.singleton( new Permission( "one" ) ) );

		assertFalse( permissionGroup.getPermissions().isEmpty() );

		permissionGroup.setPermissions( null );
		assertTrue( permissionGroup.getPermissions().isEmpty() );
	}

	@Test
	public void permissionGroupDto() {
		PermissionGroup permissionGroup = new PermissionGroup();
		permissionGroup.setName( "group name" );
		permissionGroup.setPermissions( new HashSet<>( Arrays.asList( new Permission( "one" ), new Permission(
				"two" ) ) ) );

		PermissionGroup dto = permissionGroup.toDto();
		assertNotNull( dto );
		assertEquals( permissionGroup, dto );
		assertEquals( permissionGroup.getName(), dto.getName() );
		assertEquals( permissionGroup.getPermissions(), dto.getPermissions() );
		assertNotSame( permissionGroup.getPermissions(), dto.getPermissions() );
	}
}
