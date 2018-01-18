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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.ObjectIdentity;

import java.util.Optional;

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
	private EntityViewCommand viewCommand;

	@Mock
	private AclPermissionsFormRegistry formRegistry;

	@InjectMocks
	private AclPermissionsFormViewProcessor processor;

	@Test
	public void illegalStateExceptionIfNoAclPermissionsFormConfiguration() {

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

		processor.initializeCommandObject( viewRequest, viewCommand, null );
	}

	@Test
	public void customObjectIdentityFunctionIsUsedIfRegistered() {

	}
}
