package com.foreach.across.modules.user.services;

import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.dto.UserDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

public class UserValidator extends LocalValidatorFactoryBean
{
	@Autowired
	private UserService userService;

	@Override
	public void validate( Object target, Errors errors ) {
		super.validate( target, errors );
		if( supports( target.getClass() ) ) {
			UserDto userDto = (UserDto) target;
			if( userService.isRequireEmailUnique() ) {
				User userByEmail = userService.getUserByEmail( userDto.getEmail() );
				if( userByEmail != null && userByEmail.getId() != userDto.getId() ) {
					errors.reject( null, "email already exists" );
				}
			}
			if( userService.isUseEmailAsUsername() ) {
				if( StringUtils.isNotBlank( userDto.getUsername() ) && !StringUtils.equalsIgnoreCase( userDto.getUsername(), userDto.getEmail() ) ) {
					errors.rejectValue( "username", null, "username cannot be specified when useEmailAsUsername is set" );
				}
			} else {
				if( StringUtils.isBlank( userDto.getUsername() ) ) {
					errors.rejectValue( "username", null, "username cannot be empty" );
				}
			}
			if( StringUtils.isBlank( userDto.getEmail() ) ) {
				errors.rejectValue( "email", null, "email cannot be empty" );
			}
		}
	}

	@Override
	public boolean supports( Class<?> clazz ) {
		return UserDto.class.isAssignableFrom( clazz );
	}
}
