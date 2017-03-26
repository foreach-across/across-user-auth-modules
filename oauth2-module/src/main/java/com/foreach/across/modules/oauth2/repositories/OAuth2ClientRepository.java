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

import com.foreach.across.modules.hibernate.jpa.repositories.IdBasedEntityJpaRepository;
import com.foreach.across.modules.oauth2.OAuth2ModuleCache;
import com.foreach.across.modules.oauth2.business.OAuth2Client;
import com.foreach.across.modules.spring.security.SpringSecurityModuleCache;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import java.util.List;

public interface OAuth2ClientRepository
		extends IdBasedEntityJpaRepository<OAuth2Client>, QueryDslPredicateExecutor<OAuth2Client>
{
	@Caching(
			put = {
					@CachePut(value = OAuth2ModuleCache.CLIENTS, key = "#result.clientId", condition = "#result != null"),
					@CachePut(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#result.id", condition = "#result != null"),
					@CachePut(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#result.principalName", condition = "#result != null")
			}
	)
	@Override
	OAuth2Client findOne( Long id );

	@Caching(
			put = {
					@CachePut(value = OAuth2ModuleCache.CLIENTS, key = "#result.clientId", condition = "#result != null"),
					@CachePut(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#result.id", condition = "#result != null"),
					@CachePut(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#result.principalName", condition = "#result != null")
			}
	)
	OAuth2Client findByClientId( String clientId );

	@Caching(
			put = {
					@CachePut(value = OAuth2ModuleCache.CLIENTS, key = "#result.clientId", condition = "#result != null"),
					@CachePut(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#result.id", condition = "#result != null"),
					@CachePut(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#result.principalName", condition = "#result != null")
			}
	)
	OAuth2Client findOneByPrincipalName( String principalName );

	@Caching(
			evict = {
					@CacheEvict(value = OAuth2ModuleCache.CLIENTS, key = "#p0.clientId"),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#p0.id"),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#p0.principalName")
			}
	)
	@Override
	<S extends OAuth2Client> S save( S client );

	@Caching(
			evict = {
					@CacheEvict(value = OAuth2ModuleCache.CLIENTS, key = "#p0.clientId"),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#p0.id"),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#p0.principalName")
			}
	)
	@Override
	<S extends OAuth2Client> S saveAndFlush( S client );

	@Caching(
			evict = {
					@CacheEvict(value = OAuth2ModuleCache.CLIENTS, allEntries = true),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, allEntries = true)
			}
	)
	@Override
	<S extends OAuth2Client> List<S> save( Iterable<S> entities );

	@Caching(
			evict = {
					@CacheEvict(value = OAuth2ModuleCache.CLIENTS, key = "#p0.clientId"),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#p0.id"),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#p0.principalName")
			}
	)
	@Override
	void delete( OAuth2Client client );

	@Caching(
			evict = {
					@CacheEvict(value = OAuth2ModuleCache.CLIENTS, allEntries = true),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, allEntries = true)
			}
	)
	@Override
	void delete( Long id );

	@Caching(
			evict = {
					@CacheEvict(value = OAuth2ModuleCache.CLIENTS, allEntries = true),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, allEntries = true)
			}
	)
	@Override
	void deleteAllInBatch();

	@Caching(
			evict = {
					@CacheEvict(value = OAuth2ModuleCache.CLIENTS, allEntries = true),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, allEntries = true)
			}
	)
	@Override
	void deleteInBatch( Iterable<OAuth2Client> entities );

	@Caching(
			evict = {
					@CacheEvict(value = OAuth2ModuleCache.CLIENTS, allEntries = true),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, allEntries = true)
			}
	)
	@Override
	void delete( Iterable<? extends OAuth2Client> entities );

	@Caching(
			evict = {
					@CacheEvict(value = OAuth2ModuleCache.CLIENTS, allEntries = true),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, allEntries = true)
			}
	)
	@Override
	void deleteAll();
}
