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
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.user.business.*;
import com.foreach.across.modules.user.repositories.RoleRepository;
import com.foreach.across.modules.user.services.UserService;
import com.foreach.across.modules.user.ui.GroupsFormElementBuilder;
import com.foreach.across.modules.user.ui.GroupsFormProcessorAdapter;
import com.foreach.across.modules.user.ui.RoleFormProcessorAdapter;
import com.foreach.across.modules.user.ui.RolePermissionsFormElementBuilder;
import com.foreach.across.modules.user.ui.support.EQStringToRoleConverter;
import com.foreach.across.modules.user.ui.support.EQValueToRoleConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.data.domain.Sort;

@AcrossDepends(required = "EntityModule")
@Configuration
public class UserEntitiesConfiguration implements EntityConfigurer
{
	@Autowired
	private UserService userService;

	@Autowired
	public void registerEntityQueryConverters( ConverterRegistry mvcConversionService, RoleRepository roleRepository ) {
		mvcConversionService.addConverter( new EQValueToRoleConverter( roleRepository ) );
		mvcConversionService.addConverter( new EQStringToRoleConverter( roleRepository ) );
	}

	@Override
	public void configure( EntitiesConfigurationBuilder configuration ) {
		// By default permissions cannot be managed through the user interface
		configuration.withType( Permission.class ).hide();
		configuration.withType( PermissionGroup.class ).hide();

		// Groups should be managed through the association
		/*configuration.withType( MachinePrincipal.class )
		             .properties( props -> props.property( "groups" ).hidden( true ) )
		             .association( ab -> ab.name( "machinePrincipal.groups" ).show() );
*/
		configuration.withType( Role.class )
		             .properties(
				             props -> props
						             .property( "permissions" )
						             .viewElementBuilder( ViewElementMode.CONTROL, rolePermissionsFormElementBuilder() )
		             )
		             .listView(
				             lvb -> lvb.defaultSort( new Sort( "name" ) )
				                       .showProperties( "name", "authority", "description", "lastModified" )
		             )
		             .createOrUpdateFormView( fvb -> fvb.viewProcessor( roleFormProcessorAdapter() ) );

		configuration.withType( User.class )
		             .entityModel( mb -> mb.saveMethod( userService::save ) )
		             .listView( lvb -> lvb
				             .defaultSort( "displayName" )
				             .entityQueryFilter( true )
				             .showProperties( "displayName", "email", "restrictions", "lastModified" )
		             );

		configuration.withType( Group.class )
		             .listView( lvb -> lvb.defaultSort( "name" ).entityQueryFilter( true ) );

		configuration.assignableTo( BasicSecurityPrincipal.class )
		             .properties(
				             props -> props.property( "userDirectory" )
				                           .order( 1 )
				                           .readable( true )
				                           .writable( false )
		             )
		             .createFormView( fvb -> fvb.showProperties( "~userDirectory", "." ) );

		configuration.assignableTo( GroupedPrincipal.class )
		             .properties(
				             props -> props
						             .property( "groups" )
						             .viewElementBuilder( ViewElementMode.CONTROL, groupsFormElementBuilder() )
		             )
		             .createOrUpdateFormView( fvb -> fvb.viewProcessor( groupsFormProcessorAdapter() ) );

			/*
					temporary commit - association based management
			configuration.withType( User.class )
		             .association(
				             avb -> avb.name( "user.groups" )
				                       .listView( "groupSelector", lvb ->
						                       lvb.factoryType( GroupSelectionListViewFactory.class )
						                          .viewProcessor( new GroupSelectionProcessorAdapter() )
						                          .entityQueryFilter( false )
				                       )
		             );
		             */
	}

	@Bean
	protected GroupsFormElementBuilder groupsFormElementBuilder() {
		return new GroupsFormElementBuilder();
	}

	@Bean
	protected GroupsFormProcessorAdapter groupsFormProcessorAdapter() {
		return new GroupsFormProcessorAdapter();
	}

	@Bean
	protected RoleFormProcessorAdapter roleFormProcessorAdapter() {
		return new RoleFormProcessorAdapter();
	}

	@Bean
	protected RolePermissionsFormElementBuilder rolePermissionsFormElementBuilder() {
		return new RolePermissionsFormElementBuilder();
	}
}
