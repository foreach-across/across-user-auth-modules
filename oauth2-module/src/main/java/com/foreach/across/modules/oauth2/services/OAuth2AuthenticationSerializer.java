package com.foreach.across.modules.oauth2.services;

import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.util.SerializationUtils;

public abstract class OAuth2AuthenticationSerializer<T>
{
	public byte[] serializeObject( T object, OAuth2Request oAuth2Request ) {
		AuthenticationSerializerObject<T> authenticationSerializerObject = new AuthenticationSerializerObject<>( getClass().getCanonicalName(), object,
		                                                                                                         oAuth2Request );
		return SerializationUtils.serialize( authenticationSerializerObject );
	}

	public byte[] serialize( OAuth2Authentication authentication ) {
		Object principal = authentication.getPrincipal();
		if( canSerialize( authentication ) ) {
			return serializePrincipal( principal, authentication.getOAuth2Request() );
		} else {
			throw new SerializationException( principal );
		}
	}

	protected abstract byte[] serializePrincipal( Object object, OAuth2Request oAuth2Request );

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
