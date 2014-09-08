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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.*;

public class ClientOAuth2AuthenticationSerializer extends OAuth2AuthenticationSerializer<String>
{
	@Autowired
	private ClientDetailsService clientDetailsService;

	@Override
	protected byte[] serializePrincipal( Object object, OAuth2Request oAuth2Request ) {
		String clientId = (String) object;
		return super.serializeObject( clientId, oAuth2Request );
	}

	@Override
	public OAuth2Authentication deserialize( AuthenticationSerializerObject<String> serializerObject ) {
		String clientId = serializerObject.getObject();
		ClientDetails clientDetails;
		try {
			clientDetails = clientDetailsService.loadClientByClientId( clientId );
		}
		catch ( NoSuchClientException noSuchClientException ) {
			throw new RemoveTokenException();
		}

		OAuth2Request clientRequest = serializerObject.getOAuth2Request( clientDetails.getAuthorities() );
		return new OAuth2Authentication( clientRequest, null );
	}

	@Override
	public boolean canSerialize( OAuth2Authentication authentication ) {
		return authentication.isClientOnly() && authentication.getPrincipal() instanceof String;
	}
}
