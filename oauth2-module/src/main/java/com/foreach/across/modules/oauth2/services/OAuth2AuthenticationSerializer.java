/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.foreach.across.modules.oauth2.services;

import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.util.SerializationUtils;

public abstract class OAuth2AuthenticationSerializer<T>
{
	public byte[] serializeObject( T object, OAuth2Request oAuth2Request ) {
		AuthenticationSerializerObject<T> authenticationSerializerObject = new AuthenticationSerializerObject<>(
				getClass().getCanonicalName(), object,
				oAuth2Request );
		return SerializationUtils.serialize( authenticationSerializerObject );
	}

	public byte[] serialize( OAuth2Authentication authentication ) {
		Object principal = authentication.getPrincipal();
		if ( canSerialize( authentication ) ) {
			return serializePrincipal( principal, authentication.getOAuth2Request() );
		}
		else {
			throw new SerializationException( principal );
		}
	}

	protected abstract byte[] serializePrincipal( Object object, OAuth2Request oAuth2Request );

	public abstract OAuth2Authentication deserialize( AuthenticationSerializerObject<T> serializerObject );

	public abstract boolean canSerialize( OAuth2Authentication authentication );

	public boolean canDeserialize( AuthenticationSerializerObject serializerObject ) {
		return serializerObject.getClassName().equals( getClass().getCanonicalName() );
	}

	public static class SerializationException extends RuntimeException
	{
		public SerializationException( Object object ) {
			super( "Cannot (de)serialize object of type: " + object );
		}
	}
}
