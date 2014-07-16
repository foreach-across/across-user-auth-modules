package com.foreach.across.modules.oauth2.services;

import com.foreach.across.modules.user.business.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.io.Serializable;
import java.util.Collections;

public class UserOAuth2AuthenticationSerializer extends OAuth2AuthenticationSerializer<String>
{
	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	protected byte[] serializePrincipal( Object object ) {
		User user = (User) object;
		return super.serializeObject( user.getUsername() );
	}

	@Override
	public OAuth2Authentication deserialize( AuthenticationSerializerObject<String> serializerObject ) {
		UserDetails user = userDetailsService.loadUserByUsername( serializerObject.getObject() );

		OAuth2Request userRequest = new OAuth2Request( Collections.<String, String>emptyMap(),
		                                               "foobar", Collections.<GrantedAuthority>emptyList(), true,
		                                               Collections.singleton( "full" ),
		                                               Collections.singleton( "knooppunt" ), "",
		                                               Collections.<String>emptySet(), Collections.<String, Serializable>emptyMap()
		);

		return new OAuth2Authentication( userRequest, new PreAuthenticatedAuthenticationToken( user, null, user.getAuthorities() ) );
	}

	@Override
	public boolean canSerialize( OAuth2Authentication authentication ) {
		Object principal = authentication.getPrincipal();
		return principal != null && principal instanceof User;
	}
}
