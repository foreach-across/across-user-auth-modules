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

import com.foreach.across.modules.spring.security.acl.business.AclPermission;
import org.springframework.security.acls.domain.DefaultPermissionFactory;
import org.springframework.security.acls.model.Permission;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Default implementation of {@link AclPermissionFactory}.
 * <p/>
 * NOTE: In this implementation permission names are always case insensitive and stored as lowercase.
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
public class DefaultAclPermissionFactory extends DefaultPermissionFactory implements AclPermissionFactory
{
	private final Map<Permission, String> namesForPermission = new HashMap<>();

	public DefaultAclPermissionFactory() {
		this( Collections.emptyMap() );
		registerPublicPermissions( AclPermission.class );
	}

	protected DefaultAclPermissionFactory( Map<String, ? extends Permission> namedPermissions ) {
		super( namedPermissions );
	}

	@Override
	public Permission buildFromName( String name ) {
		return super.buildFromName( name.toLowerCase() );
	}

	@Override
	public void registerPermission( Permission permission, String permissionName ) {
		String name = permissionName.toLowerCase( Locale.ENGLISH );
		super.registerPermission( permission, name );
		namesForPermission.put( permission, name );
	}

	@Override
	public String getNameForPermission( Permission permission ) {
		return namesForPermission.computeIfAbsent( permission, perm -> {
			throw new IllegalArgumentException( "Unknown permission " + perm );
		} );
	}
}
