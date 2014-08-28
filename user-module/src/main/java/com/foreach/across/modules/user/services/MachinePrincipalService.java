package com.foreach.across.modules.user.services;

import com.foreach.across.modules.user.business.MachinePrincipal;
import com.foreach.across.modules.user.dto.MachinePrincipalDto;

import java.util.Collection;

/**
 * @author Arne Vandamme
 */
public interface MachinePrincipalService
{
	Collection<MachinePrincipal> getMachinePrincipals();

	MachinePrincipal getMachinePrincipalById( long id );

	MachinePrincipal getMachinePrincipalByName( String name );

	MachinePrincipal save( MachinePrincipalDto machinePrincipalDto );
}
