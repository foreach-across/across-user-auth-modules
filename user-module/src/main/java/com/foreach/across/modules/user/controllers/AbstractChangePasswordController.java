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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

/**
 * @author Sander Van Loock
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractChangePasswordController
{
	private ChangePasswordControllerConfiguration configuration = new ChangePasswordControllerConfiguration();

	@GetMapping
	public String changePassword( @ModelAttribute("email") String email, ModelMap model ) {
		model.addAttribute( configuration );
		return configuration.getChangePasswordForm();
	}

	@PostMapping
	public String changePassword( String email ) {
		return "";
	}

	@GetMapping(path = "/change")
	public String doChange( String code,
	                        PasswordResetDto dto ) {
		return "";
	}

	@PostMapping(path = "/change")
	public String doChange(
			ModelMap model,
			@RequestParam("code") String code,
			@Valid @ModelAttribute("dto") PasswordResetDto request ) {
//		User user = userService.getUserByEmail( email );
//		if ( user != null ) {
//			LOG.debug( "Changing password of user {}", user );
//			User userDto = user.toDto();
//			userDto.setPassword( request.getPassword() );
//			userService.save( userDto );
//		}
		return "";
	}

	private class PasswordResetDto
	{
	}
}
