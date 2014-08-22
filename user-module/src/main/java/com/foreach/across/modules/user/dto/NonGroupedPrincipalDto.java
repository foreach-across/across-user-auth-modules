package com.foreach.across.modules.user.dto;

import com.foreach.across.modules.hibernate.dto.IdBasedEntityDto;
import com.foreach.across.modules.user.business.NonGroupedPrincipal;
import com.foreach.across.modules.user.business.Role;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author Arne Vandamme
 */
public class NonGroupedPrincipalDto<T extends NonGroupedPrincipal> extends IdBasedEntityDto<T>
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
}
