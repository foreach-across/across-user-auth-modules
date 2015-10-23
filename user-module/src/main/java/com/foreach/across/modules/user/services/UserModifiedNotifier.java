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
package com.foreach.across.modules.user.services;

import com.foreach.across.modules.spring.security.SpringSecurityModuleCache;
import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalService;
import com.foreach.across.modules.user.UserModuleCache;
import com.foreach.across.modules.user.dto.UserDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

/**
 * Separate instance to enforce cacheable intercept.
 * Todo: check if this can be done through an entity interceptor.
 *
 * @author Arne Vandamme
 */
public class UserModifiedNotifier
{
	@Autowired
	private SecurityPrincipalService securityPrincipalService;

	@Caching(
			evict = {
					@CacheEvict(value = UserModuleCache.USERS, key = "'username:' + #original.username", condition = "#original.username != #update.username"),
					@CacheEvict(value = UserModuleCache.USERS, key = "'email:' + #original.email", condition = "#original.email != #update.email"),
					@CacheEvict(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, key = "#original.principalName", condition = "#original.principalName != #update.principalName")
			}
	)
	public void update( UserDto original, UserDto update ) {
		String oldPrincipalName = StringUtils.lowerCase( original.getUsername() );
		String newPrincipalName = StringUtils.lowerCase( update.getUsername() );

		if ( !StringUtils.equals( oldPrincipalName, newPrincipalName ) ) {
			securityPrincipalService.publishRenameEvent( oldPrincipalName, newPrincipalName );
		}
	}
}
