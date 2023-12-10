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
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.annotation.concurrent.NotThreadSafe;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

/**
 * Represents an LDAP user directory {@link UserDirectory}.
 *
 * @author Marc Vanbrabant
 * @since 1.0.0
 */
@NotThreadSafe
@Entity
@DiscriminatorValue("ldap")
@Data
@EqualsAndHashCode(callSuper = true)
public class LdapUserDirectory extends UserDirectory
{
	@OneToOne(optional = false)
	@NotNull
	@JoinColumn(name = "settings_id")
	private LdapConnector ldapConnector;
}
