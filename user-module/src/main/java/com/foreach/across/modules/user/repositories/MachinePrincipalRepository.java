package com.foreach.across.modules.user.repositories;

import com.foreach.across.modules.hibernate.repositories.BasicRepository;
import com.foreach.across.modules.user.business.MachinePrincipal;

/**
 * @author Arne Vandamme
 */
public interface MachinePrincipalRepository extends BasicRepository<MachinePrincipal>
{
	MachinePrincipal getByName( String name );
}
