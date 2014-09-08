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

import com.foreach.across.modules.user.business.Permission;
import com.foreach.across.modules.user.business.PermissionGroup;

import java.util.Collection;

public interface PermissionRepository
{
	Collection<PermissionGroup> getPermissionGroups();

	Collection<Permission> getPermissions();

	Permission getPermission( String name );

	PermissionGroup getPermissionGroup( String groupName );

	void delete( Permission permission );

	void delete( PermissionGroup permissionGroup );

	void save( PermissionGroup permissionGroup );

	void save( Permission permission );
}
