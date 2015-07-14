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
package com.foreach.across.modules.spring.security.acl.aop;

import com.foreach.across.modules.spring.security.acl.business.AclSecurityEntity;
import com.foreach.across.modules.spring.security.acl.support.IdBasedEntityAclInterceptor;

/**
 * @author Arne Vandamme
 */
public class AclSecurityEntityAclInterceptor extends IdBasedEntityAclInterceptor<AclSecurityEntity>
{
	@Override
	public boolean handles( Class<?> entityClass ) {
		return AclSecurityEntity.class.equals( entityClass );
	}

	@Override
	public void afterCreate( AclSecurityEntity entity ) {
		AclSecurityEntity parent = entity.getParent();

		if ( parent != null ) {
			aclSecurityService().createAclWithParent( entity, parent );
		}
		else {
			aclSecurityService().createAcl( entity );
		}
	}

	@Override
	public void afterUpdate( AclSecurityEntity entity ) {
		aclSecurityService().changeAclParent( entity, entity.getParent() );
	}

	@Override
	public void afterDelete( AclSecurityEntity entity ) {
		aclSecurityService().deleteAcl( entity, true );
	}
}
