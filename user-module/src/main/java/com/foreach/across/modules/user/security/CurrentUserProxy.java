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

import com.foreach.across.modules.spring.security.infrastructure.services.CurrentSecurityPrincipalProxy;
import com.foreach.across.modules.user.business.Group;
import com.foreach.across.modules.user.business.Permission;
import com.foreach.across.modules.user.business.Role;
import com.foreach.across.modules.user.business.User;
import org.springframework.security.core.GrantedAuthority;

/**
 * Provides access to the authenticated user attached to the request or current thread.
 * Requires the authenticated principal to be of type {@link User}, calls to {@link #isAuthenticated()} should
 * return {@code false} if that is not the case.
 * <p/>
 * Unless you are only interested in {@link User} authentications,
 * favour the more generic {@link CurrentSecurityPrincipalProxy} instead.
 *
 * @see CurrentSecurityPrincipalProxy
 */
public interface CurrentUserProxy
{
	Long getId();

	String getEmail();

	String getUsername();

	boolean isMemberOf( Group group );

	boolean hasRole( String name );

	boolean hasRole( Role role );

	boolean hasPermission( String name );

	boolean hasPermission( Permission permission );

	boolean hasAuthority( String authority );

	boolean hasAuthority( GrantedAuthority authority );

	User getUser();

	boolean isAuthenticated();
}
