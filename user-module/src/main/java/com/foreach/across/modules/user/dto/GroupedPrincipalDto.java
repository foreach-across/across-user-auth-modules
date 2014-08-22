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
