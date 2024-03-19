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
package com.foreach.across.modules.it.user;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.query.EntityQueryCondition;
import com.foreach.across.modules.entity.query.EntityQueryExecutor;
import com.foreach.across.modules.entity.query.EntityQueryOps;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.user.business.*;
import com.foreach.across.modules.user.services.GroupService;
import com.foreach.across.modules.user.services.UserService;
import com.foreach.across.modules.web.AcrossWebModule;
import com.foreach.across.test.AcrossTestConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test verifies that QueryDSL is used by default for building Entity queries.  Favour QueryDSL
 * instead of JPA due to Hibernate integration bugs: see https://hibernate.atlassian.net/browse/HHH-5948.
 */
@EnableWebSecurity
@ExtendWith(SpringExtension.class)
@DirtiesContext
@WebAppConfiguration
@ContextConfiguration(classes = { ITUserModule.Config.class, ITEntityModuleDependency.Config.class })
public class ITEntityModuleDependency
{
	@Autowired
	private UserService userService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private EntityRegistry entityRegistry;

	@Test
	public void basicSecurityPrincipalShouldBeHiddenBecauseItsAbstract() {
		assertTrue( entityRegistry.getEntityConfiguration( BasicSecurityPrincipal.class ).isHidden() );
	}

	@Test
	public void permissionShouldBeHiddenExplicitly() {
		assertTrue( entityRegistry.getEntityConfiguration( Permission.class ).isHidden() );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void entityQueryExecutor() {
		Group group = createTestUserAndGroup();

		Collection<User> users = userService.findAll( QUser.user.groups.contains( group ) );
		assertEquals( 1, users.size() );
		assertEquals( Long.valueOf( -9875L ), users.iterator().next().getId() );

		EntityConfiguration entityConfiguration = entityRegistry.getEntityConfiguration( Group.class );
		EntityAssociation association = entityConfiguration.association( "user.groups" );
		assertNotNull( association );

		EntityQueryExecutor queryPageFetcher = association.getTargetEntityConfiguration()
		                                                  .getAttribute( EntityQueryExecutor.class );
		assertNotNull( queryPageFetcher );

		EntityQuery query = EntityQuery.and(
				new EntityQueryCondition( "groups", EntityQueryOps.CONTAINS, group )
		);

		users = queryPageFetcher.findAll( query );

		assertEquals( 1, users.size() );
		assertEquals( Long.valueOf( -9875L ), users.iterator().next().getId() );
	}

	private Group createTestUserAndGroup() {
		Group group = new Group();
		group.setNewEntityId( -9876L );
		group.setName( "Test group" );

		groupService.save( group );

		User user = new User();
		user.setNewEntityId( -9875L );
		user.setUsername( "uag" );
		user.setEmail( "uag@localhost" );
		user.setFirstName( "Test" );
		user.setLastName( "User" );

		user.setPassword( "pwd" );
		user.addGroup( group );

		userService.save( user );

		return group;
	}

	@Configuration
	@AcrossTestConfiguration(modules = { AcrossWebModule.NAME })
	static class Config implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			context.addModule( new AdminWebModule() );
			context.addModule( new EntityModule() );
		}
	}
}
