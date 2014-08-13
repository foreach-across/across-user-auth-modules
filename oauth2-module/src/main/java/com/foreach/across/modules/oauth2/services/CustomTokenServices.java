package com.foreach.across.modules.oauth2.services;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

public class CustomTokenServices extends DefaultTokenServices
{
	private TokenStore tokenStore;

	@Override
	public void setTokenStore( TokenStore tokenStore ) {
		super.setTokenStore( tokenStore );
		this.tokenStore = tokenStore;
	}

	@Override
	public OAuth2Authentication loadAuthentication( String accessTokenValue ) throws AuthenticationException {
		try {
			return super.loadAuthentication( accessTokenValue );
		}
		catch ( RemoveTokenException removeTokenException ) {
			// When the username is changed or the clientId is changed, we remove the access token so we get an invalid token exception later on
			OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken( accessTokenValue );
			if ( oAuth2AccessToken != null ) {
				tokenStore.removeAccessToken( oAuth2AccessToken );
			}
		}
		return null;
	}
}
