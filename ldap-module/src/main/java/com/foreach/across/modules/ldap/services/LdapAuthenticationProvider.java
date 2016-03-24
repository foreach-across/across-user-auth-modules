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

package com.foreach.across.modules.ldap.services;

import com.foreach.across.modules.ldap.business.LdapConnector;
import com.foreach.across.modules.ldap.services.support.LdapContextSourceHelper;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.services.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.util.StringUtils;

/**
 * @author Marc Vanbrabant
 */
public class LdapAuthenticationProvider implements AuthenticationProvider, MessageSourceAware
{
	private final Log LOG = LogFactory.getLog( LdapAuthenticationProvider.class );
	private String searchFilter;
	private UserService userService;
	private LdapContextSource ldapContextSource;
	private MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

	public void setUserService( UserService userService ) {
		this.userService = userService;
	}

	@Override
	public Authentication authenticate( Authentication authentication ) throws AuthenticationException {
		final UsernamePasswordAuthenticationToken userToken = (UsernamePasswordAuthenticationToken) authentication;

		String username = userToken.getName();
		String password = (String) authentication.getCredentials();

		if ( LOG.isDebugEnabled() ) {
			LOG.debug( "Processing authentication request for user: " + username );
		}

		if ( !StringUtils.hasLength( username ) ) {
			throw new BadCredentialsException( messages.getMessage( "LdapAuthenticationProvider.emptyUsername",
			                                                        "Empty Username" ) );
		}
		if ( !StringUtils.hasLength( password ) ) {
			throw new BadCredentialsException( messages.getMessage( "AbstractLdapAuthenticationProvider.emptyPassword",
			                                                        "Empty Password" ) );
		}

		User user = userService.getUserByUsername( username );

		if ( user == null ) {
			throw new BadCredentialsException( "User not found" );
		}

		BindAuthenticator ldapAuthenticator = new BindAuthenticator( ldapContextSource );

		FilterBasedLdapUserSearch search = new FilterBasedLdapUserSearch( "", searchFilter,
		                                                                  ldapContextSource );
		ldapAuthenticator.setUserSearch( search );
		org.springframework.security.ldap.authentication.LdapAuthenticationProvider ldapAuthenticationProvider =
				new org.springframework.security.ldap.authentication.LdapAuthenticationProvider( ldapAuthenticator );

		return ldapAuthenticationProvider.authenticate( userToken );
	}

	@Override
	public boolean supports( Class<?> authentication ) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom( authentication );
	}

	public void setMessageSource( MessageSource messageSource ) {
		this.messages = new MessageSourceAccessor( messageSource );
	}

	public void setSearchFilter( String searchFilter ) {
		this.searchFilter = searchFilter;
	}

	public void setLdapContextSource( LdapConnector ldapConnector ) {
		this.ldapContextSource = LdapContextSourceHelper.createLdapContextSource( ldapConnector );
	}
}
