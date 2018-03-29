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

package test;

import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.spring.security.acl.business.AclSecurityEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import test.acl.AdminUiApplication;
import test.acl.application.domain.customer.Customer;
import test.acl.application.domain.group.Group;
import test.acl.application.domain.user.User;

import static com.foreach.across.modules.spring.security.acl.ui.AclPermissionsForm.VIEW_NAME;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.MOCK,
		classes = AdminUiApplication.class,
		properties = {
				"acrossHibernate.generate-ddl=true",
				"spring.datasource.url=jdbc:h2:mem:test-admin-ui"
		}
)
public class TestAdminUiApplication
{
	@Autowired
	private EntityRegistry entityRegistry;

	@Test
	public void aclPermissionViewsShouldBeRegistered() {
		assertThat( entityRegistry.getEntityConfiguration( Customer.class ).hasView( VIEW_NAME ) ).isTrue();
		assertThat( entityRegistry.getEntityConfiguration( Group.class ).hasView( VIEW_NAME ) ).isTrue();
		assertThat( entityRegistry.getEntityConfiguration( AclSecurityEntity.class ).hasView( VIEW_NAME ) ).isTrue();
		assertThat( entityRegistry.getEntityConfiguration( User.class ).hasView( VIEW_NAME ) ).isFalse();
	}
}
