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
package com.foreach.across.modules.user.repositories;

import com.foreach.across.modules.user.TestDatabaseConfig;
import com.foreach.across.modules.user.business.Permission;
import com.foreach.across.modules.user.business.PermissionGroup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestPermissionRepository.Config.class)
@DirtiesContext
public class TestPermissionRepository
{
	@Autowired
	private PermissionRepository permissionRepository;

	@Test
	public void notExistingPermission() {
		Permission existing = permissionRepository.getPermission( "djsklfjdskds" );

		assertNull( existing );
	}

	@Test
	public void notExistingPermissionGroup() {
		PermissionGroup existing = permissionRepository.getPermissionGroup( "djsklfjdskds" );

		assertNull( existing );
	}

	@Test
	public void saveAndGetPermissionGroup() {
		PermissionGroup group = new PermissionGroup();
		group.setName( "test-group" );
		group.setTitle( "Test permission group" );
		group.setDescription( "Contains some permissions" );

		permissionRepository.save( group );

		assertTrue( group.getId() > 0 );

		PermissionGroup existing = permissionRepository.getPermissionGroup( "test-group" );
		assertEquals( group, existing );
		assertEquals( group.getId(), existing.getId() );
		assertEquals( "Test permission group", existing.getTitle() );
		assertEquals( "Contains some permissions", existing.getDescription() );

		permissionRepository.delete( existing );

		existing = permissionRepository.getPermissionGroup( "test-group" );
		assertNull( existing );
	}

	@Test
	public void saveAndGetPermission() {
		PermissionGroup userGroup = new PermissionGroup();
		userGroup.setName( "test-users" );
		userGroup.setTitle( "Test users" );

		permissionRepository.save( userGroup );

		Permission manageUsers = new Permission( "manage users" );
		manageUsers.setGroup( userGroup );
		permissionRepository.save( manageUsers );

		assertTrue( manageUsers.getId() > 0 );

		Permission manageGroups = new Permission( "manage groups" );
		manageGroups.setGroup( userGroup );
		permissionRepository.save( manageGroups );

		assertTrue( manageGroups.getId() > 0 );

		Permission existing = permissionRepository.getPermission( "manage users" );
		assertEquals( manageUsers, existing );
		assertEquals( manageUsers.getId(), existing.getId() );

		permissionRepository.delete( existing );

		existing = permissionRepository.getPermission( "manage users" );
		assertNull( existing );

		Permission existingManageGroups = permissionRepository.getPermission( "manage groups" );
		assertEquals( manageGroups, existingManageGroups );
		assertEquals( manageGroups.getId(), existingManageGroups.getId() );

		permissionRepository.delete( existingManageGroups );

		existing = permissionRepository.getPermission( "manage groups" );
		assertNull( existing );
	}

	@Configuration
	@Import(TestDatabaseConfig.class)
	static class Config
	{
		@Bean
		public PermissionRepository permissionRepository() {
			return new PermissionRepositoryImpl();
		}
	}
}
