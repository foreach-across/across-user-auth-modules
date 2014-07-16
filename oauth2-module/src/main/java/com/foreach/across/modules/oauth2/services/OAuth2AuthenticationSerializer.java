package com.foreach.across.modules.oauth2.services;

import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.util.SerializationUtils;

public abstract class OAuth2AuthenticationSerializer<T>
{
	public byte[] serializeObject( T object ) {
		AuthenticationSerializerObject<T> authenticationSerializerObject = new AuthenticationSerializerObject<>( getClass().getCanonicalName(), object );
		return SerializationUtils.serialize( authenticationSerializerObject );
	}

	public byte[] serialize( OAuth2Authentication authentication ) {
		Object principal = authentication.getPrincipal();
		if( canSerialize( authentication ) ) {
			return serializePrincipal( principal );
		} else {
			throw new SerializationException( principal );
		}
	}

	protected abstract byte[] serializePrincipal( Object object );

	public abstract OAuth2Authentication deserialize( AuthenticationSerializerObject<T> serializerObject );

	public abstract boolean canSerialize( OAuth2Authentication authentication );

	public boolean canDeserialize( AuthenticationSerializerObject serializerObject ) {
		return serializerObject.getClassName().equals( getClass().getCanonicalName() );
	}

	public static class SerializationException extends RuntimeException {
		public SerializationException( Object object ) {
			super( "Cannot (de)serialize object of type: " + object );
		}
	}
}
