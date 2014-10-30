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
package com.foreach.across.modules.user.controllers;

import com.foreach.across.modules.adminweb.annotations.AdminWebController;
import com.foreach.across.modules.spring.security.acl.services.QueryableAclSecurityService;
import com.foreach.across.modules.user.business.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Arne Vandamme
 */
@AdminWebController
public class UserAclController
{
	@Autowired
	private QueryableAclSecurityService aclSecurityService;

	@RequestMapping(value = "/users/{id}/acl")
	public String viewAcl( @PathVariable("id") User user, Model model ) {
		model.addAttribute( "user", user );
		model.addAttribute( "objects", aclSecurityService.getObjectIdentitiesWithAclEntriesForPrincipal( user ) );

		return "th/user/users/acl";
	}
}
