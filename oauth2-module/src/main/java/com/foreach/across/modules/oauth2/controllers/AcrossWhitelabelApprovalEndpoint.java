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
package com.foreach.across.modules.oauth2.controllers;

import com.foreach.across.modules.oauth2.OAuth2ModuleSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.endpoint.WhitelabelApprovalEndpoint;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@SessionAttributes("authorizationRequest")
public class AcrossWhitelabelApprovalEndpoint
{
	@Autowired
	private WhitelabelApprovalEndpoint whitelabelApprovalEndpoint;

	@Autowired
	private OAuth2ModuleSettings oAuth2ModuleSettings;

	@RequestMapping("/oauth/confirm_access_external")
	public void getAccessConfirmation( Map<String, Object> model,
	                                   HttpServletRequest request, HttpServletResponse response ) throws Exception {
		whitelabelApprovalEndpoint.getAccessConfirmation( model, request );
		String confirmFormRedirectUrl = oAuth2ModuleSettings.getApproval().getFormEndpoint();
		response.sendRedirect( confirmFormRedirectUrl + ( confirmFormRedirectUrl.contains( "?" ) ? "&" : "?" ) + request
				.getQueryString() );
	}
}
