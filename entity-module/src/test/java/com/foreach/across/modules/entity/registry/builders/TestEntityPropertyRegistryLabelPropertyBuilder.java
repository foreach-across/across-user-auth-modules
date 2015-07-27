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
package com.foreach.across.modules.entity.registry.builders;

import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyRegistry;
import com.foreach.across.modules.entity.views.support.SpelValueFetcher;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.core.convert.TypeDescriptor;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
public class TestEntityPropertyRegistryLabelPropertyBuilder
{
	private static final EntityPropertyDescriptor EXISTING;

	private static final Answer<Void> EXISTING_ANSWER = new Answer()
	{
		@Override
		public Void answer( InvocationOnMock invocation ) throws Throwable {
			MutableEntityPropertyDescriptor label = (MutableEntityPropertyDescriptor) invocation.getArguments()[0];
			assertNotNull( label );
			assertEquals( "Label", label.getDisplayName() );
			assertEquals( "test", label.getAttribute( EntityAttributes.SORTABLE_PROPERTY ) );
			assertSame( EXISTING.getValueFetcher(), label.getValueFetcher() );
			assertNull( label.getPropertyType() );
			assertNull( label.getPropertyTypeDescriptor() );
			assertTrue( label.isReadable() );
			assertFalse( label.isWritable() );
			assertTrue( label.isHidden() );

			return null;
		}
	};

	static {
		EXISTING = mock( EntityPropertyDescriptor.class );
		ValueFetcher valueFetcher = mock( ValueFetcher.class );

		Mockito.<Class<?>>when( EXISTING.getPropertyType() ).thenReturn( Integer.class );

		Map<String, Object> attributes = Collections.singletonMap( EntityAttributes.SORTABLE_PROPERTY,
		                                                           (Object) "test" );
		when( EXISTING.attributeMap() ).thenReturn( attributes );
		when( EXISTING.getValueFetcher() ).thenReturn( valueFetcher );
		when( EXISTING.getPropertyTypeDescriptor() ).thenReturn( TypeDescriptor.valueOf( Integer.class ) );
	}

	private EntityPropertyRegistryBuilder builder = new EntityPropertyRegistryLabelPropertyBuilder();
	private MutableEntityPropertyRegistry propertyRegistry;

	@Before
	public void before() {
		propertyRegistry = mock( MutableEntityPropertyRegistry.class );
	}

	@Test
	public void defaultLabelIsToString() {
		doAnswer( new Answer()
		{
			@Override
			public Void answer( InvocationOnMock invocation ) throws Throwable {
				MutableEntityPropertyDescriptor label = (MutableEntityPropertyDescriptor) invocation.getArguments()[0];
				assertNotNull( label );
				assertEquals( "Label", label.getDisplayName() );
				assertTrue( label.getValueFetcher() instanceof SpelValueFetcher );
				assertNull( label.getPropertyType() );
				assertNull( label.getPropertyTypeDescriptor() );
				assertTrue( label.isReadable() );
				assertFalse( label.isWritable() );
				assertTrue( label.isHidden() );

				return null;
			}
		} ).when( propertyRegistry ).register( any( MutableEntityPropertyDescriptor.class ) );

		builder.buildRegistry( Object.class, propertyRegistry );

		verify( propertyRegistry ).register( any( MutableEntityPropertyDescriptor.class ) );
	}

	@Test
	public void nameIsUsedBeforeTitleAndLabel() {
		when( propertyRegistry.contains( "name" ) ).thenReturn( true );
		when( propertyRegistry.contains( "title" ) ).thenReturn( true );
		when( propertyRegistry.contains( "label" ) ).thenReturn( true );
		when( propertyRegistry.getProperty( "name" ) ).thenReturn( EXISTING );

		doAnswer( EXISTING_ANSWER ).when( propertyRegistry ).register( any( MutableEntityPropertyDescriptor.class ) );
		builder.buildRegistry( Object.class, propertyRegistry );
		verify( propertyRegistry ).register( any( MutableEntityPropertyDescriptor.class ) );
	}

	@Test
	public void titleIsUsedBeforeLabel() {
		when( propertyRegistry.contains( "name" ) ).thenReturn( false );
		when( propertyRegistry.contains( "title" ) ).thenReturn( true );
		when( propertyRegistry.contains( "label" ) ).thenReturn( true );
		when( propertyRegistry.getProperty( "title" ) ).thenReturn( EXISTING );

		doAnswer( EXISTING_ANSWER ).when( propertyRegistry ).register( any( MutableEntityPropertyDescriptor.class ) );
		builder.buildRegistry( Object.class, propertyRegistry );
		verify( propertyRegistry ).register( any( MutableEntityPropertyDescriptor.class ) );
	}

	@Test
	public void labelIsUsedIfAvailable() {
		when( propertyRegistry.contains( "name" ) ).thenReturn( false );
		when( propertyRegistry.contains( "title" ) ).thenReturn( false );
		when( propertyRegistry.contains( "label" ) ).thenReturn( true );
		when( propertyRegistry.getProperty( "label" ) ).thenReturn( EXISTING );

		doAnswer( EXISTING_ANSWER ).when( propertyRegistry ).register( any( MutableEntityPropertyDescriptor.class ) );
		builder.buildRegistry( Object.class, propertyRegistry );
		verify( propertyRegistry ).register( any( MutableEntityPropertyDescriptor.class ) );
	}

}
