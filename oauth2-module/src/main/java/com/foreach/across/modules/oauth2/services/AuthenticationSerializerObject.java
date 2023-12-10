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
package com.foreach.across.modules.oauth2.services;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Request;

import java.io.Serializable;
import java.util.*;

public class AuthenticationSerializerObject<T> implements Serializable
{
	private static final long serialVersionUID = -3165303425428338511L;

	private final String className;
	private final T object;

	// Properties from OAuth2Request
	private final Map<String, String> requestParameters;
	private final String clientId;
	private final boolean approved;
	private final Set<String> scope;
	private final Set<String> resourceIds;
	private final String redirectUri;
	private final Set<String> responseTypes;
	private final Map<String, Serializable> extensionProperties;

	public AuthenticationSerializerObject( String className, T object, OAuth2Request oAuth2Request ) {
		this.className = className;
		this.object = object;

		this.requestParameters = oAuth2Request.getRequestParameters();
		this.clientId = oAuth2Request.getClientId();
		this.approved = oAuth2Request.isApproved();
		this.scope = duplicate( oAuth2Request.getScope() );
		this.resourceIds = duplicate( oAuth2Request.getResourceIds() );
		this.redirectUri = oAuth2Request.getRedirectUri();
		this.responseTypes = duplicate( oAuth2Request.getResponseTypes() );
		this.extensionProperties = duplicate( oAuth2Request.getExtensions() );
	}

	private Set<String> duplicate( Set<String> values ) {
		return values != null ? new HashSet<>( values ) : null;
	}

	private Map<String, Serializable> duplicate( Map<String, Serializable> values ) {
		return values != null ? new HashMap<>( values ) : null;
	}

	public String getClassName() {
		return className;
	}

	public T getObject() {
		return object;
	}

	public String getClientId() {
		return clientId;
	}

	public OAuth2Request getOAuth2Request( Collection<GrantedAuthority> authorities ) {
		return new OAuth2Request( requestParameters,
		                          clientId,
		                          authorities,
		                          approved,
		                          scope,
		                          resourceIds,
		                          redirectUri,
		                          responseTypes,
		                          extensionProperties
		);
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		AuthenticationSerializerObject<?> that = (AuthenticationSerializerObject<?>) o;
		return Objects.equals( approved, that.approved ) &&
				Objects.equals( className, that.className ) &&
				Objects.equals( object, that.object ) &&
				Objects.equals( requestParameters, that.requestParameters ) &&
				Objects.equals( clientId, that.clientId ) &&
				Objects.equals( scope, that.scope ) &&
				Objects.equals( resourceIds, that.resourceIds ) &&
				Objects.equals( redirectUri, that.redirectUri ) &&
				Objects.equals( responseTypes, that.responseTypes ) &&
				Objects.equals( extensionProperties, that.extensionProperties );
	}

	@Override
	public int hashCode() {
		return Objects.hash( className, object, requestParameters, clientId, approved, scope, resourceIds, redirectUri,
		                     responseTypes, extensionProperties );
	}
}
