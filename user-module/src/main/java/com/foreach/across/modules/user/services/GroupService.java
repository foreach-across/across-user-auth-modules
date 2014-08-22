package com.foreach.across.modules.user.services;

import com.foreach.across.modules.user.business.Group;
import com.foreach.across.modules.user.dto.GroupDto;

import java.util.Collection;

/**
 * @author Arne Vandamme
 */
public interface GroupService
{
	Collection<Group> getGroups();

	Group getGroupById( long id );

	Group save( GroupDto groupDto );
}
