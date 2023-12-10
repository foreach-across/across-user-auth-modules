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

import com.foreach.across.modules.oauth2.config.OAuth2SchemaConfiguration;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = OAuth2SchemaConfiguration.TABLE_CLIENT_SCOPE)
@AssociationOverrides(
		{
				@AssociationOverride(name = "id.oAuth2Client", joinColumns = @JoinColumn(name = "client_id")),
				@AssociationOverride(name = "id.oAuth2Scope", joinColumns = @JoinColumn(name = "scope_id"))
		}
)
public class OAuth2ClientScope implements Persistable<OAuth2ClientScopeId>, Comparable, Serializable
{
	private static final long serialVersionUID = -1673618360294752368L;

	@EmbeddedId
	private OAuth2ClientScopeId id = new OAuth2ClientScopeId();

	@Column(name = "auto_approve")
	private boolean autoApprove;

	@Override
	public OAuth2ClientScopeId getId() {
		return id;
	}

	public void setId( OAuth2ClientScopeId id ) {
		this.id = id;
	}

	@Transient
	public OAuth2Client getOAuth2Client() {
		return this.getId().getOAuth2Client();
	}

	public void setOAuth2Client( OAuth2Client oAuth2Client ) {
		this.getId().setOAuth2Client( oAuth2Client );
	}

	@Transient
	public OAuth2Scope getOAuth2Scope() {
		return getId().getOAuth2Scope();
	}

	public void setOAuth2Scope( OAuth2Scope oAuth2Scope ) {
		getId().setOAuth2Scope( oAuth2Scope );
	}

	public boolean isAutoApprove() {
		return autoApprove;
	}

	public void setAutoApprove( boolean autoApprove ) {
		this.autoApprove = autoApprove;
	}

	@Override
	public boolean isNew() {
		return id == null || id.getOAuth2Scope() == null || id.getOAuth2Client() == null;
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || !( o instanceof OAuth2ClientScope ) ) {
			return false;
		}

		OAuth2ClientScope that = (OAuth2ClientScope) o;

		return Objects.equals( getId(), that.getId() );
	}

	@Override
	public int hashCode() {
		return Objects.hash( getId() );
	}

	@Override
	public int compareTo( Object o ) {
		OAuth2ClientScope that = (OAuth2ClientScope) o;
		return ObjectUtils.compare( getOAuth2Scope(), that != null ? that.getOAuth2Scope() : null );
	}
}
