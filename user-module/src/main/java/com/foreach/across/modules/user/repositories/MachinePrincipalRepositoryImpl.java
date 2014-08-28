package com.foreach.across.modules.user.repositories;

import com.foreach.across.modules.hibernate.repositories.BasicRepositoryImpl;
import com.foreach.across.modules.user.business.MachinePrincipal;
import com.foreach.across.modules.user.converters.FieldUtils;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Arne Vandamme
 */
@Repository
public class MachinePrincipalRepositoryImpl extends BasicRepositoryImpl<MachinePrincipal> implements MachinePrincipalRepository
{
	@Transactional(readOnly = true)
	@Override
	public MachinePrincipal getByName( String name ) {
		return (MachinePrincipal) distinct()
				.add( Restrictions.eq( "principalName", FieldUtils.lowerCase( name ) ) )
				.uniqueResult();
	}
}
