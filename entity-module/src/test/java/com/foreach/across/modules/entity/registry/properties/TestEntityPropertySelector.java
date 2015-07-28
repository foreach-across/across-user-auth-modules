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

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author Arne Vandamme
 */
public class TestEntityPropertySelector
{
	private EntityPropertySelector selector;

	@Before
	public void setUp() throws Exception {
		selector = new EntityPropertySelector();
	}

	@Test
	public void simpleProperties() {
		selector.configure( "one" );
		selector.configure( "id", "name", "product.title" );

		Map<String, Boolean> expected = new LinkedHashMap<>();
		expected.put( "id", true );
		expected.put( "name", true );
		expected.put( "product.title", true );

		assertEquals( expected, selector.propertiesToSelect() );
		assertArrayEquals( new String[] { "id", "name", "product.title" },
		                   selector.propertiesToSelect().keySet().toArray( new String[3] ) );
	}

	@Test
	public void incrementalBuild() {
		selector.configure( "id" );
		selector.configure( ".", "name", "product.title" );

		Map<String, Boolean> expected = new LinkedHashMap<>();
		expected.put( "id", true );
		expected.put( "name", true );
		expected.put( "product.title", true );

		assertEquals( expected, selector.propertiesToSelect() );
		assertArrayEquals( new String[] { "id", "name", "product.title" },
		                   selector.propertiesToSelect().keySet().toArray( new String[3] ) );
	}

	@Test
	public void incrementalWithExclusion() {
		selector.configure( "id", "name" );
		selector.configure( "~id", "product.title", "~date", "." );

		Map<String, Boolean> expected = new LinkedHashMap<>();
		expected.put( "id", false );
		expected.put( "name", true );
		expected.put( "product.title", true );
		expected.put( "date", false );

		assertEquals( expected, selector.propertiesToSelect() );
		assertArrayEquals( new String[] { "id", "name", "product.title", "date" },
		                   selector.propertiesToSelect().keySet().toArray( new String[4] ) );
	}
}
