package com.foreach.across.modules.user.services;

public class UserModuleException extends RuntimeException
{
	public UserModuleException() {
	}

	public UserModuleException( String message ) {
		super( message );
	}

	public UserModuleException( String message, Throwable cause ) {
		super( message, cause );
	}

	public UserModuleException( Throwable cause ) {
		super( cause );
	}

	public UserModuleException( String message,
	                            Throwable cause,
	                            boolean enableSuppression,
	                            boolean writableStackTrace ) {
		super( message, cause, enableSuppression, writableStackTrace );
	}
}
