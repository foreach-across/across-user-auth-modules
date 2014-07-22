package com.foreach.across.modules.oauth2.services;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Request;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class AuthenticationSerializerObject<T> implements Serializable
{
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
		this.scope = oAuth2Request.getScope();
		this.resourceIds = oAuth2Request.getResourceIds();
		this.redirectUri = oAuth2Request.getRedirectUri();
		this.responseTypes = oAuth2Request.getResponseTypes();
		this.extensionProperties = oAuth2Request.getExtensions();
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
}
