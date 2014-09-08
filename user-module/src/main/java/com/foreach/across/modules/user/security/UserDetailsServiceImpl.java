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
package com.foreach.across.modules.user.security;

import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService
{
	@Autowired
	private UserService userService;

	@Override
	public UserDetails loadUserByUsername( String username ) throws UsernameNotFoundException {
		User user = retrieveUser( username );

		if ( user == null ) {
			throw new UsernameNotFoundException( "No user found with username: " + username );
		}

		return user;
	}

	private User retrieveUser( String usernameOrEmail ) {
		if ( userService.isUseEmailAsUsername() ) {
			User candidate = userService.getUserByEmail( usernameOrEmail );

			if ( candidate != null ) {
				return candidate;
			}
		}

		return userService.getUserByUsername( usernameOrEmail );
	}
}
