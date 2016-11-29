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
package com.foreach.across.modules.oauth2;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("OAuth2Module")
public class OAuth2ModuleSettings
{
	/**
	 * How approvals should be handled and remembered.
	 */
	public enum ApprovalHandler
	{
		DEFAULT,
		TOKEN_STORE,
		APPROVAL_STORE
	}

	/**
	 * Value for the type of approval store that should be used in case of
	 * {@link com.foreach.across.modules.oauth2.OAuth2ModuleSettings.ApprovalHandler#APPROVAL_STORE}.
	 */
	public enum ApprovalStore
	{
		IN_MEMORY,
		JDBC,
		TOKEN
	}


	public static final String APPROVAL_FORM_ENDPOINT = "OAuth2Module.approval.formEndpoint";
	public static final String APPROVAL_HANDLER = "OAuth2Module.approval.handler";
	public static final String APPROVAL_STORE = "OAuth2Module.approval.store";
	public static final String USE_JDBC_AUTHORIZATION_CODE_SERVICE = "OAuth2Module.useJdbcAuthorizationCodeServices";
	public static final String USE_LOCKING_FOR_TOKEN_CREATION = "OAuth2Module.useLockingForTokenCreation";

	/**
	 * Specifies whether the authorization process should use a jdbcAuthorizationCodeService instead of the default
	 * inMemoryAuthorizationCodeService
	 * <p/>
	 * False/True
	 */
	private Boolean useJdbcAuthorizationCodeServices = true;

	/**
	 * Should distributed locking be used for token creation.
	 * Defaults to true which incurs a performance hit but ensures compatibility when scaling out to multiple servers.
	 */
	private Boolean useLockingForTokenCreation = true;

	/**
	 * Specifies whether the default spring endpoint for the approval form should be used (when left empty)
	 * or the custom endpoint that redirects to a custom form
	 * <p/>
	 * String
	 */
	private ApprovalSettings approval = new ApprovalSettings();

	public Boolean getUseJdbcAuthorizationCodeServices() {
		return useJdbcAuthorizationCodeServices;
	}

	public void setUseJdbcAuthorizationCodeServices( Boolean useJdbcAuthorizationCodeServices ) {
		this.useJdbcAuthorizationCodeServices = useJdbcAuthorizationCodeServices;
	}

	public Boolean getUseLockingForTokenCreation() {
		return useLockingForTokenCreation;
	}

	public void setUseLockingForTokenCreation( Boolean useLockingForTokenCreation ) {
		this.useLockingForTokenCreation = useLockingForTokenCreation;
	}

	public ApprovalSettings getApproval() {
		return approval;
	}

	public void setApproval( ApprovalSettings approval ) {
		this.approval = approval;
	}

	public static class ApprovalSettings
	{

		/**
		 * Specifies whether the default spring endpoint for the approval form should be used
		 * (when left empty) or the custom endpoint that redirects to a custom form.
		 */
		private String formEndpoint;

		/**
		 * Specify how user approvals should be handled and remembers (defaults to storing approvals in an approval store)
		 */
		private ApprovalHandler handler = ApprovalHandler.APPROVAL_STORE;

		/**
		 * Specify the type of approval store that should be used (defaults to jdbc - storing approvals in database)
		 */
		private ApprovalStore store = ApprovalStore.JDBC;

		public String getFormEndpoint() {
			return formEndpoint;
		}

		public void setFormEndpoint( String formEndpoint ) {
			this.formEndpoint = formEndpoint;
		}

		public ApprovalHandler getHandler() {
			return handler;
		}

		public void setHandler( ApprovalHandler handler ) {
			this.handler = handler;
		}

		public ApprovalStore getStore() {
			return store;
		}

		public void setStore( ApprovalStore store ) {
			this.store = store;
		}
	}
}
