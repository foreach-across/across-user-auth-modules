package com.foreach.across.modules.user.repositories;

import com.foreach.across.modules.hibernate.repositories.BasicRepository;
import com.foreach.across.modules.user.business.NonGroupedPrincipal;
import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalRetrievalStrategy;

/**
 * @author Arne Vandamme
 */
public interface SecurityPrincipalRepository extends BasicRepository<NonGroupedPrincipal>, SecurityPrincipalRetrievalStrategy
{
}
