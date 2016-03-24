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

import com.foreach.across.modules.user.business.UserDirectory;

import javax.annotation.concurrent.NotThreadSafe;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Represents an LDAP user directory {@link UserDirectory}.
 *
 * @author Marc Vanbrabant
 * @since 1.0.0
 */
@NotThreadSafe
@Entity
@DiscriminatorValue("ldap")
public class LdapUserDirectory extends UserDirectory
{
	@ManyToOne(optional = false)
	@JoinColumn(name = "settings_id")
	private LdapConnector ldapConnector;

	public LdapConnector getLdapConnector() {
		return ldapConnector;
	}

	public void setLdapConnector( LdapConnector ldapConnector ) {
		this.ldapConnector = ldapConnector;
	}
}
