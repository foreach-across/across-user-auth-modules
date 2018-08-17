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

import com.foreach.across.modules.hibernate.jpa.repositories.IdBasedEntityJpaRepository;
import com.foreach.across.modules.spring.security.acl.SpringSecurityAclModuleCache;
import com.foreach.across.modules.spring.security.acl.business.AclSecurityEntity;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;

import java.util.Optional;

/**
 * @author Arne Vandamme
 */
public interface AclSecurityEntityRepository extends IdBasedEntityJpaRepository<AclSecurityEntity>
{
	@Caching(
			put = {
					@CachePut(value = SpringSecurityAclModuleCache.ACL_SECURITY_ENTITY, key = "#result.id"),
					@CachePut(value = SpringSecurityAclModuleCache.ACL_SECURITY_ENTITY, key = "#result.name")
			}
	)
	@Override
	Optional<AclSecurityEntity> findById( Long id );

	@Caching(
			put = {
					@CachePut(value = SpringSecurityAclModuleCache.ACL_SECURITY_ENTITY, key = "#result.id"),
					@CachePut(value = SpringSecurityAclModuleCache.ACL_SECURITY_ENTITY, key = "#result.name")
			}
	)
	Optional<AclSecurityEntity> findByName( String name );

	@Caching(
			evict = {
					@CacheEvict(value = SpringSecurityAclModuleCache.ACL_SECURITY_ENTITY, key = "#p0.id"),
					@CacheEvict(value = SpringSecurityAclModuleCache.ACL_SECURITY_ENTITY, key = "#p0.name")
			}
	)
	@Override
	<S extends AclSecurityEntity> S save( S aclSecurityEntity );

	@Caching(
			evict = {
					@CacheEvict(value = SpringSecurityAclModuleCache.ACL_SECURITY_ENTITY, key = "#p0.id"),
					@CacheEvict(value = SpringSecurityAclModuleCache.ACL_SECURITY_ENTITY, key = "#p0.name")
			}
	)
	@Override
	<S extends AclSecurityEntity> S saveAndFlush( S aclSecurityEntity );

	@Caching(
			evict = {
					@CacheEvict(value = SpringSecurityAclModuleCache.ACL_SECURITY_ENTITY, key = "#p0.id"),
					@CacheEvict(value = SpringSecurityAclModuleCache.ACL_SECURITY_ENTITY, key = "#p0.name")
			}
	)
	@Override
	void delete( AclSecurityEntity aclSecurityEntity );

	@CacheEvict(value = SpringSecurityAclModuleCache.ACL_SECURITY_ENTITY, allEntries = true)
	@Override
	void deleteById( Long id );

	@CacheEvict(value = SpringSecurityAclModuleCache.ACL_SECURITY_ENTITY, allEntries = true)
	@Override
	void deleteAllInBatch();

	@CacheEvict(value = SpringSecurityAclModuleCache.ACL_SECURITY_ENTITY, allEntries = true)
	@Override
	void deleteInBatch( Iterable<AclSecurityEntity> entities );

	@CacheEvict(value = SpringSecurityAclModuleCache.ACL_SECURITY_ENTITY, allEntries = true)
	@Override
	void deleteAll( Iterable<? extends AclSecurityEntity> entities );

	@CacheEvict(value = SpringSecurityAclModuleCache.ACL_SECURITY_ENTITY, allEntries = true)
	@Override
	void deleteAll();
}
