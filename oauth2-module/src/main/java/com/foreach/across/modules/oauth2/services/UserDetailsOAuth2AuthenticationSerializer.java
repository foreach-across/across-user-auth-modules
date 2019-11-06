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

import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipalId;
import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalService;
import com.foreach.across.modules.user.business.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public class UserDetailsOAuth2AuthenticationSerializer extends OAuth2AuthenticationSerializer<String>
{
	@Autowired
	private ClientDetailsService clientDetailsService;

	@Autowired
	private SecurityPrincipalService securityPrincipalService;

	@Override
	protected byte[] serializePrincipal( Object object, OAuth2Request oAuth2Request ) {
		SecurityPrincipalId principalId = (SecurityPrincipalId) object;
		return super.serializeObject( principalId.toString(), oAuth2Request );
	}

	@Override
	public OAuth2Authentication deserialize( AuthenticationSerializerObject<String> serializerObject ) {
		SecurityPrincipal securityPrincipal = securityPrincipalService.getPrincipalByName( serializerObject.getObject() ).orElse( null );
		User user = null;

		if ( securityPrincipal instanceof User ) {
			user = (User) securityPrincipal;
		}

		if ( user == null || !isAllowedToLogon( user ) ) {
			throw new RemoveTokenException();
		}

		ClientDetails clientDetails = clientDetailsService.loadClientByClientId( serializerObject.getClientId() );
		OAuth2Request userRequest = serializerObject.getOAuth2Request( clientDetails.getAuthorities() );

		return new OAuth2Authentication( userRequest, new PreAuthenticatedAuthenticationToken( user.getSecurityPrincipalId(), null, user.getAuthorities() ) );
	}

	private boolean isAllowedToLogon( User user ) {
		return user.isEnabled() && user.isAccountNonExpired() && user.isAccountNonLocked() && user.isCredentialsNonExpired();
	}

	@Override
	public boolean canSerialize( OAuth2Authentication authentication ) {
		Object principal = authentication.getPrincipal();
		return principal instanceof SecurityPrincipalId;
	}

	@Override
	public boolean canDeserialize( AuthenticationSerializerObject serializerObject ) {
		return super.canDeserialize( serializerObject )
				|| serializerObject.getClassName().equals( "com.foreach.across.modules.oauth2.services.UserOAuth2AuthenticationSerializer" );
	}
}
