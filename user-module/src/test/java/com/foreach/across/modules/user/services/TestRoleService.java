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

package com.foreach.across.modules.user.services;

import com.foreach.across.modules.user.business.Permission;
import com.foreach.across.modules.user.business.Role;
import com.foreach.across.modules.user.repositories.PermissionRepository;
import com.foreach.across.modules.user.repositories.RoleRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestRoleService.Config.class)
@DirtiesContext
public class TestRoleService
{
	@Autowired
	private RoleService roleService;
	@Autowired
	private PermissionRepository permissionRepository;
	@Autowired
	private RoleRepository roleRepository;

	@Before
	public void resetMock() {
		reset( permissionRepository, roleRepository );
	}

	@Test
	public void cannotRedefineRole() {
		Role admin = new Role( "ROLE_ADMIN" );
		admin.setPermissions( Arrays.asList( new Permission( "access users" ), new Permission( "access groups" ) ) );
		when( roleRepository.findByAuthorityIgnoringCase( "ROLE_ADMIN" ) ).thenReturn( Optional.of( admin ) );

		Role newRole = new Role( "ROLE_ADMIN" );
		newRole.addPermission( new Permission( "access administration" ) );

		Role redefinedRole = roleService.defineRole( newRole );

		assertNotNull( redefinedRole );
		assertEquals( 2, redefinedRole.getPermissions().size() );
		Set<Permission> permissions = redefinedRole.getPermissions();
		assertTrue( permissions.stream().anyMatch( p -> "access users".equals( p.getName() ) ) );
		assertTrue( permissions.stream().anyMatch( p -> "access groups".equals( p.getName() ) ) );
		assertTrue( permissions.stream().noneMatch( p -> "access administration".equals( p.getName() ) ) );
	}

	@Configuration
	static class Config
	{
		@Bean
		public RoleService roleService() {
			return new RoleServiceImpl();
		}

		@Bean
		public PermissionRepository permissionRepository() {
			return mock( PermissionRepository.class );
		}

		@Bean
		public RoleRepository roleRepository() {
			return mock( RoleRepository.class );
		}

	}
}
