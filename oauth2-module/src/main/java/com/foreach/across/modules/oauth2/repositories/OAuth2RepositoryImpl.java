package com.foreach.across.modules.oauth2.repositories;

import com.foreach.across.modules.oauth2.business.OAuth2Client;
import com.foreach.across.modules.oauth2.business.OAuth2Scope;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Repository
public class OAuth2RepositoryImpl implements OAuth2Repository
{

	@Autowired
	private SessionFactory sessionFactory;

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public Collection<OAuth2Client> getOAuth2Clients() {
		return (Collection<OAuth2Client>) sessionFactory.getCurrentSession().createCriteria( OAuth2Client.class )
		                                                .setResultTransformer(
				                                                Criteria.DISTINCT_ROOT_ENTITY ).list();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public Collection<OAuth2Scope> getOAuth2Scopes() {
		return (Collection<OAuth2Scope>) sessionFactory.getCurrentSession().createCriteria( OAuth2Scope.class )
		                                               .setResultTransformer(
				                                               Criteria.DISTINCT_ROOT_ENTITY ).list();
	}

	@Transactional
	@Override
	public void save( OAuth2Scope oAuth2Scope ) {
		sessionFactory.getCurrentSession().saveOrUpdate( oAuth2Scope );
	}

	@Transactional(readOnly = true)
	@Override
	public OAuth2Scope getScopeById( long id ) {
		return (OAuth2Scope) sessionFactory.getCurrentSession().get( OAuth2Scope.class, id );
	}

	@Transactional
	@Override
	public void save( OAuth2Client oAuth2Client ) {
		sessionFactory.getCurrentSession().saveOrUpdate( oAuth2Client );
	}

	@Transactional(readOnly = true)
	@Override
	public OAuth2Client getClientById( String clientId ) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria( OAuth2Client.class );
		criteria.add( Restrictions.eq( "clientId", clientId ) );

		return (OAuth2Client) criteria.uniqueResult();
	}
}
