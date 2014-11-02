package com.foreach.across.modules.oauth2.repositories;

import com.foreach.across.modules.hibernate.repositories.BasicRepositoryImpl;
import com.foreach.across.modules.oauth2.business.OAuth2Client;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class OAuth2ClientRepositoryImpl extends BasicRepositoryImpl<OAuth2Client> implements OAuth2ClientRepository
{
	@Transactional
	@Override
	public void save( OAuth2Client oAuth2Client ) {
		session().saveOrUpdate( oAuth2Client );
	}

	@Transactional(readOnly = true)
	@Override
	public OAuth2Client getByClientId( String clientId ) {
		return (OAuth2Client) distinct()
				.add( Restrictions.eq( "clientId", clientId ) )
				.uniqueResult();
	}
}
