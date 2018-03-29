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
import org.junit.Test;
import org.springframework.security.acls.model.Permission;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
public class TestDefaultAclPermissionFactory
{
	private static final AclPermission CUSTOM_PERM = AclPermission.create( 10, 'X' );

	private DefaultAclPermissionFactory permissionFactory = new DefaultAclPermissionFactory();

	@Test
	public void defaultPermissionsAreRegistered() {
		assertThat( permissionFactory.buildFromName( "read" ) ).isEqualTo( AclPermission.READ );
		assertThat( permissionFactory.buildFromName( "write" ) ).isEqualTo( AclPermission.WRITE );
		assertThat( permissionFactory.buildFromName( "create" ) ).isEqualTo( AclPermission.CREATE );
		assertThat( permissionFactory.buildFromName( "delete" ) ).isEqualTo( AclPermission.DELETE );
		assertThat( permissionFactory.buildFromName( "administration" ) ).isEqualTo( AclPermission.ADMINISTRATION );
		assertThat( permissionFactory.buildFromNames( Arrays.asList( "READ", "WRITE", "create", "DELETE", "ADMINISTRATION" ) ) )
				.containsExactly( AclPermission.READ, AclPermission.WRITE, AclPermission.CREATE, AclPermission.DELETE, AclPermission.ADMINISTRATION );
	}

	@Test
	public void defaultPermissionNamesAreAvailable() {
		assertThat( permissionFactory.getNameForPermission( AclPermission.READ ) ).isEqualTo( "read" );
		assertThat( permissionFactory.getNameForPermission( AclPermission.WRITE ) ).isEqualTo( "write" );
		assertThat( permissionFactory.getNameForPermission( AclPermission.CREATE ) ).isEqualTo( "create" );
		assertThat( permissionFactory.getNameForPermission( AclPermission.DELETE ) ).isEqualTo( "delete" );
		assertThat( permissionFactory.getNameForPermission( AclPermission.ADMINISTRATION ) ).isEqualTo( "administration" );
	}

	@Test
	public void registerCustomPermission() {
		permissionFactory.registerPermission( CUSTOM_PERM, "my-custom-permission" );
		assertThat( permissionFactory.getNameForPermission( CUSTOM_PERM ) ).isEqualTo( "my-custom-permission" );
		assertThat( permissionFactory.buildFromName( "MY-CUSTOM-PERMISSION" ) ).isEqualTo( CUSTOM_PERM );
		assertThat( permissionFactory.buildFromName( "my-custom-permission" ) ).isEqualTo( CUSTOM_PERM );
		assertThat( permissionFactory.buildFromMask( CUSTOM_PERM.getMask() ) ).isEqualTo( CUSTOM_PERM );
	}

	@Test
	public void registerCumulativePermission() {
		Permission combined = AclPermission.combine( AclPermission.READ, AclPermission.ADMINISTRATION );
		permissionFactory.registerPermission( combined, "read-admin" );
		assertThat( permissionFactory.getNameForPermission( combined ) ).isEqualTo( "read-admin" );
		assertThat( permissionFactory.buildFromName( "read-admin" ) ).isEqualTo( combined );
		assertThat( permissionFactory.buildFromName( "READ-ADMIN" ) ).isEqualTo( combined );
		assertThat( permissionFactory.buildFromMask( combined.getMask() ) ).isEqualTo( combined );
	}

	@Test
	public void exceptionIfMaskAlreadyPresent() {
		permissionFactory.registerPermission( CUSTOM_PERM, "my-custom-permission" );

		AclPermission other = AclPermission.create( 10, 'Y' );
		assertThatExceptionOfType( IllegalArgumentException.class )
				.isThrownBy( () -> permissionFactory.registerPermission( other, "my-other-permission" ) );
	}

	@Test
	public void exceptionIfNameAlreadyPresent() {
		AclPermission other = AclPermission.create( 12, 'Y' );
		assertThatExceptionOfType( IllegalArgumentException.class )
				.isThrownBy( () -> permissionFactory.registerPermission( other, "read" ) );
		assertThatExceptionOfType( IllegalArgumentException.class )
				.isThrownBy( () -> permissionFactory.registerPermission( other, "ADMINISTRATION" ) );
	}

	@Test
	public void exceptionIfNameCouldNotBeDetermined() {
		assertThatExceptionOfType( IllegalArgumentException.class )
				.isThrownBy( () -> permissionFactory.getNameForPermission( AclPermission.create( 12 ) ) );
	}
}
