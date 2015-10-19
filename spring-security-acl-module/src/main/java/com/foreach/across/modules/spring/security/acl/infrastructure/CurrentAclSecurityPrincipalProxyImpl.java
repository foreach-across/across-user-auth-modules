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
package com.foreach.across.modules.spring.security.acl.infrastructure;

import com.foreach.across.core.annotations.Refreshable;
import com.foreach.across.modules.hibernate.business.IdBasedEntity;
import com.foreach.across.modules.spring.security.acl.business.AclPermission;
import com.foreach.across.modules.spring.security.acl.services.AclSecurityService;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import com.foreach.across.modules.spring.security.infrastructure.services.CurrentSecurityPrincipalProxyImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Arne Vandamme
 */
@Service
@Refreshable
public class CurrentAclSecurityPrincipalProxyImpl extends CurrentSecurityPrincipalProxyImpl implements CurrentAclSecurityPrincipalProxy
{
	@Autowired(required = false)
	private AclSecurityService aclSecurityService;

	@Override
	public boolean hasAclPermission( IdBasedEntity entity, AclPermission permission ) {
		SecurityPrincipal securityPrincipal = getPrincipal();

		return aclSecurityService != null
				&& isAuthenticated()
				&& aclSecurityService.hasPermission( securityPrincipal, entity, permission );
	}
}
