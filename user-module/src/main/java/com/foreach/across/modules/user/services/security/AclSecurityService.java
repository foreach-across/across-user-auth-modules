package com.foreach.across.modules.user.services.security;

import com.foreach.across.modules.hibernate.business.IdBasedEntity;
import com.foreach.across.modules.spring.security.business.AclPermission;
import com.foreach.across.modules.spring.security.business.SecurityPrincipal;
import com.foreach.across.modules.user.business.Permission;
import com.foreach.across.modules.user.business.Role;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.core.Authentication;

/**
 * Provides easy access to ACL related services and checking.
 *
 * @author Arne Vandamme
 */
public interface AclSecurityService
{
	MutableAcl getAcl( IdBasedEntity entity );

	MutableAcl createAcl( IdBasedEntity entity );

	MutableAcl createAclWithParent( IdBasedEntity entity, IdBasedEntity parent );

	void allow( SecurityPrincipal principal, IdBasedEntity entity, AclPermission... aclPermissions );

	void allow( Role role, IdBasedEntity entity, AclPermission... aclPermissions );

	void allow( Permission permission, IdBasedEntity entity, AclPermission... aclPermissions );

	void allow( String authority, IdBasedEntity entity, AclPermission... aclPermissions );

	void revoke( SecurityPrincipal principal, IdBasedEntity entity, AclPermission... aclPermissions );

	void revoke( Role role, IdBasedEntity entity, AclPermission... aclPermissions );

	void revoke( Permission permission, IdBasedEntity entity, AclPermission... aclPermissions );

	void revoke( String authority, IdBasedEntity entity, AclPermission... aclPermissions );

	void deny( SecurityPrincipal principal, IdBasedEntity entity, AclPermission... aclPermissions );

	void deny( Role role, IdBasedEntity entity, AclPermission... aclPermissions );

	void deny( Permission permission, IdBasedEntity entity, AclPermission... aclPermissions );

	void deny( String authority, IdBasedEntity entity, AclPermission... aclPermissions );

	void deleteAcl( IdBasedEntity entity, boolean deleteChildren );

	MutableAcl updateAcl( MutableAcl acl );

	void changeAclOwner( MutableAcl acl, SecurityPrincipal principal );

	boolean hasPermission( Authentication authentication, IdBasedEntity entity, AclPermission permission );

	boolean hasPermission( SecurityPrincipal principal, IdBasedEntity entity, AclPermission permission );
}
