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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
@ExtendWith(MockitoExtension.class)
public class TestAclPermissionsFormRegistry
{
	@Mock
	private EntityConfiguration entityConfiguration;

	private AclPermissionsFormRegistry formRegistry = new AclPermissionsFormRegistry();

	@Test
	public void noFormIfEntityConfigurationDoesNotHaveAttribute() {
		assertThat( formRegistry.getForEntityConfiguration( entityConfiguration ) ).isEmpty();
	}

	@Test
	public void noFormIfNotAvailableUnderThatName() {
		when( entityConfiguration.getAttribute( AclPermissionsFormRegistry.ATTR_ACL_PROFILE, String.class ) ).thenReturn( "my-form" );
		assertThat( formRegistry.getForEntityConfiguration( entityConfiguration ) ).isEmpty();
	}

	@Test
	public void formRegisteredOnEntityConfigurationAttributeIsReturned() {
		AclPermissionsForm myForm = mock( AclPermissionsForm.class );
		formRegistry.put( "my-form", myForm );
		when( entityConfiguration.getAttribute( AclPermissionsFormRegistry.ATTR_ACL_PROFILE, String.class ) ).thenReturn( "my-form" );
		assertThat( formRegistry.getForEntityConfiguration( entityConfiguration ) ).hasValue( myForm );
	}
}
