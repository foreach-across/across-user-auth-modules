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

package com.foreach.across.modules.user.services;

import com.foreach.across.modules.hibernate.jpa.config.HibernateJpaConfiguration;
import com.foreach.across.modules.spring.security.SpringSecurityModuleCache;
import com.foreach.across.modules.user.UserModuleCache;
import com.foreach.across.modules.user.business.Group;
import com.foreach.across.modules.user.business.GroupProperties;
import com.foreach.across.modules.user.business.UserDirectory;
import com.foreach.across.modules.user.repositories.GroupRepository;
import com.foreach.across.modules.user.services.support.DefaultUserDirectoryStrategy;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Arne Vandamme
 */
@Service
public class GroupServiceImpl implements GroupService
{
	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private GroupPropertiesService groupPropertiesService;

	@Autowired
	private DefaultUserDirectoryStrategy defaultUserDirectoryStrategy;

	@Override
	public Collection<Group> getGroups() {
		return groupRepository.findAll( Sort.by( Sort.Direction.ASC, "name" ) );
	}

	@Cacheable(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, unless = SpringSecurityModuleCache.UNLESS_NULLS_ONLY)
	@Override
	public Optional<Group> getGroupById( long id ) {
		return groupRepository.findById( id );
	}

	@Cacheable(value = UserModuleCache.GROUPS, unless = SpringSecurityModuleCache.UNLESS_NULLS_ONLY)
	@Override
	public Optional<Group> getGroupByName( String name ) {
		return getGroupByName( name, defaultUserDirectoryStrategy.getDefaultUserDirectory() );
	}

	@Cacheable(value = UserModuleCache.GROUPS, key = "#p0 + ':' + #p1.id", unless = SpringSecurityModuleCache.UNLESS_NULLS_ONLY)
	@Override
	public Optional<Group> getGroupByName( String name, UserDirectory directory ) {
		return groupRepository.findByNameAndUserDirectory( name, directory );
	}

	@Override
	public Group save( Group groupDto ) {
		defaultUserDirectoryStrategy.apply( groupDto );
		return groupRepository.save( groupDto );
	}

	@Override
	@Transactional(HibernateJpaConfiguration.TRANSACTION_MANAGER)
	public void delete( long groupId ) {
		groupRepository.findById( groupId )
		               .ifPresent( group -> {
			               deleteProperties( groupId );
			               groupRepository.delete( group );
		               } );
	}

	@Override
	public GroupProperties getProperties( Group group ) {
		return groupPropertiesService.getProperties( group.getId() );
	}

	@Override
	public void saveProperties( GroupProperties groupProperties ) {
		groupPropertiesService.saveProperties( groupProperties );
	}

	@Override
	public void deleteProperties( Group group ) {
		deleteProperties( group.getId() );
	}

	@Override
	public void deleteProperties( long groupId ) {
		groupPropertiesService.deleteProperties( groupId );
	}

	@Transactional(readOnly = true, value = HibernateJpaConfiguration.TRANSACTION_MANAGER)
	@Override
	public Collection<Group> getGroupsWithPropertyValue( String propertyName, Object propertyValue ) {
		Collection<Long> groupIds = groupPropertiesService.getEntityIdsForPropertyValue( propertyName, propertyValue );

		if ( groupIds.isEmpty() ) {
			return Collections.emptyList();
		}

		return groupRepository.findAllById( groupIds );
	}

	@Override
	public Collection<Group> findAll( Predicate predicate ) {
		return (Collection<Group>) groupRepository.findAll( predicate );
	}

	@Override
	public Iterable<Group> findAll( Predicate predicate, Sort sort ) {
		return groupRepository.findAll( predicate, sort );
	}

	@Override
	public Collection<Group> findAll( Predicate predicate, OrderSpecifier<?>... orderSpecifiers ) {
		return (Collection<Group>) groupRepository.findAll( predicate, orderSpecifiers );
	}

	@Override
	public Iterable<Group> findAll( OrderSpecifier<?>... orders ) {
		return groupRepository.findAll( orders );
	}

	@Override
	public Page<Group> findAll( Predicate predicate, Pageable pageable ) {
		return groupRepository.findAll( predicate, pageable );
	}

	@Override
	public long count( Predicate predicate ) {
		return groupRepository.count( predicate );
	}

	@Override
	public boolean exists( Predicate predicate ) {
		return groupRepository.exists( predicate );
	}

	@Override
	public <S extends Group, R> R findBy( Predicate predicate, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction ) {
		return groupRepository.findBy( predicate, queryFunction );
	}

	@Override
	public Optional<Group> findOne( Predicate predicate ) {
		return groupRepository.findOne( predicate );
	}
}
