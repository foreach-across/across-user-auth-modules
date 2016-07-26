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

package com.foreach.across.modules.user.services.support;

import com.foreach.across.modules.user.business.BasicSecurityPrincipal;
import com.foreach.across.modules.user.business.UserDirectory;
import com.foreach.across.modules.user.services.UserDirectoryService;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link DefaultUserDirectoryStrategy} that will apply the
 * {@link UserDirectoryService#getDefaultUserDirectory()} to all principals that do not
 * have a user directory set.  This ensures that the requirement that all principals belong
 * to a user directory does not break compatibility with pre-userdirectory installations.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Service
public class DefaultUserDirectoryStrategyImpl implements DefaultUserDirectoryStrategy
{
	private final UserDirectoryService userDirectoryService;

	public DefaultUserDirectoryStrategyImpl( UserDirectoryService userDirectoryService ) {
		this.userDirectoryService = userDirectoryService;
	}

	@Override
	public UserDirectory getDefaultUserDirectory() {
		return userDirectoryService.getDefaultUserDirectory();
	}

	@Override
	public void apply( BasicSecurityPrincipal<?> securityPrincipal ) {
		if ( securityPrincipal.getUserDirectory() == null ) {
			securityPrincipal.setUserDirectory( userDirectoryService.getDefaultUserDirectory() );
		}
	}
}
