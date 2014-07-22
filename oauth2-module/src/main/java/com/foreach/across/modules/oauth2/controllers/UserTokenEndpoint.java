package com.foreach.across.modules.oauth2.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

import java.util.*;

/**
 * Additional OAuth endpoint for creating a new user token without approvals, but based
 * on the permissions of the principal represented by the current token.
 *
 * @author Arne Vandamme
 */
@FrameworkEndpoint
public class UserTokenEndpoint
{
	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private AuthorizationServerTokenServices authorizationServerTokenServices;

	@RequestMapping("/oauth/user_token")
	public ResponseEntity<Map<String, String>> createUserToken(
			OAuth2Authentication authentication,
			@RequestParam(value = "username") String username,
			@RequestParam(value = "scope") String scope
	) {
		HttpStatus status = HttpStatus.OK;
		Map<String, String> response = new HashMap<>();

		String forbidden = null;

		Set<String> allowedScopes = authentication.getOAuth2Request().getScope();
		Set<String> requestedScopes = new HashSet<>( Arrays.asList( StringUtils.split( scope, " " ) ) );

		for ( String requestedScope : requestedScopes ) {
			if ( !allowedScopes.contains( requestedScope ) ) {
				forbidden = "Scope \"" + requestedScope + "\" is not allowed with the current access token.";
			}
		}

		if ( forbidden == null ) {
			UserDetails userDetails = userDetailsService.loadUserByUsername( username );

			if ( userDetails == null ) {
				forbidden = "Requested user does not exist.";
			}
			else if ( !canAuthenticate( userDetails ) ) {
				forbidden = "Requested user is not allowed to authenticate.";
			}

			if ( forbidden == null ) {
				// Build new request and user authentication
				OAuth2Request clientAuthentication = authentication.getOAuth2Request();

				OAuth2Request request = new OAuth2Request(
						clientAuthentication.getRequestParameters(),
						clientAuthentication.getClientId(),
						clientAuthentication.getAuthorities(),
						clientAuthentication.isApproved(),
						requestedScopes,
						clientAuthentication.getResourceIds(),
						clientAuthentication.getRedirectUri(),
						clientAuthentication.getResponseTypes(),
						clientAuthentication.getExtensions()
				);

				Authentication userAuthentication = new PreAuthenticatedAuthenticationToken( userDetails, null,
				                                                                             userDetails
						                                                                             .getAuthorities() );
				OAuth2Authentication newAuthentication = new OAuth2Authentication( request, userAuthentication );

				OAuth2AccessToken token = authorizationServerTokenServices.createAccessToken( newAuthentication );
				response.put( "access_token", token.getValue() );
				response.put( "token_type", token.getTokenType() );
				response.put( "expires_in", Integer.toString( token.getExpiresIn() ) );
				response.put( "scope", StringUtils.join( token.getScope(), " " ) );

				if ( token.getRefreshToken() != null ) {
					response.put( "refresh_token", token.getRefreshToken().getValue() );
				}

			}
		}

		if ( forbidden != null ) {
			status = HttpStatus.FORBIDDEN;
			response.put( "error", forbidden );
		}

		return new ResponseEntity<>( response, status );
	}

	private boolean canAuthenticate( UserDetails userDetails ) {
		return userDetails.isEnabled() && userDetails.isAccountNonExpired() && userDetails
				.isAccountNonLocked() && userDetails.isCredentialsNonExpired();
	}
}
