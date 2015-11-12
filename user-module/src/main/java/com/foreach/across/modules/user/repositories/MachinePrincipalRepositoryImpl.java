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
package com.foreach.across.modules.user.repositories;

import com.foreach.across.modules.hibernate.repositories.BasicRepositoryImpl;
import com.foreach.across.modules.spring.security.SpringSecurityModuleCache;
import com.foreach.across.modules.user.business.MachinePrincipal;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Arne Vandamme
 */
@Repository
public class MachinePrincipalRepositoryImpl extends BasicRepositoryImpl<MachinePrincipal> implements MachinePrincipalRepository
{
	@Caching(
			put = {
					@CachePut(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#result.id", condition = "#result != null"),
					@CachePut(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#result.principalName", condition = "#result != null")
			}
	)
	@Transactional(readOnly = true)
	@Override
	public MachinePrincipal getById( long id ) {
		return super.getById( id );
	}

	@Caching(
			evict = {
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#machinePrincipal.id"),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#machinePrincipal.principalName")
			}
	)
	@Transactional
	@Override
	public void create( MachinePrincipal machinePrincipal ) {
		super.create( machinePrincipal );
	}

	@Caching(
			evict = {
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#machinePrincipal.id"),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#machinePrincipal.principalName")
			}
	)
	@Transactional
	@Override
	public void update( MachinePrincipal machinePrincipal ) {
		super.update( machinePrincipal );
	}

	@Caching(
			evict = {
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#machinePrincipal.id"),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#machinePrincipal.principalName")
			}
	)
	@Transactional
	@Override
	public void delete( MachinePrincipal machinePrincipal ) {
		super.delete( machinePrincipal );
	}
}
