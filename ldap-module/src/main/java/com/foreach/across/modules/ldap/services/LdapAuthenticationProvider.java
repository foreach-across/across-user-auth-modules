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
import com.foreach.across.modules.ldap.business.LdapUserDirectory;
import com.foreach.across.modules.ldap.services.support.LdapContextSourceHelper;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.security.AbstractUserInDirectoryAuthenticationProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.util.Assert;

/**
 * @author Marc Vanbrabant
 */
public class LdapAuthenticationProvider extends AbstractUserInDirectoryAuthenticationProvider
{
	private final Log LOG = LogFactory.getLog( LdapAuthenticationProvider.class );
	private String searchFilter;
	private LdapContextSource ldapContextSource;

	public LdapAuthenticationProvider() {
		setThrowExceptionIfUserNotFound( false );
	}

	@Override
	protected void additionalAuthenticationChecks( UserDetails userDetails,
	                                               UsernamePasswordAuthenticationToken authentication ) throws AuthenticationException {
		BindAuthenticator ldapAuthenticator = new BindAuthenticator( ldapContextSource );
		FilterBasedLdapUserSearch search = new FilterBasedLdapUserSearch( "", searchFilter,
		                                                                  ldapContextSource );
		ldapAuthenticator.setUserSearch( search );

		DirContextOperations dirContextOperations = ldapAuthenticator.authenticate( authentication );
		if ( dirContextOperations == null ) {
			throw new BadCredentialsException( "Cannot authenticate user with LDAP" );
		}
	}

	@Override
	protected void doAfterPropertiesSet() throws Exception {
		Assert.isTrue( userDirectory instanceof LdapUserDirectory,
		               "Only LdapUserDirectory types are supported" );
	}

	@Override
	protected UserDetails buildUserDetails( User user,
	                                        UsernamePasswordAuthenticationToken authentication ) throws AuthenticationException {
		return user;
	}

	public void setSearchFilter( String searchFilter ) {
		this.searchFilter = searchFilter;
	}

	public void setLdapContextSource( LdapConnector ldapConnector ) {
		this.ldapContextSource = LdapContextSourceHelper.createLdapContextSource( ldapConnector );
	}
}
