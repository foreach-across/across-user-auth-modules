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
	 * Specifies whether the default spring endpoint for the approval form should be used (when left empty)
	 * or the custom endpoint that redirects to a custom form
	 * <p/>
	 * String
	 */
	public static final String CUSTOM_APPROVAL_FORM = "OAuth2Module.customApprovalForm";

	@Override
	protected void registerSettings( AcrossModuleSettingsRegistry registry ) {
		registry.register( CUSTOM_APPROVAL_FORM, String.class, "",
		                   "Specifies whether the default spring endpoint for the approval form should be used" +
				                   " (when left empty) or the custom endpoint that redirects to a custom form" );
	}

	public String getCustomApprovalForm() {
		return getProperty( CUSTOM_APPROVAL_FORM, String.class );
	}
}
