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
import com.foreach.across.modules.user.UserModuleCache;
import com.foreach.across.modules.user.business.Group;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
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
public class GroupRepositoryImpl extends BasicRepositoryImpl<Group> implements GroupRepository
{
	@Caching(
			put = {
					@CachePut(value = UserModuleCache.GROUPS, key = "#result.name", condition = "#result != null"),
					@CachePut(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#result.id", condition = "#result != null"),
					@CachePut(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#result.principalName", condition = "#result != null")
			}
	)
	@Transactional(readOnly = true)
	@Override
	public Group getById( long id ) {
		return super.getById( id );
	}

	@Caching(
			put = {
					@CachePut(value = UserModuleCache.GROUPS, key = "#result.name", condition = "#result != null"),
					@CachePut(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#result.id", condition = "#result != null"),
					@CachePut(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#result.principalName", condition = "#result != null")
			}
	)
	@Transactional(readOnly = true)
	@Override
	public Group getByName( String name ) {
		return (Group) distinct()
				.add( Restrictions.eq( "name", name ) )
				.uniqueResult();
	}

	@Caching(
			evict = {
					@CacheEvict(value = UserModuleCache.GROUPS, allEntries = true),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#group.id"),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#group.principalName")
			}
	)
	@Transactional
	@Override
	public void create( Group group ) {
		super.create( group );
	}

	@Caching(
			evict = {
					@CacheEvict(value = UserModuleCache.GROUPS, allEntries = true),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#group.id"),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#group.principalName")
			}
	)
	@Override
	public void update( Group group ) {
		super.update( group );
	}

	@Caching(
			evict = {
					@CacheEvict(value = UserModuleCache.GROUPS, allEntries = true),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#group.id"),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#group.principalName")
			}
	)
	@Override
	public void delete( Group group ) {
		super.delete( group );
	}

	@Override
	protected Criteria ordered( Criteria criteria ) {
		criteria.addOrder( Order.asc( "name" ) );

		return criteria;
	}
}
