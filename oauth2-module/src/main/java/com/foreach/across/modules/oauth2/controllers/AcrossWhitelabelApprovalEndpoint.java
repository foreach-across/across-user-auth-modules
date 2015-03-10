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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.endpoint.WhitelabelApprovalEndpoint;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@SessionAttributes("authorizationRequest")
public class AcrossWhitelabelApprovalEndpoint// extends WhitelabelApprovalEndpoint
{
	@Autowired
	private WhitelabelApprovalEndpoint whitelabelApprovalEndpoint;

	//@Override
	@RequestMapping("/oauth/custom_confirm_access")
	public void getAccessConfirmation( Map<String, Object> model,
	                                           HttpServletRequest request, HttpServletResponse response  ) throws Exception {
		whitelabelApprovalEndpoint.getAccessConfirmation( model, request );
		response.sendRedirect( "http://knooppunt.local:8080/oauth?" + request.getQueryString() );
	}
}
