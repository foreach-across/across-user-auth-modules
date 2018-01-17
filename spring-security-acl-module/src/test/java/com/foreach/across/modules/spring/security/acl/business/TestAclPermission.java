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

package com.foreach.across.modules.spring.security.acl.business;

import org.junit.Test;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.CumulativePermission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
public class TestAclPermission
{
	@Test
	public void baseAndAclPermissionsAreEqual() {
		assertThat( AclPermission.CREATE ).isEqualTo( BasePermission.CREATE );
		assertThat( AclPermission.WRITE ).isEqualTo( BasePermission.WRITE );
		assertThat( AclPermission.DELETE ).isEqualTo( BasePermission.DELETE );
		assertThat( AclPermission.READ ).isEqualTo( BasePermission.READ );
		assertThat( AclPermission.ADMINISTRATION ).isEqualTo( BasePermission.ADMINISTRATION );
	}

	@Test
	public void baseAndAclPermissionHaveTheSamePattern() {
		assertThat( AclPermission.CREATE.getPattern() ).isEqualTo( BasePermission.CREATE.getPattern() );
		assertThat( AclPermission.WRITE.getPattern() ).isEqualTo( BasePermission.WRITE.getPattern() );
		assertThat( AclPermission.DELETE.getPattern() ).isEqualTo( BasePermission.DELETE.getPattern() );
		assertThat( AclPermission.READ.getPattern() ).isEqualTo( BasePermission.READ.getPattern() );
		assertThat( AclPermission.ADMINISTRATION.getPattern() ).isEqualTo( BasePermission.ADMINISTRATION.getPattern() );
	}

	@Test
	public void combine() {
		CumulativePermission permission = AclPermission.combine( AclPermission.READ, AclPermission.WRITE );
		assertThat( permission ).isNotNull();
		assertThat( permission.getMask() ).isEqualTo( AclPermission.READ.getMask() | AclPermission.WRITE.getMask() );
		assertThat( permission.getPattern() ).isEqualTo( "..............................WR" );
	}

	@Test
	@SuppressWarnings( "all" )
	public void createPermissionWithoutCode() {
		AclPermission perm = AclPermission.create( 10 );
		assertThat( perm.getMask() ).isEqualTo( 1 << 10 );
		assertThat( perm.getPattern() ).isEqualTo( ".....................*.........." );

		perm = AclPermission.create( 31 );
		assertThat( perm.getMask() ).isEqualTo( 1 << 31 );
		assertThat( perm.getPattern() ).isEqualTo( "*..............................." );
	}

	@Test
	public void createPermissionWithCode() {
		AclPermission perm = AclPermission.create( 13, 'Y' );
		assertThat( perm.getMask() ).isEqualTo( 1 << 13 );
		assertThat( perm.getPattern() ).isEqualTo( "..................Y............." );
	}

	@Test
	public void creatingPermissionDoesNotAllowLessThan10OrGreaterThan32() {
		assertThatExceptionOfType( IllegalArgumentException.class )
				.isThrownBy( () -> AclPermission.create( 9 ) );

		assertThatExceptionOfType( IllegalArgumentException.class )
				.isThrownBy( () -> AclPermission.create( 32 ) );
	}
}
