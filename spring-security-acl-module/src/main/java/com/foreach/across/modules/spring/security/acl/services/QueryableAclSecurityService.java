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
package com.foreach.across.modules.spring.security.acl.services;

import com.foreach.across.modules.hibernate.business.IdBasedEntity;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import org.springframework.security.acls.model.ObjectIdentity;

import java.util.Collection;

/**
 * Extends the {@link com.foreach.across.modules.spring.security.acl.services.AclSecurityService} with
 * methods for querying the entire ACL configuration.
 *
 * @author Arne Vandamme
 */
public interface QueryableAclSecurityService extends AclSecurityService
{
	/**
	 * Retrieve the default entity that is configured to serve as parent ACL for any ACL
	 * created that does not have a parent specified.
	 *
	 * @return entity or null
	 */
	IdBasedEntity getDefaultParentAcl();

	/**
	 * Retrieve the list of ObjectIdentities that have an ACL that contains the at least one entry for
	 * the given principal.  Only ACLs that contain the principal related entry directly are taken into
	 * account, inherited entries do not make a difference.
	 *
	 * @param principal Security principal to get the entries for.
	 * @return Collection of ObjectIdentity instances.
	 */
	Collection<ObjectIdentity> getObjectIdentitiesWithAclEntriesForPrincipal( SecurityPrincipal principal );
}
