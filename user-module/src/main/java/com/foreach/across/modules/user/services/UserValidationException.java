package com.foreach.across.modules.user.services;

import org.springframework.validation.ObjectError;

import java.util.List;

public class UserValidationException extends RuntimeException
{
	private final List<ObjectError> errors;

	public UserValidationException( String message, List<ObjectError> errors ) {
		super( message );
		this.errors = errors;
	}

	public List<ObjectError> getErrors() {
		return errors;
	}
}
