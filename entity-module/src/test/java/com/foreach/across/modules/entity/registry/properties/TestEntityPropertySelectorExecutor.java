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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 */
public class TestEntityPropertySelectorExecutor
{
	private EntityPropertyRegistry propertyRegistry, productRegistry;
	private EntityPropertySelectorExecutor executor;
	private EntityPropertySelector selector;

	private List<EntityPropertyDescriptor> result;

	private EntityPropertyDescriptor id;
	private EntityPropertyDescriptor displayName;
	private EntityPropertyDescriptor name;

	@Before
	public void setUp() throws Exception {
		EntityPropertyRegistries registries = new EntityPropertyRegistries();
		propertyRegistry = mock( EntityPropertyRegistry.class );
		productRegistry = mock( EntityPropertyRegistry.class );

		executor = new EntityPropertySelectorExecutor( propertyRegistry, registries );
		selector = new EntityPropertySelector();

		result = null;

		id = property( "id" );
		displayName = property( "displayName" );
		name = property( "name" );
		EntityPropertyDescriptor product = property( "product" );
		Mockito.<Class<?>>when( product.getPropertyType() ).thenReturn( Long.class );
		registries.add( Long.class, productRegistry );

		property( "product.id" );
		property( "product.title" );
		property( "product.date" );
	}

	private EntityPropertyDescriptor property( String name ) {
		EntityPropertyDescriptor property = mock( EntityPropertyDescriptor.class );
		when( property.getName() ).thenReturn( name );

		when( propertyRegistry.getProperty( name ) ).thenReturn( property );

		return property;
	}

	@Test
	public void emptyResults() {
		select();
		assertTrue( result.isEmpty() );
	}

	@Test
	public void simpleProperties() {
		selector.configure( "id", "displayName" );
		select();

		assertResult( "id", "displayName" );
	}

	@Test
	public void simpleExclude() {
		selector.configure( "id", "~displayName" );
		select();

		assertResult( "id" );
	}

	@Test
	public void allWithDefaultFilter() {
		when( propertyRegistry.getProperties() ).thenReturn( Arrays.asList( displayName, name ) );

		selector.configure( "*" );
		select();

		assertResult( "displayName", "name" );
	}

	@Test
	public void allWithAdditionalAndExclude() {
		when( propertyRegistry.getProperties() ).thenReturn( Arrays.asList( displayName, name ) );

		selector.configure( "*", "id", "~displayName" );
		select();

		assertResult( "name", "id" );
	}

	@Test
	public void allRegistered() {
		when( propertyRegistry.getRegisteredDescriptors() ).thenReturn( Arrays.asList( id, displayName, name ) );

		selector.configure( "**" );
		select();

		assertResult( "id", "displayName", "name" );
	}

	@Test
	public void allRegisteredWithAdditionalAndExclude() {
		when( propertyRegistry.getRegisteredDescriptors() ).thenReturn( Arrays.asList( id, displayName, name ) );

		selector.configure( "product.title", "**", "~id" );
		select();

		assertResult( "product.title", "displayName", "name" );
	}

	@Test
	public void allFromNested() {
		EntityPropertySelector subSelector = new EntityPropertySelector( "*" );
		List<EntityPropertyDescriptor> descriptors
				= Arrays.asList( nestedProperty( "id" ), nestedProperty( "title" ), nestedProperty( "date" ) );

		when( productRegistry.select( subSelector ) ).thenReturn( descriptors );

		selector.configure( "id", "product.*", "~product.date" );
		select();

		assertResult( "id", "product.id", "product.title" );
	}

	private EntityPropertyDescriptor nestedProperty( String name ) {
		EntityPropertyDescriptor property = mock( EntityPropertyDescriptor.class );
		when( property.getName() ).thenReturn( name );

		return property;
	}

	@Test
	public void registeredFromNested() {
		EntityPropertySelector subSelector = new EntityPropertySelector( "**" );
		List<EntityPropertyDescriptor> descriptors
				= Arrays.asList( nestedProperty( "id" ), nestedProperty( "title" ), nestedProperty( "date" ) );

		when( productRegistry.select( subSelector ) ).thenReturn( descriptors );

		selector.configure( "id", "product.**", "~product.date" );
		select();

		assertResult( "id", "product.id", "product.title" );
	}

	@Test
	public void allRegisteredWithAdditionalFilter() {
		when( propertyRegistry.getRegisteredDescriptors() ).thenReturn( Arrays.asList( id, displayName, name ) );

		EntityPropertyFilter filter = EntityPropertyFilters.exclude( "displayName" );

		selector.setFilter( filter );
		selector.configure( "product.title", "**", "~id" );
		select();

		assertResult( "product.title", "name" );
	}

	private void select() {
		result = executor.select( selector );
	}

	private void assertResult( String... expected ) {
		assertNotNull( expected );
		assertEquals( expected.length, result.size() );

		for ( int i = 0; i < expected.length; i++ ) {
			assertEquals( expected[i], result.get( i ).getName() );
		}
	}
}
