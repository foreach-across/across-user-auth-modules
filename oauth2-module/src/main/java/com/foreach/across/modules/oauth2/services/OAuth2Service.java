package com.foreach.across.modules.oauth2.services;

import com.foreach.across.modules.oauth2.business.OAuth2Client;
import com.foreach.across.modules.oauth2.business.OAuth2Scope;

import java.util.Collection;

public interface OAuth2Service
{
	Collection<OAuth2Client> getOAuth2Clients();

	Collection<OAuth2Scope> getOAuth2Scopes();

	void save( OAuth2Scope oAuth2Scope );

	OAuth2Scope getScopeById( long id );

	void save( OAuth2Client oAuth2Client );

	OAuth2Client getClientById( String clientId );
}