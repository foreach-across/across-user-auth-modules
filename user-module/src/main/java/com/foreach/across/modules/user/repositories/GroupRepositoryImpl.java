package com.foreach.across.modules.user.repositories;

import com.foreach.across.modules.hibernate.repositories.BasicRepositoryImpl;
import com.foreach.across.modules.user.business.Group;
import org.springframework.stereotype.Repository;

/**
 * @author Arne Vandamme
 */
@Repository
public class GroupRepositoryImpl extends BasicRepositoryImpl<Group> implements GroupRepository
{
}
