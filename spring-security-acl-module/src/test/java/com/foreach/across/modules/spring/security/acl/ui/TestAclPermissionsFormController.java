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

import com.foreach.across.modules.spring.security.acl.business.AclPermission;
import com.foreach.across.modules.spring.security.acl.services.AclOperations;
import com.foreach.across.modules.spring.security.acl.ui.AclPermissionsFormController.ModelEntry;
import lombok.val;
import org.junit.Test;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.Permission;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
public class TestAclPermissionsFormController
{
	@Test
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public void applyModel() {
		MutableAcl mutableAcl = mock( MutableAcl.class );
		AclOperations operations = mock( AclOperations.class );
		when( operations.getAcl() ).thenReturn( mutableAcl );

		AclPermissionsForm form = AclPermissionsForm
				.builder()
				.section(
						AclPermissionsForm.section( "users" )
						                  .permissions( AclPermission.READ, AclPermission.WRITE, AclPermission.ADMINISTRATION )
						                  .objectForTransportIdResolver( transportId -> "user-" + transportId )
						                  .sidForObjectResolver( entity -> new PrincipalSid( entity.toString() ) )
						                  .sidMatcher( ( sid, object ) -> !"user-john".equals( object ) )
						                  .build()
				)
				.section(
						AclPermissionsForm.section( "groups" )
						                  .permissions( AclPermission.READ, AclPermission.WRITE )
						                  .objectForTransportIdResolver( transportId -> "group-" + transportId )
						                  .sidForObjectResolver( entity -> new PrincipalSid( entity.toString() ) )
						                  .sidMatcher( ( sid, object ) -> true )
						                  .build()
				)
				.build();

		val controller = new AclPermissionsFormController( operations, new AclPermissionsFormData( form ) );
		controller.getModel().put( "1", entry( "users", "admin", AclPermission.ADMINISTRATION, AclPermission.WRITE ) );
		controller.getModel().put( "2", entry( "groups", "admin", AclPermission.READ, AclPermission.DELETE ) );
		controller.getModel().put( "3", entry( "unknown-section", "admin", AclPermission.READ, AclPermission.DELETE ) );
		controller.getModel().put( "4", entry( "user", "john", AclPermission.READ, AclPermission.DELETE ) );

		Acl acl = controller.updateAclWithModel();
		assertThat( acl ).isEqualTo( mutableAcl );

		verify( operations ).getAcl();
		verify( operations ).apply(
				new PrincipalSid( "user-admin" ),
				new Permission[] { AclPermission.READ, AclPermission.WRITE, AclPermission.ADMINISTRATION },
				new int[] { AclPermission.ADMINISTRATION.getMask(), AclPermission.WRITE.getMask() }
		);
		verify( operations ).apply(
				new PrincipalSid( "group-admin" ),
				new Permission[] { AclPermission.READ, AclPermission.WRITE },
				new int[] { AclPermission.READ.getMask(), AclPermission.DELETE.getMask() }
		);
		verifyNoMoreInteractions( operations );
	}

	private ModelEntry entry( String section, String id, Permission... permissions ) {
		ModelEntry entry = new ModelEntry();
		entry.setSection( section );
		entry.setId( id );
		entry.setPermissions(
				Stream.of( permissions )
				      .map( Permission::getMask )
				      .toArray( Integer[]::new )
		);

		return entry;
	}
}
