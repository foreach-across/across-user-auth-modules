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
import com.foreach.across.modules.entity.registry.EntityModelImpl;
import com.foreach.across.modules.entity.registry.MutableEntityRegistry;
import com.foreach.across.modules.entity.views.EntityListView;
import com.foreach.across.modules.user.business.MachinePrincipal;
import com.foreach.across.modules.user.business.Permission;
import com.foreach.across.modules.user.business.PermissionGroup;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.support.RepositoryInvoker;
import org.springframework.util.MultiValueMap;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;

@AcrossDepends(required = "EntityModule")
@Configuration
public class UserEntitiesConfiguration implements EntityConfigurer
{
	@Autowired
	private MutableEntityRegistry entityRegistry;

	@Autowired
	private UserService userService;

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

		configuration.entity( User.class )
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
				.property( "group-membership" ).displayName( "GroupUsers" ).spelValueFetcher( "groups.size()" ).and()
				.property( "role-membership" ).displayName( "Roles" ).spelValueFetcher( "roles.size()" );

		@SuppressWarnings("unchecked")
		// Use the UserService for persisting User - as that one takes care of password handling
				EntityModelImpl<User, ? extends Serializable> userModel =
				(EntityModelImpl) entityRegistry.getMutableEntityConfiguration( User.class )
				                                .getEntityModel();

		userModel.setCrudInvoker( new RepositoryInvoker()
		{
			@Override
			public boolean hasSaveMethod() {
				return true;
			}

			@Override
			public boolean hasDeleteMethod() {
				return false;
			}

			@Override
			public boolean hasFindOneMethod() {
				return true;
			}

			@Override
			public boolean hasFindAllMethod() {
				return false;
			}

			@Override
			@SuppressWarnings("unchecked")
			public <T> T invokeSave( T object ) {
				return (T) userService.save( (User) object );
			}

			@Override
			@SuppressWarnings("unchecked")
			public <T> T invokeFindOne( Serializable id ) {
				return (T) userService.getUserById( (Long) id );
			}

			@Override
			public Iterable<Object> invokeFindAll( Pageable pageable ) {
				return null;
			}

			@Override
			public Iterable<Object> invokeFindAll( Sort sort ) {
				return null;
			}

			@Override
			public void invokeDelete( Serializable id ) {

			}

			@Override
			public Object invokeQueryMethod( Method method,
			                                 Map<String, String[]> parameters,
			                                 Pageable pageable,
			                                 Sort sort ) {
				return null;
			}

			@Override
			public Object invokeQueryMethod( Method method,
			                                 MultiValueMap<String, ? extends Object> parameters,
			                                 Pageable pageable,
			                                 Sort sort ) {
				return null;
			}
		} );


		/*
		configuration.entity( Role.class )
		             .properties()
		             .property( "name", "Key" )
		             .property( "description", "Name" )
		             .and()
		             .view( EntityListView.VIEW_NAME ).properties( "description", "name" );
		             */

	}
}
