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

package com.foreach.across.modules.user.config.modules;

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.registry.MutableEntityRegistry;
import com.foreach.across.modules.entity.views.EntityFormViewFactory;
import com.foreach.across.modules.entity.views.EntityListView;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.processors.WebViewProcessorAdapter;
import com.foreach.across.modules.user.business.*;
import com.foreach.across.modules.user.services.PermissionService;
import com.foreach.across.modules.user.services.UserService;
import com.foreach.across.modules.user.ui.RolePermissionsFormElementBuilder;
import com.foreach.across.modules.web.ui.MutableViewElement;
import com.foreach.across.modules.web.ui.elements.TemplateViewElement;
import com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@AcrossDepends(required = "EntityModule")
@Configuration
public class UserEntitiesConfiguration implements EntityConfigurer
{
	@Autowired
	private MutableEntityRegistry entityRegistry;

	@Autowired
	private UserService userService;

	@Autowired
	private PermissionService permissionService;

	@Override
	public void configure( EntitiesConfigurationBuilder configuration ) {
		// By default permissions cannot be managed through the user interface
		configuration.entity( Permission.class ).hide().and()
		             .entity( PermissionGroup.class ).hide();

		// Groups should be managed through the association
		configuration.entity( MachinePrincipal.class )
		             .properties().property( "groups" ).hidden( true ).and().and()
		             .association( "machinePrincipal.groups" ).show();

		configuration.entity( User.class )
		             .properties().property( "groups" ).hidden( true ).and().and()
		             .association( "user.groups" ).show();

		configuration.entity( Role.class )
		             .properties()
		             .property( "permissions" )
		             .viewElementBuilder( ViewElementMode.CONTROL,
		                                  ctx -> new TemplateViewElement( "th/UserModule/role :: permissions" )
		             )
		             .and().and()
		             .updateFormView().addProcessor( new WebViewProcessorAdapter()
		{
			@Override
			protected void extendViewModel( EntityView view ) {
				Map<PermissionGroup, List<Permission>> permissionsByGroup = permissionService
						.getPermissions().stream()
						.sorted( Comparator.<Permission, String>comparing(
								permission -> permission.getGroup().getName() )
								         .thenComparing( Comparator.comparing( Permission::getName ) ) )
						.collect( Collectors.groupingBy( Permission::getGroup, LinkedHashMap::new, toList() ) );

				view.addAttribute( "permissionsByGroup", permissionsByGroup );

				ContainerViewElementUtils.move( view.getViewElements(), "formGroup-permissions",
				                                EntityFormViewFactory.FORM_RIGHT );
				ContainerViewElementUtils.find( view.getViewElements(), "permissions", MutableViewElement.class )
				                         .ifPresent( e -> e.setCustomTemplate( "th/UserModule/test" ) );
			}
		} );

		configuration.entity( User.class )
		             // Use the UserService for persisting User - as that one takes care of password handling
		             .entityModel().saveMethod( userService::save )
		             .and()

		             //.view( EntityListView.SUMMARY_VIEW_NAME ).template( "th/user/bla" ).and()
					 /*.properties()
						.order( "id", "email", "displayName" )
						.property( "created", "Created", new AuditableCreatedPrinter() ).and()
						.property( "lastModified", "Last modified", new AuditableLastModifiedPrinter() ).and()
						.hide( "createdDate", "createdBy", "lastModifiedDate", "lastModifiedBy" )
						.and()*/
					 .listView( EntityListView.VIEW_NAME )
					 .properties(
							 "id",
							 "email",
							 "displayName",
							 "group-membership",
							 "role-membership",
							 "lastModifiedDate"
					 )
					 .property( "group-membership" ).displayName( "Groups" ).spelValueFetcher( "groups.size()" )
					 .and()
					 .property( "role-membership" ).displayName( "Roles" ).spelValueFetcher( "roles.size()" );

		/*
		configuration.entity( Role.class )
		             .properties()
		             .property( "name", "Key" )
		             .property( "description", "Name" )
		             .and()
		             .view( EntityListView.VIEW_NAME ).properties( "description", "name" );
		             */

	}

	@Bean
	protected RolePermissionsFormElementBuilder rolePermissionsFormElementBuilder() {
		return new RolePermissionsFormElementBuilder();
	}
}
