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

import com.foreach.across.modules.hibernate.repositories.BasicRepositoryImpl;
import com.foreach.across.modules.oauth2.OAuth2ModuleCache;
import com.foreach.across.modules.oauth2.business.OAuth2Client;
import com.foreach.across.modules.spring.security.SpringSecurityModuleCache;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class OAuth2ClientRepositoryImpl extends BasicRepositoryImpl<OAuth2Client> implements OAuth2ClientRepository
{
	@Caching(
			put = {
					@CachePut(value = OAuth2ModuleCache.CLIENTS, key = "#result.clientId", condition = "#result != null"),
					@CachePut(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#result.id", condition = "#result != null"),
					@CachePut(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#result.principalName", condition = "#result != null")
			}
	)
	@Transactional(readOnly = true)
	@Override
	public OAuth2Client getById( long id ) {
		return super.getById( id );
	}

	@Caching(
			evict = {
					@CacheEvict(value = OAuth2ModuleCache.CLIENTS, key = "#client.clientId"),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#client.id"),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#client.principalName")
			}
	)
	@Transactional
	@Override
	public void save( OAuth2Client client ) {
		session().saveOrUpdate( client );
	}

	@Caching(
			put = {
					@CachePut(value = OAuth2ModuleCache.CLIENTS, key = "#result.clientId", condition = "#result != null"),
					@CachePut(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#result.id", condition = "#result != null"),
					@CachePut(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#result.principalName", condition = "#result != null")
			}
	)
	@Transactional(readOnly = true)
	@Override
	public OAuth2Client getByClientId( String clientId ) {
		return (OAuth2Client) distinct()
				.add( Restrictions.eq( "clientId", clientId ) )
				.uniqueResult();
	}
}
