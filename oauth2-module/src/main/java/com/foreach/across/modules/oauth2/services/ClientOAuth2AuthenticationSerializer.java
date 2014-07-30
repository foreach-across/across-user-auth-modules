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
		} catch ( NoSuchClientException noSuchClientException ) {
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
