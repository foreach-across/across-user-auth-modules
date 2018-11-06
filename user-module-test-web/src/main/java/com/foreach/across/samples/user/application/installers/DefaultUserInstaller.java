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
import com.foreach.across.modules.user.business.InternalUserDirectory;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.business.UserDirectory;
import com.foreach.across.modules.user.services.RoleService;
import com.foreach.across.modules.user.services.UserDirectoryService;
import com.foreach.across.modules.user.services.UserService;
import lombok.RequiredArgsConstructor;

import java.util.Collections;

/**
 * @author Steven Gentens
 * @since 3.1.0
 */
@RequiredArgsConstructor
@Installer(description = "Creates a secondary user directory with a test user", phase = InstallerPhase.AfterContextBootstrap)
public class DefaultUserInstaller
{
	private final UserService userService;
	private final RoleService roleService;
	private final UserDirectoryService userDirectoryService;

	@InstallerMethod
	public void createUserDirectory() {
		UserDirectory userDirectory = new InternalUserDirectory();
		userDirectory.setName( "my-user-directory" );
		userDirectory.setActive( true );
		userDirectory = userDirectoryService.save( userDirectory );

		User jane = new User();
		jane.setUsername( "jane" );
		jane.setDisplayName( "Jane Doe" );
		jane.setEmailConfirmed( true );
		jane.setEmail( "jane@localhost" );
		jane.setPassword( "jane" );
		jane.setFirstName( "Jane" );
		jane.setLastName( "Doe" );
		jane.setUserDirectory( userDirectory );
		jane.setRoles( Collections.singleton( roleService.getRole( "ROLE_ADMIN" ) ) );
		userService.save( jane );
	}
}
