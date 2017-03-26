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

import com.foreach.across.modules.oauth2.OAuth2ModuleCache;
import com.foreach.across.modules.oauth2.business.OAuth2ClientScope;
import com.foreach.across.modules.oauth2.business.OAuth2ClientScopeId;
import com.foreach.across.modules.spring.security.SpringSecurityModuleCache;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public interface OAuth2ClientScopeRepository extends JpaRepository<OAuth2ClientScope, OAuth2ClientScopeId>, JpaSpecificationExecutor<OAuth2ClientScope>
{
	@Caching(
			evict = {
					@CacheEvict(value = OAuth2ModuleCache.CLIENTS, key = "#p0.id.OAuth2Client.clientId"),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#p0.id.OAuth2Client.id"),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#p0.id.OAuth2Client.principalName")
			}
	)
	@Override
	<S extends OAuth2ClientScope> S save( S entity );

	@Caching(
			evict = {
					@CacheEvict(value = OAuth2ModuleCache.CLIENTS, key = "#p0.id.OAuth2Client.clientId"),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#p0.id.OAuth2Client.id"),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#p0.id.OAuth2Client.principalName")
			}
	)
	@Override
	<S extends OAuth2ClientScope> S saveAndFlush( S entity );

	@Caching(
			evict = {
					@CacheEvict(value = OAuth2ModuleCache.CLIENTS, allEntries = true),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, allEntries = true)
			}
	)
	@Override
	<S extends OAuth2ClientScope> List<S> save( Iterable<S> entities );

	@Caching(
			evict = {
					@CacheEvict(value = OAuth2ModuleCache.CLIENTS, allEntries = true),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, allEntries = true)
			}
	)
	@Override
	void deleteInBatch( Iterable<OAuth2ClientScope> entities );

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
					@CacheEvict(value = OAuth2ModuleCache.CLIENTS, key = "#p0.OAuth2Client.clientId"),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#p0.OAuth2Client.id"),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#p0.OAuth2Client.principalName")
			}
	)
	@Override
	void delete( OAuth2ClientScopeId oAuth2ClientScopeId );

	@Caching(
			evict = {
					@CacheEvict(value = OAuth2ModuleCache.CLIENTS, key = "#p0.id.OAuth2Client.clientId"),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#p0.id.OAuth2Client.id"),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#p0.id.OAuth2Client.principalName")
			}
	)
	@Override
	void delete( OAuth2ClientScope entity );

	@Caching(
			evict = {
					@CacheEvict(value = OAuth2ModuleCache.CLIENTS, allEntries = true),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, allEntries = true)
			}
	)
	@Override
	void delete( Iterable<? extends OAuth2ClientScope> entities );

	@Caching(
			evict = {
					@CacheEvict(value = OAuth2ModuleCache.CLIENTS, allEntries = true),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, allEntries = true)
			}
	)
	@Override
	void deleteAll();
}
