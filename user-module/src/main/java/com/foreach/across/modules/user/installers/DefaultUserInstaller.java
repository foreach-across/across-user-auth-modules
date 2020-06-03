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
import com.foreach.across.modules.spring.security.infrastructure.services.CloseableAuthentication;
import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalService;
import com.foreach.across.modules.user.UserAuthorities;
import com.foreach.across.modules.user.UserModule;
import com.foreach.across.modules.user.business.*;
import com.foreach.across.modules.user.services.MachinePrincipalService;
import com.foreach.across.modules.user.services.PermissionService;
import com.foreach.across.modules.user.services.RoleService;
import com.foreach.across.modules.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashSet;

@Installer(description = "Installs the default permissions, roles and user", version = 5,
		phase = InstallerPhase.AfterModuleBootstrap)
public class DefaultUserInstaller implements UserAuthorities
{
	@Autowired
	private PermissionService permissionService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private UserService userService;

	@Autowired
	private MachinePrincipalService machinePrincipalService;

	@Autowired
	private SecurityPrincipalService securityPrincipalService;

	@InstallerMethod
	public void install() {
		MachinePrincipal machine = createSystemAccount();

		try (CloseableAuthentication ignored = securityPrincipalService.authenticate( machine )) {
			createPermissionGroups();
			createPermissionsAndRoles();
			createUser();
		}
	}

	private MachinePrincipal createSystemAccount() {
		MachinePrincipal machine = machinePrincipalService.getMachinePrincipalByName( "system" ).orElse( null );

		if ( machine == null ) {
			MachinePrincipal dto = new MachinePrincipal();
			dto.setName( "system" );

			machinePrincipalService.save( dto );
		}

		return machine;
	}

	private void createPermissionGroups() {
		PermissionGroup group = permissionService.getPermissionGroup( UserModule.NAME );

		if ( group == null ) {
			PermissionGroup dto = new PermissionGroup();
			dto.setName( UserModule.NAME );
			dto.setTitle( "Module: " + UserModule.NAME );
			dto.setDescription( "Basic user and user management related permissions." );

			permissionService.saveGroup( dto );
		}
	}

	private void createPermissionsAndRoles() {
		permissionService.definePermission( "access administration",
		                                    "User can perform one or more administrative actions.  Usually this means the user can access the administration interface.",
		                                    UserModule.NAME );

		permissionService.definePermission( MANAGE_USERS, "Manage user accounts", UserModule.NAME );
		Permission manageGroups = permissionService.definePermission( MANAGE_GROUPS, "Manage groups", UserModule.NAME );
		permissionService.definePermission( MANAGE_USER_ROLES, "Manage user roles", UserModule.NAME );

		Role adminRole = roleService.getRole( "ROLE_ADMIN" );
		if ( adminRole == null ) {
			roleService.defineRole( "ROLE_ADMIN", "Administrator",
			                        "System administrator role, has all major permissions.",
			                        Arrays.asList( "access administration",
			                                       MANAGE_USERS,
			                                       MANAGE_GROUPS,
			                                       MANAGE_USER_ROLES ) );
		}
		else {
			adminRole.addPermission( manageGroups );
			roleService.save( adminRole );
		}

		Role managerRole = roleService.getRole( "ROLE_MANAGER" );
		if ( managerRole == null ) {
			roleService.defineRole( "ROLE_MANAGER", "Manager",
			                        "Restricted administrator role, can manage users and groups.",
			                        Arrays.asList( "access administration",
			                                       MANAGE_USERS,
			                                       MANAGE_GROUPS ) );
		}
		else {
			managerRole.addPermission( manageGroups );
			roleService.save( managerRole );
		}
	}

	private void createUser() {
		User existing = userService.getUserByUsername( "admin" ).orElse( null );

		if ( existing == null ) {
			User user = new User();
			user.setUsername( "admin" );
			user.setPassword( "admin" );
			user.setFirstName( "" );
			user.setLastName( "" );
			user.setDisplayName( "System administrator" );
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
