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
import com.foreach.across.modules.user.business.PermissionGroup;
import com.foreach.across.modules.user.repositories.PermissionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
public class PermissionServiceImpl implements PermissionService
{
	@Autowired
	private PermissionRepository permissionRepository;

	@Override
	public Permission definePermission( String name, String description, String groupName ) {
		PermissionGroup group = permissionRepository.getPermissionGroup( groupName );

		if ( group == null ) {
			group = new PermissionGroup();
			group.setName( groupName );

			permissionRepository.save( group );
		}

		return definePermission( name, description, group );
	}

	@Override
	public Permission definePermission( String name, String description, PermissionGroup group ) {
		Permission permission = new Permission( name, description );
		permission.setGroup( group );

		definePermission( permission );

		return permission;
	}

	@Transactional
	@Override
	public void definePermission( Permission permission ) {
		Permission existing = permissionRepository.getPermission( permission.getName() );

		if ( existing != null ) {
			existing.setName( permission.getName() );
			existing.setDescription( permission.getDescription() );
			existing.setGroup( permission.getGroup() );

			permissionRepository.save( existing );

			BeanUtils.copyProperties( existing, permission );
		}
		else {
			permissionRepository.save( permission );
		}
	}

	@Override
	public Collection<PermissionGroup> getPermissionGroups() {
		return permissionRepository.getPermissionGroups();
	}

	@Override
	public PermissionGroup getPermissionGroup( String name ) {
		return permissionRepository.getPermissionGroup( name );
	}

	@Override
	public void save( PermissionGroup group ) {
		permissionRepository.save( group );
	}

	@Override
	public void delete( PermissionGroup group ) {
		permissionRepository.delete( group );
	}

	@Override
	public Collection<Permission> getPermissions() {
		return permissionRepository.getPermissions();
	}

	@Override
	public Permission getPermission( String name ) {
		return permissionRepository.getPermission( name );
	}

	@Override
	public void save( Permission permission ) {
		permissionRepository.save( permission );
	}

	@Override
	public void delete( Permission permission ) {
		permissionRepository.delete( permission );
	}
}
