package com.foreach.across.modules.user.dto;

import com.foreach.across.modules.user.business.MachinePrincipal;

/**
 * @author Arne Vandamme
 */
public class MachinePrincipalDto extends GroupedPrincipalDto<MachinePrincipal>
{
	private String name;

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}
}
