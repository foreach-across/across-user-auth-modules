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
package com.foreach.across.modules.entity.registry;

import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.SimpleEntityPropertyDescriptor;
import org.junit.Test;
import org.springframework.beans.BeanUtils;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * @author Arne Vandamme
 */
public class TestSimpleEntityPropertyDescriptor
{
	@Test
	public void mergeProperties() {
		SimpleEntityPropertyDescriptor one = new SimpleEntityPropertyDescriptor( "address" );
		one.setDisplayName( "Address" );
		one.setAttribute( EntityAttributes.SORTABLE_PROPERTY, "address" );

		SimpleEntityPropertyDescriptor two = new SimpleEntityPropertyDescriptor( "street" );
		two.setAttribute( EntityAttributes.SORTABLE_PROPERTY, "street" );

		EntityPropertyDescriptor merged = one.merge( two );

		assertEquals( "street", merged.getName() );
		assertEquals( "Address", merged.getDisplayName() );
		assertEquals( "street", merged.getAttribute( EntityAttributes.SORTABLE_PROPERTY ) );
	}

	@Test
	public void readableAndWritableProperty() {
		SimpleEntityPropertyDescriptor descriptor = SimpleEntityPropertyDescriptor.forPropertyDescriptor(
				BeanUtils.getPropertyDescriptor( Instance.class, "name" ), Instance.class
		);

		assertTrue( descriptor.isReadable() );
		assertTrue( descriptor.isWritable() );
		assertFalse( descriptor.isHidden() );
	}

	@Test
	public void nonWritablePropertyIsHiddenByDefault() {
		SimpleEntityPropertyDescriptor descriptor = SimpleEntityPropertyDescriptor.forPropertyDescriptor(
				BeanUtils.getPropertyDescriptor( Instance.class, "readonly" ), Instance.class
		);

		assertTrue( descriptor.isReadable() );
		assertFalse( descriptor.isWritable() );
		assertTrue( descriptor.isHidden() );
	}

	@Test
	public void nonReadablePropertyIsHiddenByDefault() {
		SimpleEntityPropertyDescriptor descriptor = SimpleEntityPropertyDescriptor.forPropertyDescriptor(
				BeanUtils.getPropertyDescriptor( Instance.class, "writeonly" ), Instance.class
		);

		assertFalse( descriptor.isReadable() );
		assertTrue( descriptor.isWritable() );
		assertTrue( descriptor.isHidden() );
	}

	@SuppressWarnings( "unused" )
	private static class Instance
	{
		private String name;
		private int readonly;
		private Date writeonly;

		public String getName() {
			return name;
		}

		public void setName( String name ) {
			this.name = name;
		}

		public int getReadonly() {
			return readonly;
		}

		public void setWriteonly( Date writeonly ) {
			this.writeonly = writeonly;
		}
	}
}
