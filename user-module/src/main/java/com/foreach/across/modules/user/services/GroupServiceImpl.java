package com.foreach.across.modules.user.services;

import com.foreach.across.modules.hibernate.util.BasicServiceHelper;
import com.foreach.across.modules.user.business.Group;
import com.foreach.across.modules.user.dto.GroupDto;
import com.foreach.across.modules.user.repositories.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * @author Arne Vandamme
 */
@Service
public class GroupServiceImpl implements GroupService
{
	@Autowired
	private GroupRepository groupRepository;

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
}
