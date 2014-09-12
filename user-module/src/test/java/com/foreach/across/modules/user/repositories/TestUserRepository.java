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
import com.foreach.across.modules.user.business.Group;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.business.UserRestriction;
import com.foreach.across.modules.user.dto.GroupDto;
import com.foreach.across.modules.user.services.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestUserRepository.Config.class)
@DirtiesContext
public class TestUserRepository
{
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleService roleService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private PermissionService permissionService;

	private Group existingGroup;

	@Before
	public void createGroupsAndRolesAndPermissions() {
		permissionService.definePermission( "perm one", "", "test-perms" );
		permissionService.definePermission( "perm two", "", "test-perms" );
		permissionService.definePermission( "perm three", "", "test-perms" );

		roleService.defineRole( "role one", "", Arrays.asList( "perm one", "perm two" ) );
		roleService.defineRole( "role two", "", Arrays.asList( "perm two", "perm three" ) );

		existingGroup = groupService.getGroupById( -333 );

		if ( existingGroup == null ) {
			GroupDto group = new GroupDto();
			group.setName( "existing" );

			existingGroup = groupService.save( group );
		}
	}

	@Test
	public void userNotFound() {
		User user = userRepository.getById( -123 );

		assertNull( user );
	}

	@Test
	public void userWithoutPermissions() {
		User user = new User();
		user.setUsername( "fred" );
		user.setEmail( "fred@gmail.com" );
		user.setPassword( "temp1234" );
		user.setFirstName( "Freddy" );
		user.setLastName( "Alvis" );
		user.setDisplayName( "Fréddy & Màc " );
		user.setEmailConfirmed( false );
		user.setDeleted( false );

		userRepository.create( user );

		assertTrue( user.getId() > 0 );

		User existing = userRepository.getById( user.getId() );

		assertEquals( user.getId(), existing.getId() );
		assertEquals( user.getUsername(), existing.getUsername() );
		assertEquals( user.getFirstName(), existing.getFirstName() );
		assertEquals( user.getLastName(), existing.getLastName() );
		assertEquals( user.getDisplayName(), existing.getDisplayName() );
		assertEquals( user.getEmail(), existing.getEmail() );
		assertEquals( user.getPassword(), existing.getPassword() );
		assertEquals( user.isDeleted(), existing.isDeleted() );
		assertEquals( user.getEmailConfirmed(), existing.getEmailConfirmed() );
		assertNotNull( existing.getRestrictions() );
		assertEquals( user.getRestrictions(), existing.getRestrictions() );
		for ( UserRestriction userRestriction : UserRestriction.values() ) {
			assertEquals( false, existing.hasRestriction( userRestriction ) );
		}

		assertEquals( true, user.isCredentialsNonExpired() );
		assertEquals( true, user.isAccountNonLocked() );
		assertEquals( true, user.isAccountNonExpired() );
		assertEquals( true, user.isEnabled() );
	}

	@Test
	public void userDelete() {
		User user = new User();
		user.setUsername( "deleteme" );
		user.getRoles().add( roleService.getRole( "role one" ) );
		user.setEmailConfirmed( false );
		user.setDeleted( false );

		userRepository.create( user );
		assertTrue( user.getId() > 0 );

		userRepository.delete( user );
		User deleted = userRepository.getById( user.getId() );
		assertNotNull( "user should still exist in database (soft deleted)", deleted );
		assertEquals( true, deleted.isDeleted() );
	}

	@Test
	public void userWithRoles() {
		User user = new User();
		user.setUsername( "paul" );
		user.getRoles().add( roleService.getRole( "role one" ) );
		user.getRoles().add( roleService.getRole( "role two" ) );

		userRepository.create( user );

		User existing = userRepository.getById( user.getId() );

		assertEquals( user.getRoles(), existing.getRoles() );
	}

	@Test
	public void userWithGroup() {
		User user = new User();
		user.setUsername( "freddy" );
		user.addRole( roleService.getRole( "role one" ) );
		user.addGroup( existingGroup );

		userRepository.create( user );

		User existing = userRepository.getById( user.getId() );

		assertEquals( user.getGroups(), existing.getGroups() );
		assertEquals( user.getRoles(), existing.getRoles() );
	}

	@Configuration
	@Import(TestDatabaseConfig.class)
	static class Config
	{
		@Bean
		public RoleService roleService() {
			return new RoleServiceImpl();
		}

		@Bean
		public PermissionService permissionService() {
			return new PermissionServiceImpl();
		}

		@Bean
		public RoleRepository roleRepository() {
			return new RoleRepositoryImpl();
		}

		@Bean
		public GroupRepositoryImpl groupRepository() {
			return new GroupRepositoryImpl();
		}

		@Bean
		public GroupService groupService() {
			return new GroupServiceImpl();
		}

		@Bean
		public PermissionRepository permissionRepository() {
			return new PermissionRepositoryImpl();
		}

		@Bean
		public UserRepository userRepository() {
			return new UserRepositoryImpl();
		}
	}
}
