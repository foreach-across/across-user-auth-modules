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

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.modules.spring.security.acl.business.AclSecurityEntity;
import com.foreach.across.modules.spring.security.acl.services.AclSecurityEntityService;
import com.foreach.across.modules.spring.security.acl.support.IdBasedEntityAclInterceptor;
import com.foreach.across.modules.user.UserModuleSettings;
import com.foreach.across.modules.user.business.Group;
import org.springframework.beans.factory.annotation.Autowired;

@AcrossDepends(required = "SpringSecurityAclModule")
public class GroupAclInterceptor extends IdBasedEntityAclInterceptor<Group>
{
	@Autowired
	private UserModuleSettings settings;

	@Autowired
	private AclSecurityEntityService aclSecurityEntityService;

	@Override
	public void afterCreate( Group entity ) {
		if ( settings.isEnableDefaultAcls() ) {
			AclSecurityEntity groupParentEntity = aclSecurityEntityService.getSecurityEntityByName( "groups" );
			aclSecurityService().createAclWithParent( entity, groupParentEntity );
		}
	}

	@Override
	public void beforeDelete( Group entity, boolean isSoftDelete ) {
		if ( settings.isEnableDefaultAcls() ) {
			aclSecurityService().deleteAcl( entity, true );
		}
	}
}
