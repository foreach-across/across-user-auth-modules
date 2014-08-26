package com.foreach.across.modules.user.services.security;

import com.foreach.across.modules.user.business.NonGroupedPrincipal;

/**
 * @author Arne Vandamme
 */
public interface SecurityPrincipalService
{
	<T extends NonGroupedPrincipal> T getPrincipalById( long id );

	<T extends NonGroupedPrincipal> T getPrincipalByName( String principalName );

	void publishRenameEvent( String oldPrincipalName, String newPrincipalName );
}
