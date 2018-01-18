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

import lombok.Builder;
import lombok.Data;
import org.springframework.security.acls.model.Permission;

import java.util.function.Supplier;

/**
 * Represents a single section of an ACL permissions form for an entity.
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
@Data
@Builder(toBuilder = true)
public class AclPermissionsFormSection
{
	/**
	 * Name of this section, used as a key in message codes.
	 */
	private String name;

	/**
	 * Supplier that will return the set of {@link Permission} that should be selectable on this section.
	 * Note that all permissions must be registered with the {@link com.foreach.across.modules.spring.security.acl.services.AclPermissionFactory}
	 * to be able to select them.
	 */
	private Supplier<Permission[]> permissions;
}
