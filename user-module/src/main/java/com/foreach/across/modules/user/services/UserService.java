package com.foreach.across.modules.user.services;

import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.business.UserProperties;
import com.foreach.across.modules.user.dto.UserDto;

import java.util.Collection;

public interface UserService extends UserPropertiesService
{
	boolean isUseEmailAsUsername();

	boolean isRequireEmailUnique();

	Collection<User> getUsers();

	User getUserById( long id );

	User getUserByEmail( String email );

	User getUserByUsername( String username );

	UserDto createUserDto( User user );

	void save( UserDto user );

	void delete( long id );

	UserProperties getProperties( User user );

	UserProperties getProperties( UserDto userDto );
}
