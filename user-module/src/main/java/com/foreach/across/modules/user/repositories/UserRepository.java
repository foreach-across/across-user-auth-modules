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
import com.foreach.across.modules.user.business.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository extends IdBasedEntityJpaRepository<User>, QuerydslPredicateExecutor<User>
{
	@Caching(
			put = {
					@CachePut(value = UserModuleCache.USERS, key = "'username:' + #result.get().username", condition = "#result.isPresent()"),
					@CachePut(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#result.get().id", condition = "#result.isPresent()"),
					@CachePut(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#result.get().principalName", condition = "#result.isPresent()")
			}
	)
	Optional<User> findByUsername( String userName );

	@Caching(
			put = {
					@CachePut(value = UserModuleCache.USERS, key = "'username:' + #result.get().username", condition = "#result.isPresent()"),
					@CachePut(value = UserModuleCache.USERS, key = "'email:' + #result.get().email", condition = "#result.isPresent()"),
					@CachePut(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#result.get().id", condition = "#result.isPresent()"),
					@CachePut(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#result.get().principalName", condition = "#result.isPresent()")
			}
	)
	Optional<User> findByEmail( String email );

	Collection<User> findAllByGroups( Group group );

	@Caching(
			put = {
					@CachePut(value = UserModuleCache.USERS, key = "'username:' + #result.get().username"),
					@CachePut(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#result.get().id"),
					@CachePut(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#result.get().principalName")
			}
	)
	@Override
	Optional<User> findById( Long id );

	@Caching(
			evict = {
					@CacheEvict(value = UserModuleCache.USERS, key = "'username:' + #p0.username"),
					@CacheEvict(value = UserModuleCache.USERS, key = "'email:' + #p0.email"),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#p0.id"),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#p0.principalName")
			}
	)
	@Override
	<S extends User> S save( S user );

	@Caching(
			evict = {
					@CacheEvict(value = UserModuleCache.USERS, key = "'username:' + #p0.username"),
					@CacheEvict(value = UserModuleCache.USERS, key = "'email:' + #p0.email"),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#p0.id"),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#p0.principalName")
			}
	)
	@Override
	<S extends User> S saveAndFlush( S user );

	@Caching(
			evict = {
					@CacheEvict(value = UserModuleCache.USERS, key = "'username:' + #p0.username"),
					@CacheEvict(value = UserModuleCache.USERS, key = "'email:' + #p0.email"),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#p0.id"),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#p0.principalName")
			}
	)
	@Override
	void delete( User user );

	@Caching(
			evict = {
					@CacheEvict(value = UserModuleCache.USERS, allEntries = true),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, allEntries = true)
			}
	)
	@Override
	void deleteById( Long id );

	@Caching(
			evict = {
					@CacheEvict(value = UserModuleCache.USERS, allEntries = true),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, allEntries = true)
			}
	)
	@Override
	void deleteAllInBatch();

	@Caching(
			evict = {
					@CacheEvict(value = UserModuleCache.USERS, allEntries = true),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, allEntries = true)
			}
	)
	@Override
	void deleteInBatch( Iterable<User> entities );

	@Caching(
			evict = {
					@CacheEvict(value = UserModuleCache.USERS, allEntries = true),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, allEntries = true)
			}
	)
	@Override
	void deleteAll( Iterable<? extends User> entities );

	@Caching(
			evict = {
					@CacheEvict(value = UserModuleCache.USERS, allEntries = true),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, allEntries = true)
			}
	)
	@Override
	void deleteAll();
}
