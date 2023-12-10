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

import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipalUserDetails;
import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalService;
import com.foreach.across.modules.user.business.BasicSecurityPrincipal;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.business.UserDirectory;
import com.foreach.across.modules.user.services.UserDirectoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link UserDetailsService} that dispatches lookups to a backing {@link SecurityPrincipalService}.
 * A lookup by username will loop through all active user directories ({@link UserDirectoryService#getActiveUserDirectories()}
 * and build a unique principal name for that particular directory.  The first non-null {@link UserDetails} implementation
 * returned from the {@link SecurityPrincipalService} will be returned.
 * <p>
 * In case of several directories this lookup will incur a performance hit, adequate caching is advised.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService
{
	private final SecurityPrincipalService securityPrincipalService;
	private final UserDirectoryService userDirectoryService;

	@Autowired
	public UserDetailsServiceImpl( SecurityPrincipalService securityPrincipalService, UserDirectoryService userDirectoryService ) {
		this.securityPrincipalService = securityPrincipalService;
		this.userDirectoryService = userDirectoryService;
	}

	@Override
	public UserDetails loadUserByUsername( String username ) throws UsernameNotFoundException {
		for ( UserDirectory userDirectory : userDirectoryService.getActiveUserDirectories() ) {
			String principalName = buildPrincipalName( username, userDirectory );

			SecurityPrincipal principal = securityPrincipalService.getPrincipalByName( principalName ).orElse( null );

			if ( principal instanceof User ) {
				User user = (User) principal;
				return new SecurityPrincipalUserDetails(
						user.getSecurityPrincipalId(),
						user.getUsername(),
						user.getPassword(),
						user.isEnabled(),
						user.isAccountNonExpired(),
						user.isCredentialsNonExpired(),
						user.isAccountNonLocked(),
						user.getAuthorities()
				);
			}
		}

		throw new UsernameNotFoundException( "No user found with username: " + username );
	}

	private String buildPrincipalName( String username, UserDirectory userDirectory ) {
		return BasicSecurityPrincipal.uniquePrincipalName( username, userDirectory );
	}
}
