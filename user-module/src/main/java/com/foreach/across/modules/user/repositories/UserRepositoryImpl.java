package com.foreach.across.modules.user.repositories;

import com.foreach.across.modules.hibernate.repositories.BasicRepositoryImpl;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.converters.FieldUtils;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserRepositoryImpl extends BasicRepositoryImpl<User> implements UserRepository
{
	@Transactional(readOnly = true)
	@Override
	public User getByEmail( String email ) {
		return (User) distinct()
				.add( Restrictions.eq( "email", FieldUtils.lowerCase( email ) ) )
				.uniqueResult();
	}

	@Transactional(readOnly = true)
	@Override
	public User getByUsername( String userName ) {
		return (User) distinct()
				.add( Restrictions.eq( "username", FieldUtils.lowerCase( userName ) ) )
				.uniqueResult();
	}
}
