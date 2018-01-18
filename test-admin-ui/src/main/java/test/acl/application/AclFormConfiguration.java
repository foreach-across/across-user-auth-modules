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

package test.acl.application;

import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.spring.security.acl.business.AclSecurityEntity;
import com.foreach.across.modules.spring.security.acl.ui.AclPermissionsForm;
import com.foreach.across.modules.spring.security.acl.ui.AclPermissionsFormRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import test.acl.application.domain.customer.Customer;
import test.acl.application.domain.group.Group;

/**
 * Sample configuration building ACL form profiles and registering to corresponding entities.
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
@Configuration
class AclFormConfiguration implements EntityConfigurer
{
	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		entities.withType( Customer.class )
		        .attribute( AclPermissionsFormRegistry.ATTR_ACL_PROFILE, "with-anonymous" );

		entities.withType( Group.class )
		        .attribute( AclPermissionsFormRegistry.ATTR_ACL_PROFILE, "group-user" );
		entities.withType( AclSecurityEntity.class )
		        .attribute( AclPermissionsFormRegistry.ATTR_ACL_PROFILE, "group-user" );
	}

	@Autowired
	void registerAclForms( AclPermissionsFormRegistry formRegistry ) {
		formRegistry.put(
				"group-user",
				AclPermissionsForm.builder()
				                  .build()
		);

		formRegistry.put(
				"with-anonymous",
				AclPermissionsForm.builder()
				                  .menuPath( "/advanced-options/aclPermissions" )
				                  .build()
		);
	}
}
