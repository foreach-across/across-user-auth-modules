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
import com.foreach.across.modules.bootstrapui.elements.FieldsetFormElement;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.newviews.EntityViewElementBuilderService;
import com.foreach.across.modules.entity.newviews.ViewElementMode;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistryFactory;
import com.foreach.across.modules.entity.registry.properties.meta.PropertyPersistenceMetadata;
import com.foreach.across.modules.entity.testmodules.springdata.SpringDataJpaModule;
import com.foreach.across.modules.entity.testmodules.springdata.business.Client;
import com.foreach.across.modules.entity.testmodules.springdata.business.ClientGroup;
import com.foreach.across.modules.entity.testmodules.springdata.business.Company;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.modules.web.ui.ViewElementBuilderContextImpl;
import com.foreach.across.test.support.AbstractViewElementTemplateTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.*;

/**
 * @author Arne Vandamme
 */
@ContextConfiguration(classes = TestEmbeddedEntities.Config.class)
public class TestEmbeddedEntities extends AbstractViewElementTemplateTest
{
	@Autowired
	private EntityRegistry entityRegistry;

	@Autowired
	private EntityPropertyRegistryFactory entityPropertyRegistryFactory;

	@Autowired
	private EntityViewElementBuilderService viewElementBuilderService;

	@Test
	public void persistenceMetadataShouldBeSet() {
		EntityPropertyRegistry registry = entityPropertyRegistryFactory.getOrCreate( Company.class );
		assertTrue( PropertyPersistenceMetadata.isEmbeddedProperty( registry.getProperty( "address" ) ) );
		assertFalse( PropertyPersistenceMetadata.isEmbeddedProperty( registry.getProperty( "group" ) ) );
		assertFalse( PropertyPersistenceMetadata.isEmbeddedProperty( registry.getProperty( "class" ) ) );

		registry = entityPropertyRegistryFactory.getOrCreate( ClientGroup.class );
		assertTrue( PropertyPersistenceMetadata.isEmbeddedProperty( registry.getProperty( "id" ) ) );

		registry = entityPropertyRegistryFactory.getOrCreate( Client.class );
		assertTrue( PropertyPersistenceMetadata.isEmbeddedProperty( registry.getProperty( "phones" ) ) );

	}

	@Test
	public void primitiveTypesShouldBehaveAsNonEmbedded() {
		EntityPropertyRegistry registry = entityPropertyRegistryFactory.getOrCreate( Client.class );
		assertFalse( PropertyPersistenceMetadata.isEmbeddedProperty( registry.getProperty( "aliases" ) ) );
	}

	@Test
	public void fieldsetForAddress() {
		EntityConfiguration entityConfiguration = entityRegistry.getEntityConfiguration( Company.class );
		EntityPropertyDescriptor address = entityConfiguration.getPropertyRegistry().getProperty( "address" );

		FieldsetFormElement fieldset = (FieldsetFormElement) viewElementBuilderService
				.getElementBuilder( entityConfiguration, address, ViewElementMode.FORM_WRITE )
				.build( new ViewElementBuilderContextImpl() );

		assertNotNull( fieldset );

		renderAndExpect(
				fieldset,
				"<fieldset name='address'>" +
						"<legend>Address</legend>" +
						"<div class='form-group'>" +
						"<label for='entity.address.street' class='control-label'>Street</label>" +
						"<input type='text' class='form-control' name='entity.address.street' id='entity.address.street' maxlength='100' />" +
						"</div>" +
						"<div class='form-group'>" +
						"<label for='entity.address.zipCode' class='control-label'>Zip code</label>" +
						"<input type='text' class='form-control' name='entity.address.zipCode' id='entity.address.zipCode' />" +
						"</div>" +
						"</fieldset>"
		);
	}

	// view for: Element collection of primitive
	// view for: Element collection of custom type

	@Configuration
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
