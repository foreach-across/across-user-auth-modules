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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.acls.domain.PermissionFactory;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;

import java.util.*;

/**
 * Helper class that allows you to easily perform bulk changes on a {@link org.springframework.security.acls.model.MutableAcl}.
 *
 * @author Arne Vandamme
 * @see AclSecurityService
 * @since 3.0.0
 */
@RequiredArgsConstructor
public class AclOperations
{
	@Getter
	private final MutableAcl acl;
	private final PermissionFactory permissionFactory;

	/**
	 * Ensures the specified permissions - specified by mask - are granted for the sid.
	 * <p/>
	 * NOTE: only positive masks are allowed for this method.
	 *
	 * @param sid   to modify the ACL for
	 * @param masks that should be granted
	 */
	public void allow( Sid sid, int... masks ) {
		Permission[] permissions = new Permission[masks.length];
		for ( int i = 0; i < masks.length; i++ ) {
			permissions[i] = permissionFactory.buildFromMask( masks[i] );
		}
		allow( sid, permissions );
	}

	/**
	 * Ensures the specified permissions - specified by name - are granted for the sid.
	 *
	 * @param sid         to modify the ACL for
	 * @param permissions that should be granted
	 */
	public void allow( Sid sid, String... permissions ) {
		allow( sid, permissionFactory.buildFromNames( Arrays.asList( permissions ) ) );
	}

	/**
	 * Ensures the specified permissions are granted for the sid.
	 *
	 * @param sid         to modify the ACL for
	 * @param permissions that should be granted
	 */
	public void allow( Sid sid, Permission... permissions ) {
		allow( sid, Arrays.asList( permissions ) );
	}

	/**
	 * Ensures the specified permissions are granted for the sid.
	 *
	 * @param sid         to modify the ACL for
	 * @param permissions that should be granted
	 */
	public void allow( Sid sid, List<Permission> permissions ) {
		for ( Permission aclPermission : permissions ) {
			List<AccessControlEntry> aces = acl.getEntries();
			int index = aces.size();
			AccessControlEntry ace = findAce( aces, sid, aclPermission );

			if ( ace != null && !ace.isGranting() ) {
				index = aces.indexOf( ace );
				acl.deleteAce( index );

				ace = null;
			}

			if ( ace == null ) {
				acl.insertAce( index, aclPermission, sid, true );
			}
		}
	}

	/**
	 * Ensures the specified permissions - specified by mask - are denied for the sid.
	 * <p/>
	 * NOTE: only positive masks are allowed for this method.
	 *
	 * @param sid   to modify the ACL for
	 * @param masks that should be denied
	 */
	public void deny( Sid sid, int... masks ) {
		Permission[] permissions = new Permission[masks.length];
		for ( int i = 0; i < masks.length; i++ ) {
			permissions[i] = permissionFactory.buildFromMask( masks[i] );
		}
		deny( sid, permissions );
	}

	/**
	 * Ensures the specified permissions - specified by name - are denied for the sid.
	 *
	 * @param sid         to modify the ACL for
	 * @param permissions that should be denied
	 */
	public void deny( Sid sid, String... permissions ) {
		deny( sid, permissionFactory.buildFromNames( Arrays.asList( permissions ) ) );
	}

	/**
	 * Ensures the specified permissions are denied for the sid.
	 *
	 * @param sid         to modify the ACL for
	 * @param permissions that should be denied
	 */
	public void deny( Sid sid, Permission... permissions ) {
		deny( sid, Arrays.asList( permissions ) );
	}

	/**
	 * Ensures the specified permissions are denied for the sid.
	 *
	 * @param sid         to modify the ACL for
	 * @param permissions that should be denied
	 */
	public void deny( Sid sid, List<Permission> permissions ) {
		for ( Permission aclPermission : permissions ) {
			List<AccessControlEntry> aces = acl.getEntries();
			int index = aces.size();
			AccessControlEntry ace = findAce( aces, sid, aclPermission );

			if ( ace != null && ace.isGranting() ) {
				index = aces.indexOf( ace );
				acl.deleteAce( index );

				ace = null;
			}

			if ( ace == null ) {
				acl.insertAce( index, aclPermission, sid, false );
			}
		}
	}

	/**
	 * Revokes the specified permissions - specified by mask - for the sid.
	 * Both granting and non-granting ACEs will be removed.
	 * <p/>
	 * NOTE: only positive masks are allowed for this method.
	 *
	 * @param sid   to modify the ACL for
	 * @param masks that should be revoked
	 */
	public void revoke( Sid sid, int... masks ) {
		Permission[] permissions = new Permission[masks.length];
		for ( int i = 0; i < masks.length; i++ ) {
			permissions[i] = permissionFactory.buildFromMask( masks[i] );
		}
		revoke( sid, permissions );
	}

	/**
	 * Revokes the specified permissions - specified by name - for the sid.
	 * Both granting and non-granting ACEs will be removed.
	 *
	 * @param sid         to modify the ACL for
	 * @param permissions that should be revoked
	 */
	public void revoke( Sid sid, String... permissions ) {
		revoke( sid, permissionFactory.buildFromNames( Arrays.asList( permissions ) ) );
	}

	/**
	 * Revokes the specified permissions - specified by name - for the sid.
	 * Both granting and non-granting ACEs will be removed.
	 *
	 * @param sid         to modify the ACL for
	 * @param permissions that should be revoked
	 */
	public void revoke( Sid sid, Permission... permissions ) {
		revoke( sid, Arrays.asList( permissions ) );
	}

	/**
	 * Revokes the specified permissions - specified by name - for the sid.
	 * Both granting and non-granting ACEs will be removed.
	 *
	 * @param sid         to modify the ACL for
	 * @param permissions that should be revoked
	 */
	public void revoke( Sid sid, List<Permission> permissions ) {
		for ( Permission aclPermission : permissions ) {
			List<AccessControlEntry> aces = acl.getEntries();
			AccessControlEntry ace = findAce( aces, sid, aclPermission );

			if ( ace != null ) {
				acl.deleteAce( aces.indexOf( ace ) );
			}
		}
	}

	/**
	 * Applies a collection of permission rules to the ACL.
	 * A positive mask number will result in an allow being called,
	 * a negative mask number will result in an explicit deny.
	 * If any of the original permissions is missing from the masks
	 * collection (either with allow or deny value), it will be revoked.
	 * If a mask is missing from the original permissions, it will be ignored.
	 *
	 * @param sid         to modify the ACL for
	 * @param permissions that were checked
	 * @param masks       with the expected state for the checked permissions
	 */
	public void apply( Sid sid, Permission[] permissions, int[] masks ) {
		apply( sid, Arrays.asList( permissions ), masks );
	}

	/**
	 * Applies a collection of permission rules to the ACL.
	 * A positive mask number will result in an allow being called,
	 * a negative mask number will result in an explicit deny.
	 * If any of the original permissions is missing from the masks
	 * collection (either with allow or deny value), it will be revoked.
	 * * If a mask is missing from the original permissions, it will be ignored.
	 *
	 * @param sid         to modify the ACL for
	 * @param permissions that were checked
	 * @param masks       with the expected state for the checked permissions
	 */
	public void apply( Sid sid, Collection<Permission> permissions, int[] masks ) {
		List<Permission> allowed = new ArrayList<>( permissions.size() );
		List<Permission> denied = new ArrayList<>( permissions.size() );
		List<Permission> revoked = new ArrayList<>( permissions.size() );

		for ( int mask : masks ) {
			Permission perm = permissionFactory.buildFromMask( Math.abs( mask ) );
			// only allow permission changes if they were in the original set
			if ( permissions.contains( perm ) ) {
				if ( mask < 0 ) {
					denied.add( perm );
				}
				else {
					allowed.add( perm );
				}
			}
		}

		for ( Permission permission : permissions ) {
			if ( !allowed.contains( permission ) && !denied.contains( permission ) ) {
				revoked.add( permission );
			}
		}

		revoke( sid, revoked );
		allow( sid, allowed );
		deny( sid, denied );
	}

	/**
	 * Retrieve the ACE for a particular sid/permission combination.
	 * Will only return a result if there is an entry with exactly that permission.
	 * <p/>
	 * NOTE: Never use this for actual security checks, only for administrative operations.
	 *
	 * @param sid        to check for
	 * @param permission to check
	 * @return access control entry
	 */
	public Optional<AccessControlEntry> getAce( Sid sid, Permission permission ) {
		return Optional.ofNullable( findAce( acl.getEntries(), sid, permission ) );
	}

	private AccessControlEntry findAce( List<AccessControlEntry> aces, Sid sid, Permission permission ) {
		for ( AccessControlEntry ace : aces ) {
			if ( ace.getSid().equals( sid ) && ace.getPermission().equals( permission ) ) {
				return ace;
			}
		}
		return null;
	}
}
