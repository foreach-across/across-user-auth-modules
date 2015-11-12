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
package com.foreach.across.modules.user.dto;

import com.foreach.across.modules.hibernate.dto.IdBasedEntityDto;
import com.foreach.across.modules.user.business.BasicSecurityPrincipal;
import com.foreach.across.modules.user.business.Role;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author Arne Vandamme
 */
public abstract class NonGroupedPrincipalDto<T extends BasicSecurityPrincipal> extends IdBasedEntityDto<T>
{
	private Set<Role> roles = new TreeSet<>();

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles( Set<Role> roles ) {
		this.roles = roles;
	}

	public void addRole( Role role ) {
		roles.add( role );
	}

	public abstract String getPrincipalName();
}
