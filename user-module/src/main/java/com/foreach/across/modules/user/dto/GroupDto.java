package com.foreach.across.modules.user.dto;

import com.foreach.across.modules.user.business.Group;

/**
 * @author Arne Vandamme
 */
public class GroupDto extends NonGroupedPrincipalDto<Group>
{
	private String name;

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}
}
