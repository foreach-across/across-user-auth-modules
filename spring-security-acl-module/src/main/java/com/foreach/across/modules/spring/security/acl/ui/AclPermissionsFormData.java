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

package com.foreach.across.modules.spring.security.acl.ui;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;

import java.util.*;

/**
 * Holds resolved and verified permission data, as well as a cache for {@link org.springframework.security.acls.model.Sid} values.
 * <p/>
 * Resolves both permission groups and permissions when a section is being added. Will filter out empty sections and or groups.
 * And empty group is one without any permissions, an empty section is one without any groups (empty groups will not be added).
 * <p>
 * This is an internal transfer object for {@link AclPermissionsFormViewProcessor}.
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
class AclPermissionsFormData
{
	private final Map<AclPermissionsFormSection, Map<AclPermissionsFormPermissionGroup, Permission[]>> permissionGroupsForSection = new HashMap<>();
	private final Map<AclPermissionsFormSection, Permission[]> permissionsForSection = new LinkedHashMap<>();

	@Getter
	private final Map<Sid, Object> sidCache = new HashMap<>();

	@Getter
	private final AclPermissionsForm permissionsForm;

	AclPermissionsFormData( AclPermissionsForm permissionsForm ) {
		this.permissionsForm = permissionsForm;

		permissionsForm.getSections().forEach( this::addSection );
	}

	private void addSection( @NonNull AclPermissionsFormSection section ) {
		val groups = section.getPermissionGroupsSupplier().get();

		List<Permission> allPermissions = new ArrayList<>();

		Map<AclPermissionsFormPermissionGroup, Permission[]> permissionsForGroup = new LinkedHashMap<>();
		for ( val group : groups ) {
			val permissions = group.getPermissionsSupplier().get();
			if ( permissions.length > 0 ) {
				permissionsForGroup.put( group, permissions );

				for ( val perm : permissions ) {
					if ( allPermissions.contains( perm ) ) {
						throw new IllegalStateException( "Permission " + perm + " was present in more than one group" );
					}
					allPermissions.add( perm );
				}
			}
		}

		if ( !permissionsForGroup.isEmpty() ) {
			permissionGroupsForSection.put( section, permissionsForGroup );
			permissionsForSection.put( section, allPermissions.toArray( new Permission[allPermissions.size()] ) );
		}
	}

	Collection<AclPermissionsFormSection> getSections() {
		return permissionsForSection.keySet();
	}

	Permission[] getPermissionsForSection( AclPermissionsFormSection section ) {
		return permissionsForSection.get( section );
	}

	Map<AclPermissionsFormPermissionGroup, Permission[]> getPermissionGroupsForSection( AclPermissionsFormSection section ) {
		return permissionGroupsForSection.get( section );
	}

	AclPermissionsFormSection getSectionWithName( @NonNull String sectionName ) {
		return permissionsForSection.keySet()
		                            .stream()
		                            .filter( s -> sectionName.equals( s.getName() ) )
		                            .findFirst()
		                            .orElse( null );
	}
}
