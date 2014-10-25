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

import com.foreach.across.modules.hibernate.util.BasicServiceHelper;
import com.foreach.across.modules.user.business.Group;
import com.foreach.across.modules.user.business.GroupProperties;
import com.foreach.across.modules.user.dto.GroupDto;
import com.foreach.across.modules.user.repositories.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;

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

	@Override
	public Collection<Group> getGroups() {
		return groupRepository.getAll();
	}

	@Override
	public Group getGroupById( long id ) {
		return groupRepository.getById( id );
	}

	@Transactional
	@Override
	public Group save( GroupDto groupDto ) {
		return BasicServiceHelper.save( groupDto, Group.class, groupRepository );
	}

	@Override
	@Transactional
	public void delete( long groupId ) {
		Group group = groupRepository.getById( groupId );
		deleteProperties( groupId );
		groupRepository.delete( group );
	}

	@Override
	public GroupProperties getProperties( Group group ) {
		return groupPropertiesService.getProperties( group.getId() );
	}

	@Override
	public GroupProperties getProperties( GroupDto groupDto ) {
		return groupPropertiesService.getProperties( groupDto.getId() );
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

	@Transactional(readOnly = true)
	@Override
	public Collection<Group> getGroupsWithPropertyValue( String propertyName, Object propertyValue ) {
		Collection<Long> groupIds = groupPropertiesService.getEntityIdsForPropertyValue( propertyName, propertyValue );

		if ( groupIds.isEmpty() ) {
			return Collections.emptyList();
		}

		return groupRepository.getAllForIds( groupIds );
	}
}
