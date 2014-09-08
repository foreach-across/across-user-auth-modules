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

import com.foreach.across.modules.user.business.Permission;
import com.foreach.across.modules.user.business.Role;
import com.foreach.across.modules.user.business.User;

/**
 * Provides access to the user attached to the request or current thread.
 */
public interface CurrentUserProxy
{
	long getId();

	String getEmail();

	String getUsername();

	boolean hasRole( String name );

	boolean hasRole( Role role );

	boolean hasPermission( String name );

	boolean hasPermission( Permission permission );

	boolean hasAuthority( String name );

	User getUser();

	boolean isAuthenticated();
}
