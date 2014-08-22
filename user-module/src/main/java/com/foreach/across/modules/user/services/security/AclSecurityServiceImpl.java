package com.foreach.across.modules.user.services.security;

import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.modules.hibernate.business.IdBasedEntity;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.modules.spring.security.business.AclPermission;
import com.foreach.across.modules.spring.security.business.SecurityPrincipal;
import com.foreach.across.modules.spring.security.business.SecurityPrincipalSid;
import com.foreach.across.modules.user.business.Permission;
import com.foreach.across.modules.user.business.Role;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;

import java.util.List;

/**
 * @author Arne Vandamme
 */
@Service
public class AclSecurityServiceImpl implements AclSecurityService
{
	@Autowired
	private AcrossContextBeanRegistry contextBeanRegistry;

	private MutableAclService fetchedAclService;

	@Transactional(readOnly = true)
	@Override
	public MutableAcl getAcl( IdBasedEntity entity ) {
		return (MutableAcl) aclService().readAclById( identity( entity ) );
	}

	@Transactional
	@Override
	public MutableAcl createAcl( IdBasedEntity entity ) {
		return createAclWithParent( entity, null );
	}

	@Transactional
	@Override
	public MutableAcl createAclWithParent( IdBasedEntity entity, IdBasedEntity parent ) {
		MutableAclService aclService = aclService();

		ObjectIdentity oi = identity( entity );

		MutableAcl acl;

		try {
			acl = (MutableAcl) aclService.readAclById( oi );
		}
		catch ( NotFoundException nfe ) {
			acl = aclService.createAcl( oi );
		}

		if ( parent != null ) {
			Acl parentAcl = aclService.readAclById( identity( parent ) );
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
	public void allow( Role role, IdBasedEntity entity, AclPermission... aclPermissions ) {
		updateAces( sid( role ), entity, true, aclPermissions );
	}

	@Transactional
	@Override
	public void allow( Permission permission, IdBasedEntity entity, AclPermission... aclPermissions ) {
		updateAces( sid( permission ), entity, true, aclPermissions );
	}

	@Transactional
	@Override
	public void deny( SecurityPrincipal principal, IdBasedEntity entity, AclPermission... aclPermissions ) {
		updateAces( sid( principal ), entity, false, aclPermissions );
	}

	@Transactional
	@Override
	public void deny( Role role, IdBasedEntity entity, AclPermission... aclPermissions ) {
		updateAces( sid( role ), entity, false, aclPermissions );
	}

	@Transactional
	@Override
	public void deny( Permission permission, IdBasedEntity entity, AclPermission... aclPermissions ) {
		updateAces( sid( permission ), entity, false, aclPermissions );
	}

	@Transactional
	@Override
	public void revoke( SecurityPrincipal principal, IdBasedEntity entity, AclPermission... aclPermissions ) {
		updateAces( sid( principal ), entity, null, aclPermissions );
	}

	@Transactional
	@Override
	public void revoke( Role role, IdBasedEntity entity, AclPermission... aclPermissions ) {
		updateAces( sid( role ), entity, null, aclPermissions );
	}

	@Transactional
	@Override
	public void revoke( Permission permission, IdBasedEntity entity, AclPermission... aclPermissions ) {
		updateAces( sid( permission ), entity, null, aclPermissions );
	}

	@Transactional
	@Override
	public void allow( String authority, IdBasedEntity entity, AclPermission... aclPermissions ) {
		updateAces( sid( authority ), entity, true, aclPermissions );
	}

	@Transactional
	@Override
	public void revoke( String authority, IdBasedEntity entity, AclPermission... aclPermissions ) {
		updateAces( sid( authority ), entity, null, aclPermissions );
	}

	@Transactional
	@Override
	public void deny( String authority, IdBasedEntity entity, AclPermission... aclPermissions ) {
		updateAces( sid( authority ), entity, false, aclPermissions );
	}

	private void updateAces( Sid sid, IdBasedEntity entity, Boolean grantAction, AclPermission... aclPermissions ) {
		MutableAclService service = aclService();

		boolean shouldRevoke = grantAction == null;
		ObjectIdentity objectIdentity = identity( entity );

		MutableAcl acl;

		try {
			acl = (MutableAcl) service.readAclById( objectIdentity );
		}
		catch ( NotFoundException nfe ) {
			acl = fetchedAclService.createAcl( objectIdentity );
		}

		List<AccessControlEntry> aces = acl.getEntries();

		for ( AclPermission aclPermission : aclPermissions ) {
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

		fetchedAclService.updateAcl( acl );
	}

	private AccessControlEntry findAce( List<AccessControlEntry> aces, Sid sid, AclPermission permission ) {
		for ( AccessControlEntry ace : aces ) {
			if ( ace.getSid().equals( sid )  && ace.getPermission().equals( permission ) ) {
				return ace;
			}
		}
		return null;
	}

	@Transactional
	@Override
	public void deleteAcl( IdBasedEntity entity, boolean deleteChildren ) {
		aclService().deleteAcl( identity( entity ), deleteChildren );
	}

	@Transactional
	@Override
	public MutableAcl updateAcl( MutableAcl acl ) {
		return aclService().updateAcl( acl );
	}

	@Transactional
	@Override
	public void changeAclOwner( MutableAcl acl, SecurityPrincipal principal ) {
		acl.setOwner( sid( principal ) );
		updateAcl( acl );
	}

	@Transactional(readOnly = true)
	@Override
	public boolean hasPermission( Authentication authentication, IdBasedEntity entity, AclPermission permission ) {
		return false;
	}

	@Transactional(readOnly = true)
	@Override
	public boolean hasPermission( SecurityPrincipal principal, IdBasedEntity entity, AclPermission permission ) {
		return false;
	}

	private Sid sid( Role role ) {
		return sid( role.getName() );
	}

	private Sid sid( Permission permission ) {
		return sid( permission.getName() );
	}

	private Sid sid( String authority ) {
		return new GrantedAuthoritySid( authority );
	}

	private Sid sid( SecurityPrincipal principal ) {
		return new SecurityPrincipalSid( principal );
	}

	private ObjectIdentity identity( IdBasedEntity entity ) {
		return new ObjectIdentityImpl( ClassUtils.getUserClass( entity.getClass() ), entity.getId() );
	}

	private MutableAclService aclService() {
		if ( fetchedAclService == null ) {
			try {
				fetchedAclService = contextBeanRegistry.getBeanOfTypeFromModule( SpringSecurityModule.NAME,
				                                                                 MutableAclService.class );
			}
			catch ( BeansException be ) {
				throw new BeanInitializationException(
						"The AclService is not available.  The AclService is only available after the context " +
								"is bootstrapped entirely, perhaps you are running from an installer " +
								"in the wrong bootstrap phase?",
						be );
			}
		}

		return fetchedAclService;
	}
}
