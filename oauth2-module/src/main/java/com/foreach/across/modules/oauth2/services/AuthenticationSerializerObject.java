package com.foreach.across.modules.oauth2.services;

import java.io.Serializable;

public class AuthenticationSerializerObject<T> implements Serializable
{
	String className;
	T object;

	public AuthenticationSerializerObject( String className, T object ) {
		this.className = className;
		this.object = object;
	}

	public String getClassName() {
		return className;
	}

	public T getObject() {
		return object;
	}
}
