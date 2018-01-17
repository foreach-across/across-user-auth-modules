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

package test.acl.application.installers;

import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.annotations.InstallerMethod;
import com.foreach.across.core.installers.InstallerPhase;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import test.acl.application.domain.customer.Customer;
import test.acl.application.domain.customer.CustomerRepository;
import test.acl.application.domain.group.Group;
import test.acl.application.domain.group.GroupRepository;
import test.acl.application.domain.user.User;
import test.acl.application.domain.user.UserRepository;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
@Installer(description = "Install test entities", phase = InstallerPhase.AfterContextBootstrap, version = 1)
@RequiredArgsConstructor
public class TestDataInstaller
{
	private final UserRepository userRepository;
	private final GroupRepository groupRepository;
	private final CustomerRepository customerRepository;

	@Order(1)
	@InstallerMethod
	public void createUsers() {
		userRepository.save( User.builder().name( "John Doe" ).build() );
		userRepository.save( User.builder().name( "Jane Doe" ).build() );
		userRepository.save( User.builder().name( "Chuck Norris" ).build() );
	}

	@Order(2)
	@InstallerMethod
	public void createGroups() {
		groupRepository.save( Group.builder().name( "Doe Family" ).build() );
		groupRepository.save( Group.builder().name( "Movie Stars" ).build() );
	}

	@Order(3)
	@InstallerMethod
	public void createCustomers() {
		customerRepository.save( Customer.builder().name( "Foreach" ).build() );
		customerRepository.save( Customer.builder().name( "Google" ).build() );
	}
}
