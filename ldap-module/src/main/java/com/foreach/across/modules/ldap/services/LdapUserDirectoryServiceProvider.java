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
import com.foreach.across.modules.ldap.business.LdapConnectorSettings;
import com.foreach.across.modules.ldap.business.LdapUserDirectory;
import com.foreach.across.modules.ldap.services.properties.LdapConnectorSettingsService;
import com.foreach.across.modules.user.business.BasicSecurityPrincipal;
import com.foreach.across.modules.user.business.UserDirectory;
import com.foreach.across.modules.user.services.UserDirectoryServiceProvider;
import com.foreach.across.modules.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.validation.Validator;

/**
 * @author Marc Vanbrabant
 * @since 1.0.0
 */
public class LdapUserDirectoryServiceProvider implements UserDirectoryServiceProvider
{
	@Autowired
	private UserService userService;

	@Autowired
	private LdapConnectorSettingsService ldapConnectorSettingsService;

	@Override
	public boolean supports( Class<? extends UserDirectory> userDirectoryClass ) {
		return LdapUserDirectory.class.isAssignableFrom( userDirectoryClass );
	}

	@Override
	public AuthenticationProvider getAuthenticationProvider( UserDirectory userDirectory ) {
		try {
			LdapUserDirectory ldapUserDirectory = (LdapUserDirectory) userDirectory;
			LdapConnector ldapConnector = ldapUserDirectory.getLdapConnector();
			LdapAuthenticationProvider ldapAuthenticationProvider = new LdapAuthenticationProvider();
			ldapAuthenticationProvider.setUserService( userService );
			ldapAuthenticationProvider.setUserDirectory( userDirectory );
			ldapAuthenticationProvider.setLdapContextSource( ldapConnector );
			ldapAuthenticationProvider.afterPropertiesSet();

			//TODO, move this to a better place?
			LdapConnectorSettings ldapConnectorSettings = ldapConnectorSettingsService.getProperties(
					ldapConnector.getId() );
			ldapAuthenticationProvider.setSearchFilter( ldapConnectorSettings.getUserObjectFilterForUser() );

			return ldapAuthenticationProvider;
		}
		catch ( Exception e ) {
			throw new RuntimeException( e );
		}
	}

	@Override
	public Validator getValidator( UserDirectory userDirectory,
	                               Class<? extends BasicSecurityPrincipal> securityPrincipalClass ) {
		return null;
	}
}
