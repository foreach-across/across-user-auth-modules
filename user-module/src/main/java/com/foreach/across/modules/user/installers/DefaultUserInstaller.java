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
package com.foreach.across.modules.user.installers;

import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.annotations.InstallerMethod;
import com.foreach.across.core.installers.InstallerPhase;
import com.foreach.across.modules.user.UserModule;
import com.foreach.across.modules.user.business.MachinePrincipal;
import com.foreach.across.modules.user.business.PermissionGroup;
import com.foreach.across.modules.user.business.Role;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.dto.MachinePrincipalDto;
import com.foreach.across.modules.user.dto.UserDto;
import com.foreach.across.modules.user.services.MachinePrincipalService;
import com.foreach.across.modules.user.services.PermissionService;
import com.foreach.across.modules.user.services.RoleService;
import com.foreach.across.modules.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashSet;

@Installer(description = "Installs the default permissions, roles and user", version = 3,
           phase = InstallerPhase.AfterModuleBootstrap)
public class DefaultUserInstaller
{
	@Autowired
	private PermissionService permissionService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private UserService userService;

	@Autowired
	private MachinePrincipalService machinePrincipalService;

	@InstallerMethod
	public void install() {
		createPermissionGroups();
		createPermissionsAndRoles();
		createUser();
		createSystemAccount();
	}

	private void createSystemAccount() {
		MachinePrincipal machine = machinePrincipalService.getMachinePrincipalByName( "system" );

		if ( machine == null ) {
			MachinePrincipalDto dto = new MachinePrincipalDto();
			dto.setName( "system" );

			machinePrincipalService.save( dto );
		}
	}

	private void createPermissionGroups() {
		PermissionGroup group = permissionService.getPermissionGroup( UserModule.NAME );

		if ( group == null ) {
			group = new PermissionGroup();
			group.setName( UserModule.NAME );
			group.setTitle( "Module: " + UserModule.NAME );
			group.setDescription( "Basic user and user management related permissions." );

			permissionService.save( group );
		}
	}

	private void createPermissionsAndRoles() {
		permissionService.definePermission( "access administration",
		                                    "User can perform one or more administrative actions.  Usually this means the user can access the administration interface.",
		                                    UserModule.NAME );

		permissionService.definePermission( "manage users", "Manage user accounts", UserModule.NAME );
		permissionService.definePermission( "manage user roles", "Manage user roles", UserModule.NAME );

		roleService.defineRole( "ROLE_ADMIN", "Administrator",
		                        Arrays.asList( "access administration", "manage users", "manage user roles" ) );
		roleService.defineRole( "ROLE_MANAGER", "Manager", Arrays.asList( "access administration", "manage users" ) );
	}

	private void createUser() {
		User existing = userService.getUserByUsername( "admin" );

		if ( existing == null ) {
			UserDto user = new UserDto();
			user.setUsername( "admin" );
			user.setPassword( "admin" );
			user.setFirstName( "" );
			user.setLastName( "" );
			user.setDisplayName( "Root administrator" );
			user.setEmail( "admin@localhost" );
			user.setDeleted( false );
			user.setEmailConfirmed( true );

			HashSet<Role> roles = new HashSet<>();
			roles.add( roleService.getRole( "ROLE_ADMIN" ) );

			user.setRoles( roles );

			userService.save( user );
		}
	}
}
