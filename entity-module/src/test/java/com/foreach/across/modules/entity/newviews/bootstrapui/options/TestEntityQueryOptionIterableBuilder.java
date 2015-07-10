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
package com.foreach.across.modules.entity.newviews.bootstrapui.options;

import com.foreach.across.modules.bootstrapui.elements.builder.OptionsFormElementBuilder.Option;
import com.foreach.across.modules.entity.newviews.EntityViewElementBuilderContext;
import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.query.EntityQueryCondition;
import com.foreach.across.modules.entity.query.EntityQueryExecutor;
import com.foreach.across.modules.entity.query.EntityQueryOps;
import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementBuilderContextImpl;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
public class TestEntityQueryOptionIterableBuilder
{
	private final Entity ONE = new Entity( "one" );
	private final Entity TWO = new Entity( "two" );
	private final Entity THREE = new Entity( "three" );

	private EntityQueryOptionIterableBuilder iterableBuilder;
	private ValueFetcher<Object> valueFetcher;
	private ViewElementBuilderContext elementBuilderContext;
	private EntityQueryExecutor entityQueryExecutor;

	private Map<String, Option> options = new HashMap<>();

	@Before
	@SuppressWarnings("unchecked")
	public void before() {
		valueFetcher = mock( ValueFetcher.class );

		EntityModel entityModel = mock( EntityModel.class );
		entityQueryExecutor = mock( EntityQueryExecutor.class );

		iterableBuilder = new EntityQueryOptionIterableBuilder();
		iterableBuilder.setEntityModel( entityModel );
		iterableBuilder.setEntityQueryExecutor( entityQueryExecutor );
		iterableBuilder.setValueFetcher( valueFetcher );

		elementBuilderContext = new ViewElementBuilderContextImpl();

		when( entityQueryExecutor.findAll( any( EntityQuery.class ) ) ).thenReturn( Arrays.asList( ONE, TWO, THREE ) );

		when( entityModel.getLabel( anyObject() ) )
				.thenAnswer( new Answer<Object>()
				{
					@Override
					public Object answer( InvocationOnMock invocation ) throws Throwable {
						return ( (Entity) invocation.getArguments()[0] ).name;
					}
				} );

		when( entityModel.getId( anyObject() ) )
				.thenAnswer( new Answer<Object>()
				{
					@Override
					public Object answer( InvocationOnMock invocation ) throws Throwable {
						return StringUtils.upperCase( ( (Entity) invocation.getArguments()[0] ).name );
					}
				} );

		options.clear();
	}

	@Test
	public void noValueFetcherMeansNoOptionSelected() {
		elementBuilderContext.setAttribute( EntityViewElementBuilderContext.ENTITY, "entity" );
		when( valueFetcher.getValue( "entity" ) ).thenReturn( TWO );

		iterableBuilder.setValueFetcher( null );

		build( true );
		assertNotSelected( ONE, TWO, THREE );
	}

	@Test
	public void noEntitySetMeansNoOptionSelected() {
		build( true );
		assertNotSelected( ONE, TWO, THREE );
	}

	@Test
	public void entityWithoutOptionSelected() {
		elementBuilderContext.setAttribute( EntityViewElementBuilderContext.ENTITY, "entity" );

		build( true );
		assertNotSelected( ONE, TWO, THREE );
	}

	@Test
	public void singleOptionSelected() {
		elementBuilderContext.setAttribute( EntityViewElementBuilderContext.ENTITY, "entity" );
		when( valueFetcher.getValue( "entity" ) ).thenReturn( TWO );

		build( true );

		assertSelected( TWO );
		assertNotSelected( ONE, THREE );
	}

	@Test
	public void multipleOptionsSelectedAsCollection() {
		elementBuilderContext.setAttribute( EntityViewElementBuilderContext.ENTITY, "entity" );
		when( valueFetcher.getValue( "entity" ) ).thenReturn( Arrays.asList( ONE, THREE ) );

		build( true );

		assertSelected( ONE, THREE );
		assertNotSelected( TWO );

		elementBuilderContext.setAttribute( EntityViewElementBuilderContext.ENTITY, "entity" );
		when( valueFetcher.getValue( "entity" ) )
				.thenReturn( new HashSet<>( Arrays.asList( TWO, THREE ) ) );

		build( true );

		assertSelected( TWO, THREE );
		assertNotSelected( ONE );
	}

	@Test
	public void multipleOptionsSelectedAsArray() {
		elementBuilderContext.setAttribute( EntityViewElementBuilderContext.ENTITY, "entity" );
		when( valueFetcher.getValue( "entity" ) ).thenReturn( new Entity[] { ONE, THREE } );

		build( true );

		assertSelected( ONE, THREE );
		assertNotSelected( TWO );
	}

	@Test
	public void differentValueReturnedFromFetcher() {
		elementBuilderContext.setAttribute( EntityViewElementBuilderContext.ENTITY, "entity" );
		when( valueFetcher.getValue( "entity" ) ).thenReturn( 123L );

		build( true );

		assertNotSelected( ONE, TWO, THREE );
	}

	@Test
	public void customEntityQuery() {
		EntityQuery query = EntityQuery.or( new EntityQueryCondition( "name", EntityQueryOps.EQ, "test" ) );
		iterableBuilder.setEntityQuery( query );

		build( true );

		verify( entityQueryExecutor ).findAll( query );
	}

	@Test
	public void selfOptionNotIncluded() {
		elementBuilderContext.setAttribute( EntityViewElementBuilderContext.ENTITY, TWO );
		when( valueFetcher.getValue( TWO ) ).thenReturn( new Entity[] { ONE } );

		build( false );

		assertEquals( 2, options.size() );
		assertFalse( options.containsKey( TWO.name ) );

		assertSelected( ONE );
		assertNotSelected( THREE );
	}

	@Test
	public void selfOptionIncluded() {
		elementBuilderContext.setAttribute( EntityViewElementBuilderContext.ENTITY, TWO );
		when( valueFetcher.getValue( TWO ) ).thenReturn( new Entity[] { ONE } );

		iterableBuilder.setSelfOptionIncluded( true );

		build( true );

		assertSelected( ONE );
		assertNotSelected( TWO, THREE );
	}

	private void assertNotSelected( Entity... entities ) {
		for ( Entity entity : entities ) {
			assertFalse( options.get( entity.name ).isSelected() );
		}
	}

	private void assertSelected( Entity... entities ) {
		for ( Entity entity : entities ) {
			assertTrue( options.get( entity.name ).isSelected() );
		}
	}

	private void build( boolean verify ) {
		options.clear();

		Iterable<Option> iterable = iterableBuilder.buildOptions( elementBuilderContext );

		List<Option> optionsInOrder = new ArrayList<>( 3 );

		for ( Option option : iterable ) {
			optionsInOrder.add( option );
			options.put( option.getLabel(), option );
		}

		if ( verify ) {
			assertEquals( 3, optionsInOrder.size() );

			assertEquals( ONE.name, optionsInOrder.get( 0 ).getLabel() );
			assertEquals( StringUtils.upperCase( ONE.name ), optionsInOrder.get( 0 ).getValue() );

			assertEquals( TWO.name, optionsInOrder.get( 1 ).getLabel() );
			assertEquals( StringUtils.upperCase( TWO.name ), optionsInOrder.get( 1 ).getValue() );

			assertEquals( THREE.name, optionsInOrder.get( 2 ).getLabel() );
			assertEquals( StringUtils.upperCase( THREE.name ), optionsInOrder.get( 2 ).getValue() );
		}
	}

	static class Entity
	{
		private String name;

		public Entity( String name ) {
			this.name = name;
		}
	}
}
