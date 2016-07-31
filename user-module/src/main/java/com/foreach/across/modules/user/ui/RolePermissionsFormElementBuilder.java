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

package com.foreach.across.modules.user.ui;

import com.foreach.across.modules.user.business.Permission;
import com.foreach.across.modules.user.business.PermissionGroup;
import com.foreach.across.modules.user.services.PermissionService;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.TemplateViewElement;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * Custom renderer that renders the permissions as an accordion grouped by by permission group.
 * Requires the builder context to have an attribute *permissionsByPermissionGroup*.  If that attribute is
 * not yet present, it will be added by fetching all permissions, sorting them by name and grouping them
 * by {@link PermissionGroup} (in turn sorted by name).
 * <p/>
 * Defers actual rendering to the Thymeleaf fragment <strong>permissions</strong> in the <strong>role.thtml</strong> resource.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class RolePermissionsFormElementBuilder implements ViewElementBuilder<TemplateViewElement>
{
	public static final String TEMPLATE = "th/UserModule/role :: permissions";
	public static final String ATTRIBUTE = "permissionsByPermissionGroup";

	private PermissionService permissionService;

	@Autowired
	public void setPermissionService( PermissionService permissionService ) {
		this.permissionService = permissionService;
	}

	@Override
	public TemplateViewElement build( ViewElementBuilderContext viewElementBuilderContext ) {
		if ( !viewElementBuilderContext.hasAttribute( ATTRIBUTE ) ) {
			viewElementBuilderContext.setAttribute( ATTRIBUTE, fetchPermissionsByPermissionGroup() );
		}

		return new TemplateViewElement( TEMPLATE );
	}

	private Map<PermissionGroup, List<Permission>> fetchPermissionsByPermissionGroup() {
		return permissionService
				.getPermissions().stream()
				.sorted( Comparator.<Permission, String>comparing( permission -> permission.getGroup().getName() )
						         .thenComparing( Comparator.comparing( Permission::getName ) ) )
				.collect( Collectors.groupingBy( Permission::getGroup, LinkedHashMap::new, toList() ) );
	}
}
