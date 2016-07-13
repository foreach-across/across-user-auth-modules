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

import com.foreach.across.core.AcrossModuleSettings;
import com.foreach.across.core.AcrossModuleSettingsRegistry;

public class OAuth2ModuleSettings extends AcrossModuleSettings
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

	/**
	 * Specifies whether the default spring endpoint for the approval form should be used (when left empty)
	 * or the custom endpoint that redirects to a custom form
	 * <p/>
	 * String
	 */
	public static final String APPROVAL_FORM_ENDPOINT = "OAuth2Module.approval.formEndpoint";
	public static final String APPROVAL_HANDLER = "OAuth2Module.approval.handler";
	public static final String APPROVAL_STORE = "OAuth2Module.approval.store";

	/**
	 * Specifies whether the authorization process should use a jdbcAuthorizationCodeService instead of the default
	 * inMemoryAuthorizationCodeService
	 * <p/>
	 * False/True
	 */
	public static final String USE_JDBC_AUTHORIZATION_CODE_SERVICE = "OAuth2Module.useJdbcAuthorizationCodeServices";

	/**
	 * Should distributed locking be used for token creation.
	 * Defaults to true which incurs a performance hit but ensures compatibility when scaling out to multiple servers.
	 */
	public static final String USE_LOCKING_FOR_TOKEN_CREATION = "OAuth2Module.useLockingForTokenCreation";

	@Override
	protected void registerSettings( AcrossModuleSettingsRegistry registry ) {
		registry.register( APPROVAL_HANDLER, ApprovalHandler.class, ApprovalHandler.APPROVAL_STORE,
		                   "Specify how user approvals should be handled and remembers (defaults to storing approvals in an approval store)" );
		registry.register( APPROVAL_STORE, ApprovalStore.class, ApprovalStore.JDBC,
		                   "Specify the type of approval store that should be used (defaults to jdbc - storing approvals in database)" );
		registry.register( APPROVAL_FORM_ENDPOINT, String.class, "",
		                   "Specifies whether the default spring endpoint for the approval form should be used" +
				                   " (when left empty) or the custom endpoint that redirects to a custom form" );
		registry.register( USE_JDBC_AUTHORIZATION_CODE_SERVICE, Boolean.class, false, "Specifies whether the " +
				"authorization process should use a jdbcAuthorizationCodeService instead of the default " +
				" inMemoryAuthorizationCodeService" );
		registry.register( USE_LOCKING_FOR_TOKEN_CREATION, Boolean.class, true,
		                   "Should distributed locking be used for token creation." );
	}

	public String getCustomApprovalForm() {
		return getProperty( APPROVAL_FORM_ENDPOINT, String.class );
	}

	public boolean isUseJdbcAuthorizationCodeService() {
		return getProperty( USE_JDBC_AUTHORIZATION_CODE_SERVICE, Boolean.class );
	}

	public ApprovalHandler getApprovalHandler() {
		return getProperty( APPROVAL_HANDLER, ApprovalHandler.class );
	}

	public ApprovalStore getApprovalStore() {
		return getProperty( APPROVAL_STORE, ApprovalStore.class );
	}

	public boolean isUseLockingForTokenCreation() {
		return getProperty( USE_LOCKING_FOR_TOKEN_CREATION, Boolean.class );
	}
}
