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
package com.foreach.across.modules.entity.it;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistries;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.meta.PropertyPersistenceMetadata;
import com.foreach.across.modules.entity.testmodules.springdata.SpringDataJpaModule;
import com.foreach.across.modules.entity.testmodules.springdata.business.Client;
import com.foreach.across.modules.entity.testmodules.springdata.business.ClientGroup;
import com.foreach.across.modules.entity.testmodules.springdata.business.Company;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.test.AcrossTestWebConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@WebAppConfiguration
@ContextConfiguration(classes = TestEmbeddedEntities.Config.class)
public class TestEmbeddedEntities
{
	@Autowired
	private EntityPropertyRegistries entityPropertyRegistries;

	@Test
	public void persistenceMetadataShouldBeSet() {
		EntityPropertyRegistry registry = entityPropertyRegistries.getRegistry( Company.class );
		assertTrue( PropertyPersistenceMetadata.isEmbeddedProperty( registry.getProperty( "address" ) ) );
		assertFalse( PropertyPersistenceMetadata.isEmbeddedProperty( registry.getProperty( "group" ) ) );
		assertFalse( PropertyPersistenceMetadata.isEmbeddedProperty( registry.getProperty( "class" ) ) );

		registry = entityPropertyRegistries.getRegistry( ClientGroup.class );
		assertTrue( PropertyPersistenceMetadata.isEmbeddedProperty( registry.getProperty( "id" ) ) );

		registry = entityPropertyRegistries.getRegistry( Client.class );
		assertTrue( PropertyPersistenceMetadata.isEmbeddedProperty( registry.getProperty( "aliases" ) ) );
		assertTrue( PropertyPersistenceMetadata.isEmbeddedProperty( registry.getProperty( "phones" ) ) );
	}

	// view for: Element collection of primitive
	// view for: Element collection of custom type

	@Configuration
	@AcrossTestWebConfiguration
	protected static class Config implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			context.addModule( new SpringSecurityModule() );
			context.addModule( new AdminWebModule() );
			context.addModule( new EntityModule() );

			AcrossHibernateJpaModule hibernateModule = new AcrossHibernateJpaModule();
			hibernateModule.setHibernateProperty( "hibernate.hbm2ddl.auto", "create" );
			context.addModule( hibernateModule );

			context.addModule( new SpringDataJpaModule() );
		}
	}
}
