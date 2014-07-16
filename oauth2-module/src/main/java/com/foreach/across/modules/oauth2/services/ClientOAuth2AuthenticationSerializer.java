package com.foreach.across.modules.oauth2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

import java.io.Serializable;
import java.util.Collections;

public class ClientOAuth2AuthenticationSerializer extends OAuth2AuthenticationSerializer<String>
{
	@Autowired
	private ClientDetailsService clientDetailsService;

	@Override
	protected byte[] serializePrincipal( Object object ) {
		String clientId = (String) object;
		return super.serializeObject( clientId );
	}

	@Override
	public OAuth2Authentication deserialize( AuthenticationSerializerObject<String> serializerObject ) {
		String clientId = serializerObject.getObject();
		ClientDetails clientDetails = clientDetailsService.loadClientByClientId( clientId );

		OAuth2Request clientRequest = new OAuth2Request( Collections.<String, String>emptyMap(),
		                                                 clientId, clientDetails.getAuthorities(), true,
		                                                 clientDetails.getScope(),
		                                                 clientDetails.getResourceIds(), "",
		                                                 Collections.<String>emptySet(), Collections.<String, Serializable>emptyMap()
		);

		return new OAuth2Authentication( clientRequest, null );
	}

	@Override
	public boolean canSerialize( OAuth2Authentication authentication ) {
		return authentication.isClientOnly() && authentication.getPrincipal() instanceof String;
	}
}
