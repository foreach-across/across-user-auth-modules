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

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiFactory;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.user.business.Permission;
import com.foreach.across.modules.user.business.PermissionGroup;
import com.foreach.across.modules.user.business.Role;
import com.foreach.across.modules.user.services.PermissionService;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class RolePermissionsFormElementBuilder implements ViewElementBuilder<ContainerViewElement>
{
	private PermissionService permissionService;
	private BootstrapUiFactory bootstrapUi;

	@Autowired
	public void setPermissionService( PermissionService permissionService ) {
		this.permissionService = permissionService;
	}

	@Autowired
	public void setBootstrapUiFactory( BootstrapUiFactory bootstrapUi ) {
		this.bootstrapUi = bootstrapUi;
	}

	@Override
	public ContainerViewElement build( ViewElementBuilderContext viewElementBuilderContext ) {
		Role role = EntityViewElementUtils.currentEntity( viewElementBuilderContext, Role.class );

		Map<PermissionGroup, List<Permission>> permissionsByGroup = permissionService
				.getPermissions().stream()
				.sorted( Comparator.<Permission, String>comparing( permission -> permission.getGroup().getName() )
						         .thenComparing( Comparator.comparing( Permission::getName ) ) )
				.collect( Collectors.groupingBy( Permission::getGroup, LinkedHashMap::new, toList() ) );

		NodeViewElementBuilder container = bootstrapUi.node( "div" )
		                                              .css( "panel-group" )
		                                              .htmlId( "group-permissions" )
		                                              .attribute( "aria-multiselectable", true )
		                                              .attribute( "role", "tablist" );

		permissionsByGroup.forEach( ( group, permissions ) -> {

			NodeViewElementBuilder body = bootstrapUi
					.div()
					.css( "panel-body" )
					.add(
							bootstrapUi.paragraph()
							           .add( bootstrapUi.text( group.getDescription() ) )
					);

			permissions.forEach( permission -> {
				body.add(
						bootstrapUi.checkbox()
						           .controlName( "entity.permissions" )
						           .value( permission.getId() )
						           .selected( role.hasPermission( permission ) )
						           .label( permission.getName() )
						           .add(
								           bootstrapUi.div()
								                      .css( "small", "text-muted" )
								                      .add( bootstrapUi.text( permission.getDescription() ) )
						           )
				);
			} );

			container.add(
					bootstrapUi.div()
					           .css( "panel", "panel-default" )
					           .add(
							           bootstrapUi.div()
							                      .css( "panel-heading" )
							                      .add( bootstrapUi.node( "strong" )
							                                       .add( bootstrapUi.text( group.getTitle() ) ) ) )
					           .add(
							           bootstrapUi.div()
							                      .css( "panel-collapse", "collapse", "in" )
							                      .attribute( "role", "tabpanel" )
							                      .add( body )
					           )
			);
		} );

		return container.build( viewElementBuilderContext );
	}
}
