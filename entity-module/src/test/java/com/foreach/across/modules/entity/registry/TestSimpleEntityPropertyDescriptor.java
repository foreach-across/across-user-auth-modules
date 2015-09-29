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

import com.foreach.across.modules.entity.registry.properties.SimpleEntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import org.junit.Test;
import org.springframework.core.convert.TypeDescriptor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * @author Arne Vandamme
 */
public class TestSimpleEntityPropertyDescriptor
{
	@Test
	public void inheritedDescriptor() {
		ValueFetcher parentValueFetcher = mock( ValueFetcher.class );
		ValueFetcher childValueFetcher = mock( ValueFetcher.class );

		SimpleEntityPropertyDescriptor parent = new SimpleEntityPropertyDescriptor( "name" );
		parent.setDisplayName( "Name" );
		parent.setHidden( true );
		parent.setReadable( true );
		parent.setWritable( true );
		parent.setPropertyType( String.class );
		parent.setPropertyTypeDescriptor( TypeDescriptor.valueOf( Long.class ) );
		parent.setValueFetcher( parentValueFetcher );
		parent.setAttribute( "test", 123L );

		SimpleEntityPropertyDescriptor descriptor = new SimpleEntityPropertyDescriptor( "newName", parent );
		assertEquals( "newName", descriptor.getName() );
		assertEquals( "Name", descriptor.getDisplayName() );
		assertTrue( descriptor.isHidden() );
		assertTrue( descriptor.isReadable() );
		assertTrue( descriptor.isWritable() );
		assertEquals( String.class, descriptor.getPropertyType() );
		assertEquals( TypeDescriptor.valueOf( Long.class ), descriptor.getPropertyTypeDescriptor() );
		assertSame( parentValueFetcher, descriptor.getValueFetcher() );
		assertEquals( 123L, descriptor.getAttribute( "test" ) );

		descriptor.setDisplayName( "New name" );
		descriptor.setHidden( false );
		descriptor.setReadable( true );
		descriptor.setWritable( false );
		descriptor.setPropertyType( Long.class );
		descriptor.setPropertyTypeDescriptor( TypeDescriptor.valueOf( String.class ) );
		descriptor.setValueFetcher( childValueFetcher );
		descriptor.setAttribute( "test", 999L );

		assertEquals( "name", parent.getName() );
		assertEquals( "Name", parent.getDisplayName() );
		assertTrue( parent.isHidden() );
		assertTrue( parent.isReadable() );
		assertTrue( parent.isWritable() );
		assertEquals( String.class, parent.getPropertyType() );
		assertEquals( TypeDescriptor.valueOf( Long.class ), parent.getPropertyTypeDescriptor() );
		assertSame( parentValueFetcher, parent.getValueFetcher() );
		assertEquals( 123L, parent.getAttribute( "test" ) );

		assertEquals( "newName", descriptor.getName() );
		assertEquals( "New name", descriptor.getDisplayName() );
		assertFalse( descriptor.isHidden() );
		assertTrue( descriptor.isReadable() );
		assertFalse( descriptor.isWritable() );
		assertEquals( Long.class, descriptor.getPropertyType() );
		assertEquals( TypeDescriptor.valueOf( String.class ), descriptor.getPropertyTypeDescriptor() );
		assertSame( childValueFetcher, descriptor.getValueFetcher() );
		assertEquals( 999L, descriptor.getAttribute( "test" ) );
	}
}
