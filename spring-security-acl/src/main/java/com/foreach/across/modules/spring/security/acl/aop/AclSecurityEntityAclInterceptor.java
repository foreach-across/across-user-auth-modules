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
 * Interceptor responsible for creating the actual {@link org.springframework.security.acls.model.Acl} backing
 * an {@link AclSecurityEntity}.  Because the {@link AclSecurityEntity} hierarchy is supposed to match the
 * {@link org.springframework.security.acls.model.Acl} hierarchy exactly, a possible default parent Acl configured
 * on the {@link com.foreach.across.modules.spring.security.acl.services.AclSecurityService} will be ignored.
 *
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
		// Always create with parent explicitly, so a possible default ACL does not apply
		aclSecurityService().createAclWithParent( entity, entity.getParent() );
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
