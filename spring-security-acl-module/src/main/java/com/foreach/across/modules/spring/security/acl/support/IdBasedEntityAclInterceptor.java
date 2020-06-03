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
package com.foreach.across.modules.spring.security.acl.support;

import com.foreach.across.core.annotations.ConditionalOnAcrossModule;
import com.foreach.across.modules.hibernate.aop.EntityInterceptorAdapter;
import com.foreach.across.modules.hibernate.business.IdBasedEntity;
import com.foreach.across.modules.spring.security.acl.services.AclSecurityService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>Base class for an interceptor hooked to repository persistence methods.
 * Useful for creating ACLs when saving or deleting instances.</p>
 * <p>
 * Implementations will be picked up automatically by the AcrossHibernateModule or AcrossHibernateJpaModule
 * if one of them is active.</p>
 *
 * @author Arne Vandamme
 * @deprecated since 4.0.0 - This class offers no real added functionality and will be removed in a future release
 */
@ConditionalOnAcrossModule(anyOf = { "AcrossHibernateModule", "AcrossHibernateJpaModule" })
@Deprecated
public abstract class IdBasedEntityAclInterceptor<T extends IdBasedEntity> extends EntityInterceptorAdapter<T>
{
	@Autowired
	private AclSecurityService aclSecurityService;

	protected AclSecurityService aclSecurityService() {
		return aclSecurityService;
	}
}
