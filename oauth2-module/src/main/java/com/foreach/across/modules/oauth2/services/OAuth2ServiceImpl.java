/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.foreach.across.modules.oauth2.services;

import com.foreach.across.modules.oauth2.business.OAuth2Client;
import com.foreach.across.modules.oauth2.business.OAuth2Scope;
import com.foreach.across.modules.oauth2.repositories.OAuth2Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class OAuth2ServiceImpl implements OAuth2Service
{
	@Autowired
	private OAuth2Repository oAuth2Repository;

	@Override
	public Collection<OAuth2Client> getOAuth2Clients() {
		return oAuth2Repository.getOAuth2Clients();
	}

	@Override
	public Collection<OAuth2Scope> getOAuth2Scopes() {
		return oAuth2Repository.getOAuth2Scopes();
	}

	@Override
	public void save( OAuth2Scope oAuth2Scope ) {
		oAuth2Repository.save( oAuth2Scope );
	}

	@Override
	public OAuth2Scope getScopeById( long id ) {
		return oAuth2Repository.getScopeById( id );
	}

	@Override
	public void save( OAuth2Client oAuth2Client ) {
		oAuth2Repository.save( oAuth2Client );
	}

	@Override
	public OAuth2Client getClientById( String clientId ) {
		return oAuth2Repository.getClientById( clientId );
	}
}
