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

import com.foreach.across.modules.user.business.Group;
import com.foreach.across.modules.user.business.GroupedPrincipal;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author Arne Vandamme
 */
public class GroupedPrincipalDto<T extends GroupedPrincipal> extends NonGroupedPrincipalDto<T>
{
	private Set<Group> groups = new TreeSet<>();

	public Set<Group> getGroups() {
		return groups;
	}

	public void setGroups( Set<Group> groups ) {
		this.groups = groups;
	}

	public void addGroup( Group group ) {
		groups.add( group );
	}
}
