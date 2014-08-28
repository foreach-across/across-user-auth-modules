package com.foreach.across.modules.user.business;

import com.foreach.across.modules.hibernate.business.IdBasedEntity;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;

/**
 * @author Arne Vandamme
 */
public interface IdBasedSecurityPrincipal extends SecurityPrincipal, IdBasedEntity
{
}
