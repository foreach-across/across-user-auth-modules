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
import com.foreach.across.modules.spring.security.SpringSecurityCache;
import com.foreach.across.modules.user.UserModuleCache;
import com.foreach.across.modules.user.business.Group;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.converters.FieldUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Repository
public class UserRepositoryImpl extends BasicRepositoryImpl<User> implements UserRepository
{
	@Caching(
			put = {
					@CachePut(value = UserModuleCache.USERS, key = "'username:' +#result.username", condition = "#result != null"),
					@CachePut(value = SpringSecurityCache.SECURITY_PRINCIPAL, key = "#result.id", condition = "#result != null"),
					@CachePut(value = SpringSecurityCache.SECURITY_PRINCIPAL, key = "#result.principalName", condition = "#result != null")
			}
	)
	@Transactional(readOnly = true)
	@Override
	public User getById( long id ) {
		return super.getById( id );
	}

	@Caching(
			put = {
					@CachePut(value = UserModuleCache.USERS, key = "'username:' + #result.username", condition = "#result != null"),
					@CachePut(value = SpringSecurityCache.SECURITY_PRINCIPAL, key = "#result.id", condition = "#result != null"),
					@CachePut(value = SpringSecurityCache.SECURITY_PRINCIPAL, key = "#result.principalName", condition = "#result != null")
			}
	)
	@Transactional(readOnly = true)
	@Override
	public User getByEmail( String email ) {
		return (User) distinct()
				.add( Restrictions.eq( "email", FieldUtils.lowerCase( email ) ) )
				.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public Collection<User> getUsersInGroup( Group group ) {
		return (Collection<User>) distinct().createAlias( "groups", "group",
		                                                  org.hibernate.sql.JoinType.LEFT_OUTER_JOIN )
		                                    .add( Restrictions.eq( "group.id", group.getId() ) ).list();
	}

	@Caching(
			put = {
					@CachePut(value = UserModuleCache.USERS, key = "'username:' + #result.username", condition = "#result != null"),
					@CachePut(value = SpringSecurityCache.SECURITY_PRINCIPAL, key = "#result.id", condition = "#result != null"),
					@CachePut(value = SpringSecurityCache.SECURITY_PRINCIPAL, key = "#result.principalName", condition = "#result != null")
			}
	)
	@Transactional(readOnly = true)
	@Override
	public User getByUsername( String userName ) {
		return (User) distinct()
				.add( Restrictions.eq( "username", FieldUtils.lowerCase( userName ) ) )
				.uniqueResult();
	}

	@Caching(
			evict = {
					@CacheEvict(value = UserModuleCache.USERS, key = "'username:' + #user.username"),
					@CacheEvict(value = UserModuleCache.USERS, key = "'email:' + #user.email"),
					@CacheEvict(value = SpringSecurityCache.SECURITY_PRINCIPAL, key = "#user.id"),
					@CacheEvict(value = SpringSecurityCache.SECURITY_PRINCIPAL, key = "#user.principalName")
			}
	)
	@Transactional
	@Override
	public void create( User user ) {
		super.create( user );
	}

	@Caching(
			evict = {
					@CacheEvict(value = UserModuleCache.USERS, key = "'username:' + #user.username"),
					@CacheEvict(value = UserModuleCache.USERS, key = "'email:' + #user.email"),
					@CacheEvict(value = SpringSecurityCache.SECURITY_PRINCIPAL, key = "#user.id"),
					@CacheEvict(value = SpringSecurityCache.SECURITY_PRINCIPAL, key = "#user.principalName")
			}
	)
	@Transactional
	@Override
	public void update( User user ) {
		super.update( user );
	}

	@Caching(
			evict = {
					@CacheEvict(value = UserModuleCache.USERS, key = "'username:' + #user.username"),
					@CacheEvict(value = UserModuleCache.USERS, key = "'email:' + #user.email"),
					@CacheEvict(value = SpringSecurityCache.SECURITY_PRINCIPAL, key = "#user.id"),
					@CacheEvict(value = SpringSecurityCache.SECURITY_PRINCIPAL, key = "#user.principalName")
			}
	)
	@Transactional
	@Override
	public void delete( User user ) {
		super.delete( user );
	}

	@Override
	protected Criteria ordered( Criteria criteria ) {
		criteria.addOrder( Order.asc( "displayName" ) )
		        .addOrder( Order.asc( "username" ) )
		        .addOrder( Order.asc( "email" ) );

		return criteria;
	}
}
