package com.foreach.across.modules.user.repositories;

import com.foreach.across.modules.hibernate.repositories.BasicRepositoryImpl;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import com.foreach.across.modules.user.business.NonGroupedPrincipal;
import com.foreach.across.modules.user.converters.FieldUtils;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Arne Vandamme
 */
@Repository
public class SecurityPrincipalRepositoryImpl extends BasicRepositoryImpl<NonGroupedPrincipal> implements SecurityPrincipalRepository
{
	@Override
	@Transactional(readOnly = true)
	public SecurityPrincipal getPrincipalByName( String principalName ) {
		return (NonGroupedPrincipal) distinct()
				.add( Restrictions.eq( "principalName", FieldUtils.lowerCase( principalName ) ) )
				.uniqueResult();
	}
}
