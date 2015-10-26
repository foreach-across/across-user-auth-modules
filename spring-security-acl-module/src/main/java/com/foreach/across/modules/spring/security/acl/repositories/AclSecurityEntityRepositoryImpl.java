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
package com.foreach.across.modules.spring.security.acl.repositories;

import com.foreach.across.modules.hibernate.repositories.BasicRepositoryImpl;
import com.foreach.across.modules.spring.security.acl.SpringSecurityAclModuleCache;
import com.foreach.across.modules.spring.security.acl.business.AclSecurityEntity;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Arne Vandamme
 */
@Repository
public class AclSecurityEntityRepositoryImpl
		extends BasicRepositoryImpl<AclSecurityEntity>
		implements AclSecurityEntityRepository
{
	@Caching(
			put = {
					@CachePut(value = SpringSecurityAclModuleCache.ACL_SECURITY_ENTITY, key = "#result.id", condition = "#result != null"),
					@CachePut(value = SpringSecurityAclModuleCache.ACL_SECURITY_ENTITY, key = "#result.name", condition = "#result != null")
			}
	)
	@Transactional(readOnly = true)
	@Override
	public AclSecurityEntity getById( long id ) {
		return super.getById( id );
	}

	@Caching(
			put = {
					@CachePut(value = SpringSecurityAclModuleCache.ACL_SECURITY_ENTITY, key = "#result.id", condition = "#result != null"),
					@CachePut(value = SpringSecurityAclModuleCache.ACL_SECURITY_ENTITY, key = "#result.name", condition = "#result != null")
			}
	)
	@Transactional(readOnly = true)
	@Override
	public AclSecurityEntity getByName( String name ) {
		return (AclSecurityEntity) distinct()
				.add( Restrictions.eq( "name", name ) )
				.uniqueResult();
	}

	@Caching(
			evict = {
					@CacheEvict(value = SpringSecurityAclModuleCache.ACL_SECURITY_ENTITY, key = "#entity.id"),
					@CacheEvict(value = SpringSecurityAclModuleCache.ACL_SECURITY_ENTITY, key = "#entity.name")
			}
	)
	@Transactional
	@Override
	public void create( AclSecurityEntity entity ) {
		super.create( entity );
	}

	@Caching(
			evict = {
					@CacheEvict(value = SpringSecurityAclModuleCache.ACL_SECURITY_ENTITY, key = "#entity.id"),
					@CacheEvict(value = SpringSecurityAclModuleCache.ACL_SECURITY_ENTITY, key = "#entity.name")
			}
	)
	@Transactional
	@Override
	public void update( AclSecurityEntity entity ) {
		super.update( entity );
	}

	@Caching(
			evict = {
					@CacheEvict(value = SpringSecurityAclModuleCache.ACL_SECURITY_ENTITY, key = "#entity.id"),
					@CacheEvict(value = SpringSecurityAclModuleCache.ACL_SECURITY_ENTITY, key = "#entity.name")
			}
	)
	@Transactional
	@Override
	public void delete( AclSecurityEntity entity ) {
		super.delete( entity );
	}
}
