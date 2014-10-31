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
package com.foreach.across.modules.user.handlers;

import com.foreach.across.core.annotations.AcrossEventHandler;
import com.foreach.across.modules.adminweb.events.AdminWebUrlRegistry;
import com.foreach.across.modules.adminweb.menu.AdminMenuEvent;
import com.foreach.across.modules.user.controllers.RoleController;
import com.foreach.across.modules.user.controllers.UserController;
import com.foreach.across.modules.user.security.CurrentUserProxy;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import net.engio.mbassy.listener.Handler;
import org.springframework.beans.factory.annotation.Autowired;

@AcrossEventHandler
public class AdminWebEventsHandler
{
	@Autowired
	private CurrentUserProxy currentUser;

	@Handler
	public void secureUrls( AdminWebUrlRegistry urls ) {
		urls.match( UserController.PATH, UserController.PATH + "/*" ).hasAuthority( "manage users" );
		urls.match( RoleController.PATH, RoleController.PATH + "/*" ).hasAuthority( "manage user roles" );
	}

	@Handler
	public void registerMenu( AdminMenuEvent adminMenuEvent ) {
		PathBasedMenuBuilder builder = adminMenuEvent.builder();
		builder.group( "/users", "User management" );

		if ( currentUser.hasPermission( "manage users" ) ) {
			builder.item( "/users/users", "Users", UserController.PATH ).order( 1 ).and()
			       .item( "/users/users/create", "Create a new user", UserController.PATH + "/create" );
		}

		if ( currentUser.hasPermission( "manage user roles" ) ) {
			builder
					.item( "/users/roles", "Roles", RoleController.PATH ).order( 2 ).and()
					.item( "/users/roles/create", "Create a new role", RoleController.PATH + "/create" );
		}
	}
}
