package com.foreach.across.modules.user.services;

import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.dto.UserDto;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.constraintvalidators.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class UserValidator implements Validator
{
	@Autowired
	private UserService userService;
	@Autowired
	private EmailValidator emailValidator;

	@Override
	public void validate( Object target, Errors errors ) {
		if ( supports( target.getClass() ) ) {
			UserDto userDto = (UserDto) target;
			if ( userService.isRequireEmailUnique() ) {
				User userByEmail = userService.getUserByEmail( userDto.getEmail() );
				if ( userByEmail != null && userByEmail.getId() != userDto.getId() ) {
					errors.reject( null, "email already exists" );
				}
			}
			if ( userService.isUseEmailAsUsername() ) {
				if ( StringUtils.isNotBlank( userDto.getUsername() ) && !StringUtils.equalsIgnoreCase(
						userDto.getUsername(), userDto.getEmail() ) ) {
					errors.rejectValue( "username", null,
					                    "username cannot be specified when useEmailAsUsername is set or must be equal to the email" );
				}
			}
			else {
				if ( StringUtils.isBlank( userDto.getUsername() ) ) {
					errors.rejectValue( "username", null, "username cannot be empty" );
				}
			}

			if ( StringUtils.contains( userDto.getUsername(), ' ' ) ) {
				errors.rejectValue( "username", null, "username cannot contain whitespaces" );
			}
			if ( StringUtils.isBlank( userDto.getEmail() ) ) {
				errors.rejectValue( "email", null, "email cannot be empty" );
			}
			else {
				if ( !emailValidator.isValid( userDto.getEmail(), null ) ) {
					errors.rejectValue( "email", null, "invalid email" );
				}
			}
		}
	}

	@Override
	public boolean supports( Class<?> clazz ) {
		return UserDto.class.isAssignableFrom( clazz );
	}
}
