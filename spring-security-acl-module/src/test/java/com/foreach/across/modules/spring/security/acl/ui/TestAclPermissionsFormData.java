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

package com.foreach.across.modules.spring.security.acl.ui;

import org.junit.jupiter.api.Test;
import org.springframework.security.acls.model.Permission;

import static com.foreach.across.modules.spring.security.acl.business.AclPermission.*;
import static com.foreach.across.modules.spring.security.acl.ui.AclPermissionsForm.permissionGroup;
import static com.foreach.across.modules.spring.security.acl.ui.AclPermissionsForm.section;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class TestAclPermissionsFormData
{
	private AclPermissionsFormData data;

	@Test
	public void defaults() {
		data = new AclPermissionsFormData( AclPermissionsForm.builder().build() );
		assertThat( data.getSections() ).isEmpty();
	}

	@Test
	public void sectionWithoutGroupsIsNotAdded() {
		data = new AclPermissionsFormData( AclPermissionsForm.builder().section(
				section( "section" ).permissionGroups().build()
		).build() );
		assertThat( data.getSections() ).isEmpty();
	}

	@Test
	public void sectionWithGroupWithoutPermissionsIsNotAdded() {
		data = new AclPermissionsFormData( AclPermissionsForm.builder().section(
				section( "section" ).permissions().build()
		).build() );
		assertThat( data.getSections() ).isEmpty();
	}

	@Test
	public void duplicatePermissionsInsideSameSectionThrowsException() {
		assertThatExceptionOfType( IllegalStateException.class )
				.isThrownBy( () -> {
					new AclPermissionsFormData( AclPermissionsForm.builder().section(
							section( "section" )
									.permissionGroups(
											permissionGroup( "" ).permissions( READ, WRITE ).build(),
											permissionGroup( "" ).permissions( WRITE, DELETE ).build()
									)
									.build()
					).build() );
				} );
	}

	@Test
	public void sectionInformationCanBeQueriedSeparately() {
		AclPermissionsFormSection section = section( "section" )
				.permissionGroups(
						permissionGroup( "" ).permissions( READ, WRITE ).build(),
						permissionGroup( "" ).permissions( ADMINISTRATION, DELETE ).build()
				)
				.build();
		AclPermissionsFormSection otherSection = section( "other-section" )
				.permissions( READ, WRITE )
				.build();

		data = new AclPermissionsFormData( AclPermissionsForm.builder().section( section ).section( otherSection ).build() );

		assertThat( data.getSections() )
				.containsExactly( section, otherSection );

		assertThat( data.getPermissionsForSection( section ) )
				.containsExactly( READ, WRITE, ADMINISTRATION, DELETE );

		assertThat( data.getPermissionsForSection( otherSection ) )
				.containsExactly( READ, WRITE );

		assertThat( data.getPermissionGroupsForSection( section ) )
				.hasSize( 2 )
				.satisfies( map -> {
					assertThat( map.keySet() )
							.containsExactly( section.getPermissionGroupsSupplier().get()[0], section.getPermissionGroupsSupplier().get()[1] );
					assertThat( map.values() )
							.containsExactly(
									new Permission[] { READ, WRITE },
									new Permission[] { ADMINISTRATION, DELETE }
							);
				} );

		assertThat( data.getPermissionGroupsForSection( otherSection ) )
				.hasSize( 1 );
	}
}
