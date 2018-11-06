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

package com.foreach.across.modules.user.config.modules;

import com.foreach.across.core.annotations.ConditionalOnAcrossModule;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.adminweb.events.UserContextAdminMenuGroup;
import com.foreach.across.modules.spring.security.infrastructure.services.CurrentSecurityPrincipalProxy;
import com.foreach.across.modules.user.business.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

/**
 * Registers the user context menu in the admin web layout.
 * @author Steven Gentens
 * @since 3.1.0
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnAcrossModule(AdminWebModule.NAME)
class AdminWebUserContextMenuRegistrar
{
	private final CurrentSecurityPrincipalProxy currentSecurityPrincipalProxy;

	@EventListener
	public void configureUserContextAdminMenuItem( UserContextAdminMenuGroup userContextAdminMenuGroup ) {
		User user = currentSecurityPrincipalProxy.getPrincipal( User.class );
		if ( user != null ) {
			userContextAdminMenuGroup.setDisplayName( user.getDisplayName() );
		}
	}
}
