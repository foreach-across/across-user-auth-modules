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

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class OAuth2ClientScopeId implements Serializable
{
	@ManyToOne
	private OAuth2Client oAuth2Client;

	@ManyToOne
	private OAuth2Scope oAuth2Scope;

	public OAuth2Client getOAuth2Client() {
		return oAuth2Client;
	}

	public void setOAuth2Client( OAuth2Client oAuth2Client ) {
		this.oAuth2Client = oAuth2Client;
	}

	public OAuth2Scope getOAuth2Scope() {
		return oAuth2Scope;
	}

	public void setOAuth2Scope( OAuth2Scope oAuth2Scope ) {
		this.oAuth2Scope = oAuth2Scope;
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( !( o instanceof OAuth2ClientScopeId ) ) {
			return false;
		}
		OAuth2ClientScopeId that = (OAuth2ClientScopeId) o;
		return Objects.equals( getOAuth2Client(), that.getOAuth2Client() ) &&
				Objects.equals( getOAuth2Scope(), that.getOAuth2Scope() );
	}

	@Override
	public int hashCode() {
		return Objects.hash( getOAuth2Client(), getOAuth2Scope() );
	}
}
