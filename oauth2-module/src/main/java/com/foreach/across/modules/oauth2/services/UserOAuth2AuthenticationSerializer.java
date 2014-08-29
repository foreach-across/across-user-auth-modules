package com.foreach.across.modules.oauth2.services;

import com.foreach.across.modules.user.business.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public class UserOAuth2AuthenticationSerializer extends OAuth2AuthenticationSerializer<String>
{
	@Autowired
	private UserDetailsService userDetailsService;
	@Autowired
	private ClientDetailsService clientDetailsService;

	@Override
	protected byte[] serializePrincipal( Object object, OAuth2Request oAuth2Request ) {
		User user = (User) object;
		return super.serializeObject( user.getUsername(), oAuth2Request );
	}

	@Override
	public OAuth2Authentication deserialize( AuthenticationSerializerObject<String> serializerObject ) {
		UserDetails user;
		try {
			user = userDetailsService.loadUserByUsername( serializerObject.getObject() );
		}
		catch ( UsernameNotFoundException usernameNotFoundException ) {
			throw new RemoveTokenException();
		}

		if ( !isAllowedToLogon( user ) ) {
			throw new RemoveTokenException();
		}

		ClientDetails clientDetails = clientDetailsService.loadClientByClientId( serializerObject.getClientId() );
		OAuth2Request userRequest = serializerObject.getOAuth2Request( clientDetails.getAuthorities() );

		return new OAuth2Authentication( userRequest, new PreAuthenticatedAuthenticationToken( user, null,
		                                                                                       user.getAuthorities() ) );
	}

	private boolean isAllowedToLogon( UserDetails user ) {
		return user.isEnabled() && user.isAccountNonExpired() && user.isAccountNonLocked() && user
				.isCredentialsNonExpired();
	}

	@Override
	public boolean canSerialize( OAuth2Authentication authentication ) {
		Object principal = authentication.getPrincipal();
		return principal != null && principal instanceof User;
	}
}
