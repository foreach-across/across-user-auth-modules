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

package com.foreach.across.modules.ldap.business;

import com.foreach.across.modules.properties.business.EntityProperties;
import com.foreach.common.spring.properties.PropertiesSource;
import com.foreach.common.spring.properties.PropertyTypeRegistry;

/**
 * @author Marc Vanbrabant
 * @since 1.0.0
 */
public class LdapConnectorSettings extends EntityProperties<Long>
{
	private final long connectorId;

	public LdapConnectorSettings( long connectorId,
	                              PropertyTypeRegistry<String> propertyTypeRegistry,
	                              PropertiesSource source ) {
		super( propertyTypeRegistry, source );

		this.connectorId = connectorId;
	}

	@Override
	public Long getId() {
		return connectorId;
	}

	public String getUserObjectClass() {
		return getValue( "ldapUserObjectClass" );
	}

	public String getUserObjectFilter() {
		return getValue( "ldapUserObjectFilter" );
	}

	public String getUsername() {
		return getValue( "ldapUsername" );
	}

	public String getUserEmail() {
		return getValue( "ldapUserEmail" );
	}

	public String getFirstName() {
		return getValue( "ldapUserFirstName" );
	}

	public String getLastName() {
		return getValue( "ldapUserLastName" );
	}

	public String getDiplayName() {
		return getValue( "ldapUserDisplayName" );
	}

	public String getGroupObjectClass() {
		return getValue( "ldapGroupObjectClass" );
	}

	public String getGroupObjectFilter() {
		return getValue( "ldapGroupObjectFilter" );
	}

	public String getGroupName() {
		return getValue( "ldapGroupName" );
	}

}
