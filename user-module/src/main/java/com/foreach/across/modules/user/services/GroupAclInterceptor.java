package com.foreach.across.modules.user.services;

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.modules.spring.security.acl.business.AclPermission;
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
		if (settings.isEnableDefaultAcls()) {
			AclSecurityEntity groupParentEntity = aclSecurityEntityService.getSecurityEntityByName( "groups" );
			aclSecurityService().createAclWithParent( entity, groupParentEntity );
		}
	}

	@Override
	public void afterUpdate( Group entity ) {
	}

	@Override
	public void beforeDelete( Group entity, boolean isSoftDelete ) {
		if (settings.isEnableDefaultAcls()) {
			aclSecurityService().deleteAcl( entity, true );
		}
	}
}
