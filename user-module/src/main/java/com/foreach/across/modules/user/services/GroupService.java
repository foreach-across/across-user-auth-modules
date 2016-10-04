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

import com.foreach.across.modules.user.business.Group;
import com.foreach.across.modules.user.business.GroupProperties;
import com.foreach.across.modules.user.business.UserDirectory;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import java.util.Collection;

/**
 * @author Arne Vandamme
 */
public interface GroupService extends QueryDslPredicateExecutor<Group>
{
	Collection<Group> getGroups();

	Group getGroupById( long id );

	/**
	 * Get the group with a given name in the default directory.
	 *
	 * @param name of the group
	 * @return instance or {@code null} if not found
	 */
	Group getGroupByName( String name );

	/**
	 * Get the group with a given name in the specified directory.
	 *
	 * @param name          of the group
	 * @param userDirectory the group should belong to
	 * @return instance or {@code null} if not found
	 */
	Group getGroupByName( String name, UserDirectory userDirectory );

	Group save( Group groupDto );

	void delete( long groupId );

	GroupProperties getProperties( Group group );

	void saveProperties( GroupProperties groupProperties );

	void deleteProperties( Group group );

	void deleteProperties( long groupId );

	/**
	 * Returns all groups having a specific property value.  Will return all matching
	 * groups across all user directories.
	 *
	 * @param propertyName  name of the property
	 * @param propertyValue value the property should have
	 * @return groups
	 */
	Collection<Group> getGroupsWithPropertyValue( String propertyName, Object propertyValue );

	@Override
	Group findOne( Predicate predicate );

	@Override
	Collection<Group> findAll( Predicate predicate );

	@Override
	Collection<Group> findAll( Predicate predicate, OrderSpecifier<?>... orderSpecifiers );

	@Override
	Page<Group> findAll( Predicate predicate, Pageable pageable );

	@Override
	long count( Predicate predicate );
}
