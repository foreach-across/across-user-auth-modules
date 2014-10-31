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
package com.foreach.across.modules.user.controllers;

import com.foreach.across.modules.adminweb.AdminWeb;
import com.foreach.across.modules.adminweb.annotations.AdminWebController;
import com.foreach.across.modules.adminweb.menu.AdminMenu;
import com.foreach.across.modules.user.UserModuleSettings;
import com.foreach.across.modules.user.business.UserRestriction;
import com.foreach.across.modules.user.dto.UserDto;
import com.foreach.across.modules.user.services.RoleService;
import com.foreach.across.modules.user.services.UserService;
import com.foreach.across.modules.user.services.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.EnumSet;

@AdminWebController
@RequestMapping(UserController.PATH)
public class UserController
{
	public static final String PATH = "/users";

	@Autowired
	private AdminWeb adminWeb;

	@Autowired
	private UserService userService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private UserValidator userValidator;

	@Autowired
	private UserModuleSettings userModuleSettings;

	@InitBinder("user")
	protected void initBinder( WebDataBinder binder ) {
		binder.setValidator( userValidator );
	}

	@RequestMapping
	public String listUsers( Model model ) {
		model.addAttribute( "users", userService.getUsers() );

		return "th/user/users/list";
	}

	@RequestMapping("/create")
	public String createUser( Model model ) {
		model.addAttribute( "existing", false );
		model.addAttribute( "user", new UserDto() );
		model.addAttribute( "roles", roleService.getRoles() );
		model.addAttribute( "userRestrictions", EnumSet.allOf( UserRestriction.class ) );

		return "th/user/users/edit";
	}

	@RequestMapping("/{id}")
	public String editUser( @PathVariable("id") long id, AdminMenu adminMenu, Model model ) {
		UserDto user = userService.createUserDto( userService.getUserById( id ) );

		breadcrumb( adminMenu, user );

		model.addAttribute( "existing", true );
		model.addAttribute( "user", user );
		model.addAttribute( "roles", roleService.getRoles() );
		model.addAttribute( "userRestrictions", EnumSet.allOf( UserRestriction.class ) );

		return "th/user/users/edit";
	}

	@RequestMapping(value = { "/create", "/{id}" }, method = RequestMethod.POST)
	public String saveUser( @ModelAttribute("user") @Valid UserDto user,
	                        BindingResult bindingResult,
	                        RedirectAttributes re,
	                        Model model ) {
		if ( !bindingResult.hasErrors() ) {
			userService.save( user );

			re.addAttribute( "userId", user.getId() );

			return adminWeb.redirect( "/users/{userId}" );
		}
		else {
			model.addAttribute( "errors", bindingResult.getAllErrors() );

			model.addAttribute( "existing", true );
			model.addAttribute( "user", user );
			model.addAttribute( "roles", roleService.getRoles() );
			model.addAttribute( "userRestrictions", EnumSet.allOf( UserRestriction.class ) );

			return "th/user/users/edit";
		}
	}

	private void breadcrumb( AdminMenu adminMenu, UserDto user ) {
		if ( !user.isNewEntity() ) {
			adminMenu.getLowestSelectedItem()
			         .addItem( "/selectedUser",
			                   userModuleSettings.isUseEmailAsUsername() ? user.getEmail() : user.getUsername() )
			         .setSelected( true );
		}
	}
}
