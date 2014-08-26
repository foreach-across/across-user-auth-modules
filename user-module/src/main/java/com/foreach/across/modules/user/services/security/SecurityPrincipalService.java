package com.foreach.across.modules.user.services.security;

import com.foreach.across.modules.spring.security.business.SecurityPrincipal;

/**
 * @author Arne Vandamme
 */
public interface SecurityPrincipalService
{
	<T extends SecurityPrincipal> T getPrincipalById( long id );

	<T extends SecurityPrincipal> T getPrincipalByName( String principalName );
}
