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

package com.foreach.across.samples.user.application.installers;

import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.annotations.InstallerMethod;
import com.foreach.across.core.installers.InstallerPhase;
import com.foreach.across.modules.user.UserModule;
import com.foreach.across.modules.user.business.PermissionGroup;
import com.foreach.across.modules.user.services.PermissionService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Installer(description = "Creates an additional set of permissions", phase = InstallerPhase.AfterContextBootstrap)
public class AdditionalPermissionsInstaller
{
	public static final String DIRECTORY_PERMISSIONS = "directory-permissions";
	private final PermissionService permissionService;

	@InstallerMethod
	public void createPermissions() {
		PermissionGroup group = permissionService.getPermissionGroup( DIRECTORY_PERMISSIONS );
		if ( group == null ) {
			PermissionGroup dto = new PermissionGroup();
			dto.setName( DIRECTORY_PERMISSIONS );
			dto.setTitle( "Directory & Permission management" );
			dto.setDescription( "Permissions related to directories and permissions" );

			permissionService.saveGroup( dto );
		}

		permissionService.definePermission( "access user directories",
		                                    "Provides access to manage user directories",
		                                    DIRECTORY_PERMISSIONS );
		permissionService.definePermission( "access permissions",
		                                    "Provides access to manage permissions",
		                                    DIRECTORY_PERMISSIONS );
	}
}
