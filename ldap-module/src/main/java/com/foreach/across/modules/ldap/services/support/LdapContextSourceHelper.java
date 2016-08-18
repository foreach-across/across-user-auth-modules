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

package com.foreach.across.modules.ldap.services.support;

import com.foreach.across.modules.ldap.business.LdapConnector;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

/**
 * @author Marc Vanbrabant
 */
public class LdapContextSourceHelper
{
	public static LdapTemplate createLdapTemplate( LdapConnector ldapConnector ) {
		LdapContextSource ldapContextSource = createLdapContextSource( ldapConnector );
		LdapTemplate ldapTemplate = new LdapTemplate( ldapContextSource );
		// TODO: put this in a setting? Microsoft Active Directory cannot follow referrals when in the root context
		ldapTemplate.setIgnorePartialResultException( true );
		return ldapTemplate;
	}

	public static LdapContextSource createLdapContextSource( LdapConnector connector ) {
		LdapContextSource source = new LdapContextSource();
		source.setPooled( true );
		source.setUrl( "ldap://" + connector.getHostName() + ":" + connector.getPort() );
		source.setBase( connector.getBaseDn() );
		source.setUserDn( connector.getUsername() );
		source.setPassword( connector.getPassword() );
		source.afterPropertiesSet();
		return source;
	}
}
