package com.foreach.across.modules.user.services.security;

import com.foreach.across.modules.user.business.NonGroupedPrincipal;

/**
 * @author Arne Vandamme
 */
public interface SecurityPrincipalService
{
	/**
	 * Creates an {@link org.springframework.security.core.Authentication} for the
	 * {@link com.foreach.across.modules.spring.security.business.SecurityPrincipal} and sets it
	 * as the security context for the current thread.
	 *
	 * @param principal Principal that should authenticate.
	 */
	void authenticate( NonGroupedPrincipal principal );

	/**
	 * Clears the authentication of the current thread.
	 */
	void clearAuthentication();

	<T extends NonGroupedPrincipal> T getPrincipalById( long id );

	<T extends NonGroupedPrincipal> T getPrincipalByName( String principalName );

	void publishRenameEvent( String oldPrincipalName, String newPrincipalName );
}
