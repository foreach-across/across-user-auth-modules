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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

@Service
public class RoleServiceImpl implements RoleService
{
	@Autowired
	private PermissionRepository permissionRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Override
	public Role defineRole( String name, String description, Collection<String> permissionNames ) {
		Role role = new Role( name, description );

		Set<Permission> permissions = new TreeSet<>();

		for ( String permissionName : permissionNames ) {
			Permission permission = permissionRepository.getPermission( permissionName );
			Assert.notNull( permission, "Invalid permission: " + permissionName );

			permissions.add( permission );
		}

		role.setPermissions( permissions );

		defineRole( role );

		return role;
	}

	@Override
	public void defineRole( Role role ) {
		Role existing = roleRepository.getRole( role.getName() );

		if ( existing != null ) {
			existing.setName( role.getName() );
			existing.setDescription( role.getDescription() );
			existing.setPermissions( role.getPermissions() );

			roleRepository.save( existing );

			BeanUtils.copyProperties( existing, role );
		}
		else {
			roleRepository.save( role );
		}
	}

	@Override
	public Collection<Role> getRoles() {
		return roleRepository.getAll();
	}

	@Override
	public Role getRole( String name ) {
		return roleRepository.getRole( name );
	}

	@Transactional
	@Override
	public void save( Role role ) {
		Set<Permission> actualPermissions = new TreeSet<>();

		for ( Permission permission : role.getPermissions() ) {
			Permission existing = permissionRepository.getPermission( permission.getName() );

			if ( existing == null ) {
				throw new RuntimeException( "No permission defined with name: " + permission.getName() );
			}

			actualPermissions.add( existing );
		}

		role.setPermissions( actualPermissions );

		roleRepository.save( role );
	}

	@Override
	public void delete( Role role ) {
		roleRepository.delete( role );
	}
}
