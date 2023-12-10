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

package com.foreach.across.modules.oauth2.test.application.controllers;

import com.foreach.across.modules.user.business.User;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Marc Vanbrabant
 * @since 1.1.2
 */
@RestController
public class PreAuthorizedController
{
	@RequestMapping(value = "/api/testshouldbeauthenticated", produces = { MediaType.APPLICATION_JSON_VALUE })
	@PreAuthorize("isAuthenticated() && hasRole('ROLE_ADMIN')")
	public ResponseEntity<UserResponse> authenticatedPage( OAuth2Authentication authentication ) {
		UserResponse response = new UserResponse();
		BeanUtils.copyProperties( authentication.getPrincipal(), response, "roles" );
		return new ResponseEntity<>( response, HttpStatus.OK );
	}

	public static class UserResponse extends User
	{
	}
}
