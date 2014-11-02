package com.foreach.across.modules.oauth2.repositories;

import com.foreach.across.modules.hibernate.repositories.BasicRepository;
import com.foreach.across.modules.oauth2.business.OAuth2Client;

public interface OAuth2ClientRepository extends BasicRepository<OAuth2Client>
{
	void save( OAuth2Client oAuth2Client );

	OAuth2Client getByClientId( String clientId );
}
