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
package com.foreach.across.modules.spring.security.acl.controllers;

import com.foreach.across.modules.adminweb.annotations.AdminWebController;
import com.foreach.across.modules.adminweb.menu.AdminMenuEvent;
import com.foreach.across.modules.spring.security.acl.services.SecurityPrincipalAclService;
import net.engio.mbassy.listener.Handler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Arne Vandamme
 */
@AdminWebController
@RequestMapping("/security/acl")
public class AclBrowserController
{
	@Autowired
	private SecurityPrincipalAclService aclService;

	@Handler
	public void registerMenu( AdminMenuEvent adminMenuEvent ) {
		adminMenuEvent.builder().item( "/security/acl", "ACL browser" );
	}

	@RequestMapping
	public String listAclClasses( @RequestParam(value = "c", required = false) Class selectedClass,
	                              @RequestParam(value = "id", required = false) Long id,
	                              @RequestParam(value = "all", required = false,
	                                            defaultValue = "false") boolean showAll,
	                              Model model ) {
		model.addAttribute( "selectedClass", selectedClass );
		model.addAttribute( "classes", aclService.getRegisteredAclClasses() );

		if ( selectedClass != null ) {
			if ( id != null ) {
				model.addAttribute( "acl", aclService.readAclById( new ObjectIdentityImpl( selectedClass, id ) ) );
			}

		}

		return "th/spring-security-acl/browser";
	}
}
