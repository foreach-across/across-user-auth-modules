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

import com.foreach.across.modules.hibernate.jpa.repositories.IdBasedEntityJpaRepository;
import com.foreach.across.modules.spring.security.SpringSecurityModuleCache;
import com.foreach.across.modules.user.UserModuleCache;
import com.foreach.across.modules.user.business.Group;
import com.foreach.across.modules.user.business.UserDirectory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

/**
 * @author Arne Vandamme
 */
public interface GroupRepository extends IdBasedEntityJpaRepository<Group>, QuerydslPredicateExecutor<Group>
{
	String GROUP_KEY = "#result.get().userDirectory.id + ':' + #result.get().name";

	@Caching(
			put = {
					@CachePut(value = UserModuleCache.GROUPS, key = GROUP_KEY, condition = "#result.isPresent()"),
					@CachePut(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#result.get().id", condition = "#result.isPresent()"),
					@CachePut(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#result.get().principalName", condition = "#result.isPresent()")
			}
	)
	Optional<Group> findByNameAndUserDirectory( String name, UserDirectory userDirectory );

	@Caching(
			put = {
					@CachePut(value = UserModuleCache.GROUPS, key = GROUP_KEY, condition = "#result.isPresent()"),
					@CachePut(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#result.get().id", condition = "#result.isPresent()"),
					@CachePut(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#result.get().principalName", condition = "#result.isPresent()")
			}
	)
	@Override
	Optional<Group> findById( Long id );

	@Caching(
			evict = {
					@CacheEvict(value = UserModuleCache.GROUPS, allEntries = true),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#p0.id"),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#p0.principalName")
			}
	)
	@Override
	<S extends Group> S save( S group );

	@Caching(
			evict = {
					@CacheEvict(value = UserModuleCache.GROUPS, allEntries = true),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#p0.id"),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#p0.principalName")
			}
	)
	@Override
	<S extends Group> S saveAndFlush( S group );

	@Caching(
			evict = {
					@CacheEvict(value = UserModuleCache.GROUPS, allEntries = true),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#p0.id"),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#p0.principalName")
			}
	)
	@Override
	void delete( Group group );

	@Caching(
			evict = {
					@CacheEvict(value = UserModuleCache.GROUPS, allEntries = true),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, allEntries = true)
			}
	)
	@Override
	void deleteById( Long id );

	@Caching(
			evict = {
					@CacheEvict(value = UserModuleCache.GROUPS, allEntries = true),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, allEntries = true)
			}
	)
	@Override
	void deleteAllInBatch();

	@Caching(
			evict = {
					@CacheEvict(value = UserModuleCache.GROUPS, allEntries = true),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, allEntries = true)
			}
	)
	@Override
	void deleteInBatch( Iterable<Group> entities );

	@Caching(
			evict = {
					@CacheEvict(value = UserModuleCache.GROUPS, allEntries = true),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, allEntries = true)
			}
	)
	@Override
	void deleteAll( Iterable<? extends Group> entities );

	@Caching(
			evict = {
					@CacheEvict(value = UserModuleCache.GROUPS, allEntries = true),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, allEntries = true)
			}
	)
	@Override
	void deleteAll();
}
