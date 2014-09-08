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
