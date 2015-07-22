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
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestEntityPropertyComparators
{
	private List<EntityPropertyDescriptor> descriptors;

	@Before
	public void reset() {
		descriptors = new ArrayList<>();

		for ( PropertyDescriptor descriptor : BeanUtils.getPropertyDescriptors( Customer.class ) ) {
			descriptors.add( SimpleEntityPropertyDescriptor.forPropertyDescriptor( descriptor, Customer.class ) );
		}
	}

	@Test
	public void singleFilter() {
		Collections.sort( descriptors, new EntityPropertyComparators.Ordered( null, "id", "name", "value", "class" ) );

		assertEquals( "id", descriptors.get( 0 ).getName() );
		assertEquals( "name", descriptors.get( 1 ).getName() );
		assertEquals( "value", descriptors.get( 2 ).getName() );
		assertEquals( "class", descriptors.get( 3 ).getName() );
	}

	@Test
	public void compositeFilter() {
		EntityPropertyComparators.Ordered defaultOrder =  new EntityPropertyComparators.Ordered( null, "id", "name", "value", "class" );
		Collections.sort( descriptors, EntityPropertyComparators.composite(
				new EntityPropertyComparators.Ordered( "value", "name" ), defaultOrder ) );

		assertEquals( "value", descriptors.get( 0 ).getName() );
		assertEquals( "name", descriptors.get( 1 ).getName() );
		assertEquals( "id", descriptors.get( 2 ).getName() );
		assertEquals( "class", descriptors.get( 3 ).getName() );
	}

	private static class Customer
	{
		private long id;
		private String name;
		private Object value;

		public String getName() {
			return name;
		}

		public void setName( String name ) {
			this.name = name;
		}

		public long getId() {
			return id;
		}

		public void setId( long id ) {
			this.id = id;
		}

		public Object getValue() {
			return value;
		}

		public void setValue( Object value ) {
			this.value = value;
		}
	}
}
