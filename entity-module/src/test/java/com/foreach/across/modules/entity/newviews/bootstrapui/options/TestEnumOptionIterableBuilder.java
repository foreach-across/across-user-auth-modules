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
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.util.EntityUtils;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementBuilderContextImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 */
public class TestEnumOptionIterableBuilder
{
	enum Counter
	{
		ONE,
		TWO,
		THREE
	}

	private EnumOptionIterableBuilder iterableBuilder;
	private ValueFetcher<String> valueFetcher;
	private ViewElementBuilderContext elementBuilderContext;

	private Map<Counter, Option> options = new HashMap<>();

	@Before
	@SuppressWarnings("unchecked")
	public void before() {
		valueFetcher = mock( ValueFetcher.class );

		iterableBuilder = new EnumOptionIterableBuilder( Counter.class, valueFetcher );
		elementBuilderContext = new ViewElementBuilderContextImpl();

		EntityMessageCodeResolver codeResolver = mock( EntityMessageCodeResolver.class );
		when( codeResolver.getMessageWithFallback( anyString(), anyString() ) )
				.thenAnswer( new Answer<Object>()
				{
					@Override
					public Object answer( InvocationOnMock invocationOnMock ) throws Throwable {
						return invocationOnMock.getArguments()[1];
					}
				} );

		elementBuilderContext.setAttribute( EntityMessageCodeResolver.class, codeResolver );

		options.clear();
	}

	@Test
	public void noEntitySetMeansNoOptionSelected() {
		build();
		assertNotSelected( Counter.ONE, Counter.TWO, Counter.THREE );
	}

	@Test
	public void entityWithoutOptionSelected() {
		elementBuilderContext.setAttribute( EntityViewElementBuilderContext.ENTITY, "entity" );

		build();
		assertNotSelected( Counter.ONE, Counter.TWO, Counter.THREE );
	}

	@Test
	public void singleOptionSelected() {
		elementBuilderContext.setAttribute( EntityViewElementBuilderContext.ENTITY, "entity" );
		when( valueFetcher.getValue( "entity" ) ).thenReturn( Counter.TWO );

		build();

		assertSelected( Counter.TWO );
		assertNotSelected( Counter.ONE, Counter.THREE );
	}

	@Test
	public void multipleOptionsSelectedAsCollection() {
		elementBuilderContext.setAttribute( EntityViewElementBuilderContext.ENTITY, "entity" );
		when( valueFetcher.getValue( "entity" ) ).thenReturn( Arrays.asList( Counter.ONE, Counter.THREE ) );

		build();

		assertSelected( Counter.ONE, Counter.THREE );
		assertNotSelected( Counter.TWO );

		elementBuilderContext.setAttribute( EntityViewElementBuilderContext.ENTITY, "entity" );
		when( valueFetcher.getValue( "entity" ) )
				.thenReturn( new HashSet<>( Arrays.asList( Counter.TWO, Counter.THREE ) ) );

		build();

		assertSelected( Counter.TWO, Counter.THREE );
		assertNotSelected( Counter.ONE );
	}

	@Test
	public void multipleOptionsSelectedAsArray() {
		elementBuilderContext.setAttribute( EntityViewElementBuilderContext.ENTITY, "entity" );
		when( valueFetcher.getValue( "entity" ) ).thenReturn( new Counter[] { Counter.ONE, Counter.THREE } );

		build();

		assertSelected( Counter.ONE, Counter.THREE );
		assertNotSelected( Counter.TWO );
	}

	@Test
	public void differentValueReturnedFromFetcher() {
		elementBuilderContext.setAttribute( EntityViewElementBuilderContext.ENTITY, "entity" );
		when( valueFetcher.getValue( "entity" ) ).thenReturn( 123L );

		build();

		assertNotSelected( Counter.ONE, Counter.TWO, Counter.THREE );
	}

	private void assertNotSelected( Counter... counters ) {
		for ( Counter counter : counters ) {
			assertFalse( options.get( counter ).isSelected() );
		}
	}

	private void assertSelected( Counter... counters ) {
		for ( Counter counter : counters ) {
			assertTrue( options.get( counter ).isSelected() );
		}
	}

	private void build() {
		options.clear();

		Iterable<Option> iterable = iterableBuilder.buildOptions( elementBuilderContext );

		List<Option> optionsInOrder = new ArrayList<>( 3 );

		for ( Option option : iterable ) {
			optionsInOrder.add( option );
			options.put( Counter.valueOf( (String) option.getValue() ), option );
		}

		assertEquals( 3, optionsInOrder.size() );

		assertEquals( EntityUtils.generateDisplayName( Counter.ONE.name() ), optionsInOrder.get( 0 ).getLabel() );
		assertEquals( Counter.ONE.name(), optionsInOrder.get( 0 ).getValue() );

		assertEquals( EntityUtils.generateDisplayName( Counter.TWO.name() ), optionsInOrder.get( 1 ).getLabel() );
		assertEquals( Counter.TWO.name(), optionsInOrder.get( 1 ).getValue() );

		assertEquals( EntityUtils.generateDisplayName( Counter.THREE.name() ), optionsInOrder.get( 2 ).getLabel() );
		assertEquals( Counter.THREE.name(), optionsInOrder.get( 2 ).getValue() );
	}
}
