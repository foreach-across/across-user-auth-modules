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
import com.foreach.across.modules.spring.security.acl.business.AclPermission;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * Provides easy access to ACL related services and checking.
 *
 * @author Arne Vandamme
 */
public interface AclSecurityService
{
	MutableAcl getAcl( IdBasedEntity entity );

	MutableAcl getAcl( ObjectIdentity objectIdentity );

	MutableAcl createAcl( IdBasedEntity entity );

	MutableAcl createAcl( ObjectIdentity objectIdentity );

	MutableAcl createAclWithParent( IdBasedEntity entity, IdBasedEntity parent );

	MutableAcl createAclWithParent( ObjectIdentity objectIdentity, ObjectIdentity parentObjectIdentity );

	void allow( SecurityPrincipal principal, IdBasedEntity entity, AclPermission... aclPermissions );

	void allow( GrantedAuthority authority, IdBasedEntity entity, AclPermission... aclPermissions );

	void allow( String authority, IdBasedEntity entity, AclPermission... aclPermissions );

	void allow( Authentication authentication, IdBasedEntity entity, AclPermission... aclPermissions );

	void revoke( SecurityPrincipal principal, IdBasedEntity entity, AclPermission... aclPermissions );

	void revoke( GrantedAuthority authority, IdBasedEntity entity, AclPermission... aclPermissions );

	void revoke( String authority, IdBasedEntity entity, AclPermission... aclPermissions );

	void revoke( Authentication authentication, IdBasedEntity entity, AclPermission... aclPermissions );

	void deny( SecurityPrincipal principal, IdBasedEntity entity, AclPermission... aclPermissions );

	void deny( GrantedAuthority authority, IdBasedEntity entity, AclPermission... aclPermissions );

	void deny( String authority, IdBasedEntity entity, AclPermission... aclPermissions );

	void deny( Authentication authentication, IdBasedEntity entity, AclPermission... aclPermissions );

	void deleteAcl( IdBasedEntity entity, boolean deleteChildren );

	MutableAcl updateAcl( MutableAcl acl );

	AclOperations createAclOperations( MutableAcl acl );

	void changeAclOwner( MutableAcl acl, SecurityPrincipal principal );

	void changeAclParent( IdBasedEntity entity, IdBasedEntity parent );

	void changeAclParent( ObjectIdentity entity, ObjectIdentity parent );

	void changeAclParent( MutableAcl acl, IdBasedEntity parent );

	void changeAclParent( MutableAcl acl, ObjectIdentity parent );

	boolean hasPermission( Authentication authentication, IdBasedEntity entity, AclPermission permission );

	boolean hasPermission( SecurityPrincipal principal, IdBasedEntity entity, AclPermission permission );

	void setDefaultParentAcl( IdBasedEntity entity );
}
