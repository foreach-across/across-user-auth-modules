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
package com.foreach.across.modules.oauth2.business;

import com.foreach.across.core.database.AcrossSchemaConfiguration;
import com.foreach.across.modules.oauth2.config.OAuth2SchemaConfiguration;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = OAuth2SchemaConfiguration.TABLE_SCOPE)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class OAuth2Scope implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "seq_oauth_scope_id")
	@TableGenerator(name = "seq_oauth_scope_id", table = AcrossSchemaConfiguration.TABLE_SEQUENCES,
			pkColumnName = AcrossSchemaConfiguration.SEQUENCE_NAME,
			valueColumnName = AcrossSchemaConfiguration.SEQUENCE_VALUE, pkColumnValue = "seq_oauth_scope_id",
			allocationSize = 10)
	private long id;

	@Column(name = "name")
	private String name;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "pk.oAuth2Scope")
	private Set<OAuth2ClientScope> oAuth2ClientScopes = new HashSet<OAuth2ClientScope>();

	public long getId() {
		return id;
	}

	public void setId( long id ) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public Set<OAuth2ClientScope> getOAuth2ClientScopes() {
		return this.oAuth2ClientScopes;
	}

	public void setOAuth2ClientScopes( Set<OAuth2ClientScope> oAuth2ClientScopes ) {
		this.oAuth2ClientScopes = oAuth2ClientScopes;
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( !( o instanceof OAuth2Scope ) ) {
			return false;
		}
		OAuth2Scope that = (OAuth2Scope) o;

		return Objects.equals( getId(), that.getId() );
	}

	@Override
	public int hashCode() {
		return Objects.hash( getId() );
	}
}
