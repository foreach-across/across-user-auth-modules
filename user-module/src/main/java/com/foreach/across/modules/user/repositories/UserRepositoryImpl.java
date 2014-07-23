package com.foreach.across.modules.user.repositories;

import com.foreach.across.modules.user.business.User;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Repository
public class UserRepositoryImpl implements UserRepository
{
	@Autowired
	private SessionFactory sessionFactory;

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public Collection<User> getUsers() {
		return (Collection<User>) sessionFactory.getCurrentSession().createCriteria( User.class ).setResultTransformer(
				Criteria.DISTINCT_ROOT_ENTITY ).list();
	}

	@Transactional(readOnly = true)
	@Override
	public User getUserById( long id ) {
		return (User) sessionFactory.getCurrentSession().get( User.class, id );
	}

	@Transactional(readOnly = true)
	@Override
	public User getUserByEmail( String email ) {
		if( StringUtils.isNotBlank( email ) ) {
			//TODO #6 don't do this here, use a @Type(type=TypeTrimmedLowerCase) ?
			email = StringUtils.lowerCase( email );
			email = StringUtils.trimToEmpty( email );
		}
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria( User.class );
		criteria.add( Restrictions.eq( "email", email ) );
		return (User) criteria.uniqueResult();
	}

	@Transactional(readOnly = true)
	@Override
	public User getUserByUsername( String userName ) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria( User.class );
		criteria.add( Restrictions.eq( "username", userName ) );

		return (User) criteria.uniqueResult();
	}

	@Transactional
	@Override
	public void update( User user ) {
		sessionFactory.getCurrentSession().update( user );
	}

	@Transactional
	@Override
	public void create( User user ) {
		sessionFactory.getCurrentSession().save( user );
	}

	@Transactional
	@Override
	public void delete( User user ) {
        user.setDeleted( true );
        sessionFactory.getCurrentSession().saveOrUpdate( user );
	}

}
