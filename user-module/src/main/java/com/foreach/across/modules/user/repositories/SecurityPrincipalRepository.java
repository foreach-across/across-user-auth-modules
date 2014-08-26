package com.foreach.across.modules.user.repositories;

import com.foreach.across.modules.hibernate.repositories.BasicRepository;
import com.foreach.across.modules.user.business.NonGroupedPrincipal;

/**
 * @author Arne Vandamme
 */
public interface SecurityPrincipalRepository extends BasicRepository<NonGroupedPrincipal>
{
	NonGroupedPrincipal getByPrincipalName( String principalName );
}
