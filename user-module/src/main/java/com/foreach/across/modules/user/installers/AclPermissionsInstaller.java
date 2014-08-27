package com.foreach.across.modules.user.installers;

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.annotations.InstallerMethod;
import com.foreach.across.core.installers.InstallerPhase;
import com.foreach.across.modules.spring.security.acl.SpringSecurityAclModule;
import com.foreach.across.modules.spring.security.acl.business.AclAuthorities;
import com.foreach.across.modules.user.business.PermissionGroup;
import com.foreach.across.modules.user.business.Role;
import com.foreach.across.modules.user.services.PermissionService;
import com.foreach.across.modules.user.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Arne Vandamme
 */
@AcrossDepends(required = SpringSecurityAclModule.NAME)
@Installer(
		description = "Installs the ACL permissions if ACL module is enabled",
		version = 1,
        phase = InstallerPhase.AfterModuleBootstrap
)
public class AclPermissionsInstaller
{
	@Autowired
	private PermissionService permissionService;

	@Autowired
	private RoleService roleService;

	@InstallerMethod
	public void createPermissionsAndAddToAdminRole() {
		// Create the individual permissions
		permissionService.definePermission( AclAuthorities.TAKE_OWNERSHIP,
		                                    "Allows the user to change the ownership of any ACL.",
		                                    SpringSecurityAclModule.NAME );
		permissionService.definePermission( AclAuthorities.MODIFY_ACL,
		                                    "Allows the user to modify the entries of any ACL.",
		                                    SpringSecurityAclModule.NAME );
		permissionService.definePermission( AclAuthorities.AUDIT_ACL,
		                                    "Allows the user to modify the auditing settings of an ACL.  " +
				                                    "This permission is also required to change the auditing " +
				                                    "settings of an ACL already owned by the user.",
		                                    SpringSecurityAclModule.NAME );

		// Update permission group for ACL permissions
		PermissionGroup group = permissionService.getPermissionGroup( SpringSecurityAclModule.NAME );
		group.setTitle( "Module: " + SpringSecurityAclModule.NAME );
		group.setDescription( "Permissions for managing ACL security." );

		permissionService.save( group );

		// Add permissions to the admin role if it exists
		Role adminRole = roleService.getRole( "ROLE_ADMIN" );

		if ( adminRole != null ) {
			adminRole.addPermission( AclAuthorities.TAKE_OWNERSHIP, AclAuthorities.MODIFY_ACL,
			                         AclAuthorities.AUDIT_ACL );
			roleService.save( adminRole );
		}
	}
}
