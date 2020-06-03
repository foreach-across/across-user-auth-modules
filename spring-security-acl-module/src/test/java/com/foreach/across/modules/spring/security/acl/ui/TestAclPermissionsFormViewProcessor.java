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

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.spring.security.acl.business.AclSecurityEntity;
import com.foreach.across.modules.spring.security.acl.services.AclOperations;
import com.foreach.across.modules.spring.security.acl.services.AclSecurityService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.ObjectIdentity;

import java.util.Optional;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestAclPermissionsFormViewProcessor
{
	private final AclSecurityEntity ENTITY = AclSecurityEntity.builder().id( 123L ).build();
	private final ObjectIdentity ENTITY_OID = new ObjectIdentityImpl( ENTITY );

	@Mock
	private EntityViewRequest viewRequest;

	@Mock
	private MutableAcl acl;

	@Mock
	private AclOperations aclOperations;

	@Mock
	private AclPermissionsFormRegistry formRegistry;

	@Mock
	private AclSecurityService aclSecurityService;

	@InjectMocks
	private AclPermissionsFormViewProcessor processor;

	private EntityViewCommand viewCommand = new EntityViewCommand();

	@Test
	public void illegalStateExceptionIfNoAclPermissionsFormConfiguration() {
		EntityConfiguration entityConfiguration = mock( EntityConfiguration.class );
		EntityViewContext viewContext = mock( EntityViewContext.class );
		when( viewContext.getEntityConfiguration() ).thenReturn( entityConfiguration );
		when( viewRequest.getEntityViewContext() ).thenReturn( viewContext );

		when( formRegistry.getForEntityConfiguration( entityConfiguration ) ).thenReturn( Optional.empty() );

		assertThatExceptionOfType( IllegalStateException.class )
				.isThrownBy( () -> processor.initializeCommandObject( viewRequest, viewCommand, null ) );
	}

	@Test
	public void controllerGetsCreatedWithRightSettings() {
		EntityConfiguration entityConfiguration = mock( EntityConfiguration.class );
		EntityViewContext viewContext = mock( EntityViewContext.class );
		when( viewContext.getEntityConfiguration() ).thenReturn( entityConfiguration );
		when( viewContext.getEntity() ).thenReturn( ENTITY );
		when( viewRequest.getEntityViewContext() ).thenReturn( viewContext );

		AclPermissionsForm form = AclPermissionsForm.builder().build();
		when( formRegistry.getForEntityConfiguration( entityConfiguration ) ).thenReturn( Optional.of( form ) );

		when( aclSecurityService.getAcl( ENTITY_OID ) ).thenReturn( acl );
		when( aclSecurityService.createAclOperations( acl ) ).thenReturn( aclOperations );

		processor.initializeCommandObject( viewRequest, viewCommand, null );

		AclPermissionsFormController controller = viewCommand.getExtension( AclPermissionsFormViewProcessor.CONTROLLER_EXTENSION );
		assertThat( controller ).isNotNull();
		assertThat( controller.getFormData().getPermissionsForm() ).isEqualTo( form );
		assertThat( controller.getAclOperations() ).isEqualTo( aclOperations );
	}

	@Test
	public void customObjectIdentityFunctionIsUsedIfRegistered() {
		EntityConfiguration entityConfiguration = mock( EntityConfiguration.class );
		EntityViewContext viewContext = mock( EntityViewContext.class );
		when( viewContext.getEntityConfiguration() ).thenReturn( entityConfiguration );
		when( viewContext.getEntity() ).thenReturn( ENTITY );
		when( viewRequest.getEntityViewContext() ).thenReturn( viewContext );

		ObjectIdentity customIdentity = mock( ObjectIdentity.class );
		Function<Object, ObjectIdentity> identityResolver = entity -> customIdentity;

		when( entityConfiguration.getAttribute( ObjectIdentity.class.getName(), Function.class ) ).thenReturn( identityResolver );

		AclPermissionsForm form = AclPermissionsForm.builder().build();
		when( formRegistry.getForEntityConfiguration( entityConfiguration ) ).thenReturn( Optional.of( form ) );

		when( aclSecurityService.getAcl( customIdentity ) ).thenReturn( acl );
		when( aclSecurityService.createAclOperations( acl ) ).thenReturn( aclOperations );

		processor.initializeCommandObject( viewRequest, viewCommand, null );

		AclPermissionsFormController controller = viewCommand.getExtension( AclPermissionsFormViewProcessor.CONTROLLER_EXTENSION );
		assertThat( controller ).isNotNull();
		assertThat( controller.getFormData().getPermissionsForm() ).isEqualTo( form );
		assertThat( controller.getAclOperations() ).isEqualTo( aclOperations );
	}
}
