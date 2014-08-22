package com.foreach.across.modules.user.repositories;

import com.foreach.across.modules.hibernate.repositories.BasicRepository;
import com.foreach.across.modules.user.business.User;

public interface UserRepository extends BasicRepository<User>
{
	User getByUsername( String userName );

	User getByEmail( String email );
}
