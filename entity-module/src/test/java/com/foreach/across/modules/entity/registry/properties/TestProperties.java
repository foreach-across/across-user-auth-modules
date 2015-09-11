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
package com.foreach.across.modules.entity.registry.properties;

import com.foreach.across.modules.entity.registry.builders.EntityPropertyRegistryBuilder;
import com.foreach.across.modules.entity.views.support.SpelValueFetcher;
import com.foreach.common.test.MockedLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockedLoader.class, classes = { TestProperties.Config.class })
public class TestProperties
{
	@Autowired
	private EntityPropertyRegistryFactoryImpl entityPropertyRegistryFactory;

	@Test
	public void propertiesAreDetected() {
		EntityPropertyRegistry registry = entityPropertyRegistryFactory.create( Customer.class );

		Collection<EntityPropertyDescriptor> descriptors = registry.getRegisteredDescriptors();
		assertEquals( 6, descriptors.size() );

		// Properties class, id and displayName are hidden
		descriptors = registry.getProperties();
		assertEquals( 3, descriptors.size() );

		assertTrue( registry.contains( "id" ) );
		assertTrue( registry.contains( "name" ) );
		assertTrue( registry.contains( "displayName" ) );
		assertTrue( registry.contains( "someValue" ) );
		assertTrue( registry.contains( "class" ) );
		assertTrue( registry.contains( "address" ) );
	}

	@Test
	public void defaultOrderCanBeSpecified() {
		EntityPropertyRegistry registry = entityPropertyRegistryFactory.create( Customer.class );
		registry.setDefaultOrder( "name", "displayName", "someValue", "class", "id" );

		List<EntityPropertyDescriptor> descriptors = registry.getProperties( EntityPropertyFilters.NOOP );

		assertEquals( 6, descriptors.size() );
		assertEquals( "name", descriptors.get( 0 ).getName() );
		assertEquals( "displayName", descriptors.get( 1 ).getName() );
		assertEquals( "someValue", descriptors.get( 2 ).getName() );
		assertEquals( "class", descriptors.get( 3 ).getName() );
		assertEquals( "id", descriptors.get( 4 ).getName() );
		assertEquals( "address", descriptors.get( 5 ).getName() );
	}

	@Test
	public void defaultOrderIsAccordingToDeclarationIfNotSpecified() {
		EntityPropertyRegistry registry = entityPropertyRegistryFactory.create( Customer.class );

		List<EntityPropertyDescriptor> descriptors = registry.getProperties( EntityPropertyFilters.NOOP );
		assertEquals( 6, descriptors.size() );

		assertEquals( "name", descriptors.get( 0 ).getName() );
		assertEquals( "address", descriptors.get( 1 ).getName() );
		assertEquals( "displayName", descriptors.get( 2 ).getName() );
		assertEquals( "someValue", descriptors.get( 3 ).getName() );
		assertEquals( "id", descriptors.get( 4 ).getName() );
		assertEquals( "class", descriptors.get( 5 ).getName() );
	}

	@Test
	public void customIncludeFilterKeepsTheDefaultOrder() {
		EntityPropertyRegistry registry = entityPropertyRegistryFactory.create( Customer.class );
		List<EntityPropertyDescriptor> descriptors = registry.getProperties(
				EntityPropertyFilters.include( "name", "id", "displayName" )
		);

		assertEquals( 3, descriptors.size() );
		assertEquals( "name", descriptors.get( 0 ).getName() );
		assertEquals( "displayName", descriptors.get( 1 ).getName() );
		assertEquals( "id", descriptors.get( 2 ).getName() );
	}

	@Test
	public void customExcludeFilterKeepsTheDefaultOrder() {
		EntityPropertyRegistry registry = entityPropertyRegistryFactory.create( Customer.class );
		List<EntityPropertyDescriptor> descriptors = registry.getProperties(
				EntityPropertyFilters.exclude( "id", "displayName" )
		);

		assertEquals( 4, descriptors.size() );
		assertEquals( "name", descriptors.get( 0 ).getName() );
		assertEquals( "address", descriptors.get( 1 ).getName() );
		assertEquals( "someValue", descriptors.get( 2 ).getName() );
		assertEquals( "class", descriptors.get( 3 ).getName() );
	}

	@Test
	public void defaultFilterIsApplied() {
		EntityPropertyRegistry registry = entityPropertyRegistryFactory.create( Customer.class );
		registry.setDefaultFilter( EntityPropertyFilters.exclude( "class" ) );

		List<EntityPropertyDescriptor> descriptors = registry.getProperties();
		assertEquals( 5, descriptors.size() );
		assertEquals( "name", descriptors.get( 0 ).getName() );
		assertEquals( "address", descriptors.get( 1 ).getName() );
		assertEquals( "displayName", descriptors.get( 2 ).getName() );
		assertEquals( "someValue", descriptors.get( 3 ).getName() );
		assertEquals( "id", descriptors.get( 4 ).getName() );

		descriptors = registry.getProperties( EntityPropertyFilters.exclude( "id", "displayName" ) );

		assertEquals( 4, descriptors.size() );
		assertEquals( "name", descriptors.get( 0 ).getName() );
		assertEquals( "address", descriptors.get( 1 ).getName() );
		assertEquals( "someValue", descriptors.get( 2 ).getName() );
		assertEquals( "class", descriptors.get( 3 ).getName() );
	}

	@Test
	public void filterWithCustomOrder() {
		EntityPropertyRegistry registry = entityPropertyRegistryFactory.create( Customer.class );
		List<EntityPropertyDescriptor> descriptors = registry.getProperties(
				EntityPropertyFilters.include( "name", "id", "displayName" ),
				EntityPropertyComparators.ordered( "displayName", "id", "name" )
		);

		assertEquals( 3, descriptors.size() );
		assertEquals( "displayName", descriptors.get( 0 ).getName() );
		assertEquals( "id", descriptors.get( 1 ).getName() );
		assertEquals( "name", descriptors.get( 2 ).getName() );
	}

	@Test
	public void includeNestedProperties() {
		EntityPropertyRegistry registry = entityPropertyRegistryFactory.create( Customer.class );

		List<EntityPropertyDescriptor> descriptors = registry.getProperties(
				EntityPropertyFilters.include( "name", "address.street" ),
				EntityPropertyComparators.ordered( "name", "address.street" )
		);

		assertEquals( 2, descriptors.size() );
		assertEquals( "name", descriptors.get( 0 ).getName() );
		assertEquals( "address.street", descriptors.get( 1 ).getName() );
	}

	@Test
	public void valueFetchersAreCreated() {
		EntityPropertyRegistry registry = entityPropertyRegistryFactory.create( Customer.class );

		Customer customer = new Customer();
		customer.setName( "some name" );
		customer.setSomeValue( "some value" );
		customer.setId( 123 );

		Address address = new Address();
		address.setStreet( "my street" );
		address.setNumber( 666 );

		customer.setAddress( address );

		assertEquals( "some name", fetch( registry, customer, "name" ) );
		assertEquals( "some name (123)", fetch( registry, customer, "displayName" ) );
		assertEquals( "my street", fetch( registry, customer, "address.street" ) );
		assertNull( registry.getProperty( "address.size()" ) );
	}

	@Test
	public void customPropertyAndValueFetcher() {
		MutableEntityPropertyRegistry parent = entityPropertyRegistryFactory.create( Customer.class );
		MutableEntityPropertyRegistry registry = entityPropertyRegistryFactory.createWithParent( parent );

		SimpleEntityPropertyDescriptor calculated = new SimpleEntityPropertyDescriptor( "address.size()" );
		calculated.setValueFetcher( new SpelValueFetcher( "address.size()" ) );
		registry.register( calculated );

		Customer customer = new Customer();
		customer.setName( "some name" );
		customer.setSomeValue( "some value" );
		customer.setId( 123 );

		Address address = new Address();
		address.setStreet( "my street" );
		address.setNumber( 666 );
		customer.setAddress( address );

		assertEquals( "some name", fetch( registry, customer, "name" ) );
		assertEquals( "some name (123)", fetch( registry, customer, "displayName" ) );
		assertEquals( "my street", fetch( registry, customer, "address.street" ) );
		assertEquals( 9, fetch( registry, customer, "address.size()" ) );
	}

	@Test
	public void wildcardShouldNeverReturnNested() {
		MutableEntityPropertyRegistry registry = entityPropertyRegistryFactory.create( Customer.class );
		MutableEntityPropertyDescriptor descriptor
				= (MutableEntityPropertyDescriptor) registry.getProperty( "address.street" );
		registry.register( descriptor );

		List<EntityPropertyDescriptor> descriptors = registry.select( new EntityPropertySelector( "*" ) );
		assertFalse( descriptors.contains( descriptor ) );
	}

	@SuppressWarnings("unchecked")
	private Object fetch( EntityPropertyRegistry registry, Object entity, String propertyName ) {
		return registry.getProperty( propertyName ).getValueFetcher().getValue( entity );
	}

	public static class Address
	{
		private String street;
		private int number;

		public String getStreet() {
			return street;
		}

		public void setStreet( String street ) {
			this.street = street;
		}

		public int getNumber() {
			return number;
		}

		public void setNumber( int number ) {
			this.number = number;
		}

		public int size() {
			return street.length();
		}
	}

	public abstract static class BaseCustomer
	{
		private long id;

		public long getId() {
			return id;
		}

		public void setId( long id ) {
			this.id = id;
		}
	}

	public static class Customer extends BaseCustomer
	{
		private String name;
		private Address address;
		private Object value;

		public String getName() {
			return name;
		}

		public void setName( String name ) {
			this.name = name;
		}

		public Address getAddress() {
			return address;
		}

		public void setAddress( Address address ) {
			this.address = address;
		}

		public String getDisplayName() {
			return String.format( "%s (%s)", getName(), getId() );
		}

		public void setSomeValue( String someValue ) {
			value = someValue;
		}
	}

	@Configuration
	public static class Config
	{
		@Bean
		public EntityPropertyRegistryFactoryImpl entityPopertyRegistryFactory() {
			return new EntityPropertyRegistryFactoryImpl();
		}

		@Bean
		public EntityPropertyDescriptorFactory entityPropertyDescriptorFactory() {
			return new EntityPropertyDescriptorFactoryImpl();
		}

		@Bean
		public EntityPropertyRegistryBuilder dummyBuilder() {
			return mock( EntityPropertyRegistryBuilder.class );
		}
	}
}
