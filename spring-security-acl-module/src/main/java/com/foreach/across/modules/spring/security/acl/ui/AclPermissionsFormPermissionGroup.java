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

import lombok.*;
import org.springframework.security.acls.model.Permission;

import java.util.function.Supplier;

/**
 * Represents a group of permissions that can be selected in a section.
 * Requires a permissions supplier which should return the permissions in rendering order.
 * Can optionally have a name in which case an extra header row with the group label will be added.
 *
 * @author Arne Vandamme
 * @see AclPermissionsForm
 * @see AclPermissionsFormSection
 * @since 3.0.0
 */
@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AclPermissionsFormPermissionGroup
{
	/**
	 * Name of this permission group.
	 * Can be empty or {@code null} in which case the group will not have any label.
	 */
	private final String name;

	/**
	 * Supplier that will return the set of {@link Permission} that should be selectable on this section.
	 * Note that all permissions must be registered with the {@link com.foreach.across.modules.spring.security.acl.services.AclPermissionFactory}
	 * to be able to select them.
	 */
	@NonNull
	private final Supplier<Permission[]> permissionsSupplier;

	@SuppressWarnings("unused")
	public static class AclPermissionsFormPermissionGroupBuilder
	{
		private Supplier<Permission[]> permissionsSupplier = () -> new Permission[0];

		public AclPermissionsFormPermissionGroupBuilder permissions( Permission... permissions ) {
			return permissionsSupplier( () -> permissions );
		}
	}
}
