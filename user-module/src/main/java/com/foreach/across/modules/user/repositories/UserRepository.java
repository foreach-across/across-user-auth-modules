package com.foreach.across.modules.user.repositories;

import com.foreach.across.modules.user.business.User;

import java.util.Collection;

public interface UserRepository
{
	Collection<User> getUsers();

	User getUserByUsername( String userName );

	User getUserById( long id );

	User getUserByEmail( String email );

	void update( User user );

	void create( User user );

	void delete( User user );
}
