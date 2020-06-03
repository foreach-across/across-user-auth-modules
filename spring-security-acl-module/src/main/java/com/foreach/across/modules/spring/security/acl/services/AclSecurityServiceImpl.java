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
import com.foreach.across.modules.spring.security.acl.business.SecurityPrincipalSid;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipalHierarchy;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.PermissionFactory;
import org.springframework.security.acls.model.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.foreach.across.modules.spring.security.acl.support.AclUtils.*;

/**
 * @author Arne Vandamme
 */
@Service
@RequiredArgsConstructor
public class AclSecurityServiceImpl implements QueryableAclSecurityService
{
	private final SecurityPrincipalAclService aclService;
	private final PermissionEvaluator aclPermissionEvaluator;
	private final PermissionFactory permissionFactory;

	private IdBasedEntity defaultParent;

	@Override
	public void setDefaultParentAcl( IdBasedEntity entity ) {
		defaultParent = entity;
	}

	@Override
	public IdBasedEntity getDefaultParentAcl() {
		return defaultParent;
	}

	@Transactional(readOnly = true)
	@Override
	public MutableAcl getAcl( IdBasedEntity entity ) {
		return getAcl( objectIdentity( entity ) );
	}

	@Transactional(readOnly = true)
	@Override
	public MutableAcl getAcl( ObjectIdentity objectIdentity ) {
		try {
			return (MutableAcl) aclService.readAclById( objectIdentity );
		}
		catch ( NotFoundException nfe ) {
			return null;
		}
	}

	@Transactional
	@Override
	public MutableAcl createAcl( @NonNull IdBasedEntity entity ) {
		return createAcl( objectIdentity( entity ) );
	}

	@Transactional
	@Override
	public MutableAcl createAcl( ObjectIdentity objectIdentity ) {
		val parentIdentity = objectIdentity( defaultParent );
		return createAclWithParent( objectIdentity, !objectIdentity.equals( parentIdentity ) ? parentIdentity : null );
	}

	@Transactional
	@Override
	public MutableAcl createAclWithParent( @NonNull IdBasedEntity entity, IdBasedEntity parent ) {
		return createAclWithParent( objectIdentity( entity ), objectIdentity( parent ) );
	}

	@Transactional
	@Override
	public MutableAcl createAclWithParent( @NonNull ObjectIdentity objectIdentity, ObjectIdentity parent ) {
		MutableAcl acl;

		try {
			acl = (MutableAcl) aclService.readAclById( objectIdentity );

			changeAclParent( acl, parent );

			return acl;
		}
		catch ( NotFoundException nfe ) {
			acl = aclService.createAcl( objectIdentity );
		}

		if ( parent != null ) {
			Acl parentAcl = getAcl( parent );

			if ( parentAcl == null ) {
				parentAcl = createAcl( parent );
			}

			acl.setParent( parentAcl );
		}

		return aclService.updateAcl( acl );
	}

	@Transactional
	@Override
	public void allow( SecurityPrincipal principal, IdBasedEntity entity, AclPermission... permissions ) {
		updateAces( sid( principal ), entity, true, permissions );
	}

	@Transactional
	@Override
	public void allow( GrantedAuthority authority, IdBasedEntity entity, AclPermission... aclPermissions ) {
		updateAces( sid( authority ), entity, true, aclPermissions );
	}

	@Transactional
	@Override
	public void deny( SecurityPrincipal principal, IdBasedEntity entity, AclPermission... aclPermissions ) {
		updateAces( sid( principal ), entity, false, aclPermissions );
	}

	@Transactional
	@Override
	public void deny( GrantedAuthority authority, IdBasedEntity entity, AclPermission... aclPermissions ) {
		updateAces( sid( authority ), entity, false, aclPermissions );
	}

	@Transactional
	@Override
	public void revoke( SecurityPrincipal principal, IdBasedEntity entity, AclPermission... aclPermissions ) {
		updateAces( sid( principal ), entity, null, aclPermissions );
	}

	@Transactional
	@Override
	public void revoke( GrantedAuthority authority, IdBasedEntity entity, AclPermission... aclPermissions ) {
		updateAces( sid( authority ), entity, null, aclPermissions );
	}

	@Transactional
	@Override
	public void allow( String authority, IdBasedEntity entity, AclPermission... aclPermissions ) {
		updateAces( sidForAuthority( authority ), entity, true, aclPermissions );
	}

	@Transactional
	@Override
	public void revoke( String authority, IdBasedEntity entity, AclPermission... aclPermissions ) {
		updateAces( sidForAuthority( authority ), entity, null, aclPermissions );
	}

	@Transactional
	@Override
	public void deny( String authority, IdBasedEntity entity, AclPermission... aclPermissions ) {
		updateAces( sidForAuthority( authority ), entity, false, aclPermissions );
	}

	@Transactional
	@Override
	public void allow( Authentication authentication, IdBasedEntity entity, AclPermission... aclPermissions ) {
		updateAces( sid( authentication ), entity, true, aclPermissions );
	}

	@Transactional
	@Override
	public void revoke( Authentication authentication, IdBasedEntity entity, AclPermission... aclPermissions ) {
		updateAces( sid( authentication ), entity, null, aclPermissions );
	}

	@Transactional
	@Override
	public void deny( Authentication authentication, IdBasedEntity entity, AclPermission... aclPermissions ) {
		updateAces( sid( authentication ), entity, false, aclPermissions );
	}

	private void updateAces( Sid sid, IdBasedEntity entity, Boolean grantAction, AclPermission... aclPermissions ) {
		MutableAclService service = aclService;

		boolean shouldRevoke = grantAction == null;
		ObjectIdentity objectIdentity = objectIdentity( entity );

		MutableAcl acl;

		try {
			acl = (MutableAcl) service.readAclById( objectIdentity );
		}
		catch ( NotFoundException nfe ) {
			acl = aclService.createAcl( objectIdentity );
		}

		for ( AclPermission aclPermission : aclPermissions ) {
			List<AccessControlEntry> aces = acl.getEntries();
			int index = aces.size();
			AccessControlEntry ace = findAce( aces, sid, aclPermission );

			if ( ace != null && ( shouldRevoke || ace.isGranting() != grantAction ) ) {
				index = aces.indexOf( ace );
				acl.deleteAce( index );

				ace = null;
			}

			if ( ace == null && !shouldRevoke ) {
				acl.insertAce( index, aclPermission, sid, grantAction );
			}
		}

		aclService.updateAcl( acl );
	}

	private AccessControlEntry findAce( List<AccessControlEntry> aces, Sid sid, AclPermission permission ) {
		for ( AccessControlEntry ace : aces ) {
			if ( ace.getSid().equals( sid ) && ace.getPermission().equals( permission ) ) {
				return ace;
			}
		}
		return null;
	}

	@Transactional
	@Override
	public void deleteAcl( IdBasedEntity entity, boolean deleteChildren ) {
		aclService.deleteAcl( objectIdentity( entity ), deleteChildren );
	}

	@Transactional
	@Override
	public MutableAcl updateAcl( MutableAcl acl ) {
		return aclService.updateAcl( acl );
	}

	@Override
	public AclOperations createAclOperations( MutableAcl acl ) {
		return new AclOperations( acl, permissionFactory );
	}

	@Transactional
	@Override
	public void changeAclOwner( MutableAcl acl, SecurityPrincipal principal ) {
		acl.setOwner( sid( principal ) );
		updateAcl( acl );
	}

	@Transactional
	@Override
	public void changeAclParent( IdBasedEntity entity, IdBasedEntity parent ) {
		changeAclParent( getAcl( entity ), parent );
	}

	@Transactional
	@Override
	public void changeAclParent( ObjectIdentity entity, ObjectIdentity parent ) {
		changeAclParent( getAcl( entity ), parent );
	}

	@Transactional
	@Override
	public void changeAclParent( MutableAcl acl, IdBasedEntity parent ) {
		changeAclParent( acl, objectIdentity( parent ) );
	}

	@Transactional
	@Override
	public void changeAclParent( MutableAcl acl, ObjectIdentity parent ) {
		if ( acl != null ) {
			Acl parentAcl = acl.getParentAcl();

			if ( parent == null && parentAcl != null ) {
				acl.setParent( null );
				updateAcl( acl );
			}
			else if ( parent != null ) {
				Acl newParentAcl = getAcl( parent );

				if ( newParentAcl == null ) {
					newParentAcl = createAcl( parent );
				}

				if ( parentAcl == null || !parentAcl.getObjectIdentity().equals( newParentAcl.getObjectIdentity() ) ) {
					acl.setParent( newParentAcl );
					updateAcl( acl );
				}
			}
		}
	}

	@Transactional(readOnly = true)
	@Override
	public boolean hasPermission( Authentication authentication, IdBasedEntity entity, AclPermission permission ) {
		return aclPermissionEvaluator.hasPermission( authentication, entity, permission );
	}

	@Transactional(readOnly = true)
	@Override
	public boolean hasPermission( SecurityPrincipal principal, IdBasedEntity entity, AclPermission permission ) {
		List<Sid> sids = buildSids( principal );
		List<Permission> aclPermissions = Collections.singletonList( permission );

		try {
			// Lookup only ACLs for SIDs we're interested in
			Acl acl = aclService.readAclById( objectIdentity( entity ), sids );

			if ( acl.isGranted( aclPermissions, sids, false ) ) {
				return true;
			}
		}
		catch ( NotFoundException nfe ) {
			return false;
		}

		return false;
	}

	@Transactional(readOnly = true)
	@Override
	public Collection<ObjectIdentity> getObjectIdentitiesWithAclEntriesForPrincipal( SecurityPrincipal principal ) {
		return aclService.findObjectIdentitiesWithAclForSid( sid( principal ) );
	}

	private List<Sid> buildSids( SecurityPrincipal principal ) {
		Collection<SecurityPrincipal> principals = new LinkedList<>();
		principals.add( principal );

		if ( principal instanceof SecurityPrincipalHierarchy ) {
			principals.addAll( ( (SecurityPrincipalHierarchy) principal ).getParentPrincipals() );
		}

		List<Sid> sids = new ArrayList<>();
		Collection<Sid> authoritySids = new LinkedHashSet<>();

		for ( SecurityPrincipal candidate : principals ) {
			sids.add( SecurityPrincipalSid.of( candidate ) );

			for ( GrantedAuthority authority : candidate.getAuthorities() ) {
				authoritySids.add( new GrantedAuthoritySid( authority ) );
			}
		}

		sids.addAll( authoritySids );

		return sids;
	}
}
