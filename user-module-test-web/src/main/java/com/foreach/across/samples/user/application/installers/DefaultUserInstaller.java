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
import com.foreach.across.core.installers.InstallerRunCondition;
import com.foreach.across.modules.user.business.*;
import com.foreach.across.modules.user.services.GroupService;
import com.foreach.across.modules.user.services.RoleService;
import com.foreach.across.modules.user.services.UserDirectoryService;
import com.foreach.across.modules.user.services.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * @author Steven Gentens
 * @since 3.1.0
 */
@RequiredArgsConstructor
@Installer(description = "Creates additional user directories, groups and users", phase = InstallerPhase.AfterContextBootstrap, runCondition = InstallerRunCondition.AlwaysRun)
public class DefaultUserInstaller
{
	private final GroupService groupService;
	private final UserService userService;
	private final RoleService roleService;
	private final UserDirectoryService userDirectoryService;

	@InstallerMethod
	public void createUserDirectory() {
		UserDirectory customDirectory = userDirectoryService.save( getUserDirectory( "my-user-directory", true ) );

		Role admin = roleService.getRole( "ROLE_ADMIN" );

		Group customDirectoryGroup = getGroup( "externals", admin );
		customDirectoryGroup.setUserDirectory( customDirectory );
		groupService.save( customDirectoryGroup );
		Group managers = groupService.save( getGroup( "managers", admin ) );
		Group extras = groupService.save( getGroup( "extras" ) );

		User jane = getUser( "jane", "doe", admin );
		jane.setUserDirectory( customDirectory );
		userService.save( jane );

		userService.save( getUser( "john", "lee", admin ) );

		User joshua = getUser( "joshua", "doe" );
		joshua.setGroups( Arrays.asList( managers, extras ) );
		userService.save( joshua );
	}

	private UserDirectory getUserDirectory( String name, boolean active ) {
		UserDirectory userDirectory = userDirectoryService.getUserDirectories().stream()
		                                                  .filter( ud -> StringUtils.equals( name, ud.getName() ) )
		                                                  .findFirst()
		                                                  .orElse( new InternalUserDirectory() );
		userDirectory.setName( name );
		userDirectory.setActive( active );
		return userDirectory;
	}

	private User getUser( String username, String lastname, Role... roles ) {
		User user = userService.findOne( QUser.user.username.eq( username ) ).orElse( new User() );
		String firstName = StringUtils.capitalize( username );
		String lastName = StringUtils.capitalize( lastname );
		user.setUsername( username );
		user.setFirstName( firstName );
		user.setLastName( lastName );
		user.setDisplayName( firstName + " " + lastName );
		user.setEmail( username + "@localhost" );
		user.setPassword( username );
		user.setRoles( Arrays.asList( roles ) );
		return user;
	}

	private Group getGroup( String groupName, Role... roles ) {
		Group group = groupService.getGroupByName( groupName ).orElse( new Group() );
		group.setName( groupName );
		group.setRoles( Arrays.asList( roles ) );
		return group;
	}
}
