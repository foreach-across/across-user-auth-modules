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
package com.foreach.across.modules.entity.newviews.bootstrapui;

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiFactory;
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiFactoryImpl;
import com.foreach.across.modules.bootstrapui.elements.CheckboxFormElement;
import com.foreach.across.modules.entity.newviews.ViewElementMode;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import com.foreach.common.test.MockedLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = TestCheckboxFormElementBuilderFactory.Config.class, loader = MockedLoader.class)
public class TestCheckboxFormElementBuilderFactory extends ViewElementBuilderFactoryTestSupport<CheckboxFormElement>
{
	@Override
	protected Class getTestClass() {
		return Booleans.class;
	}

	@Test
	public void withoutValue() {
		CheckboxFormElement checkbox = assembleAndVerify( "primitive", false );
		assertFalse( checkbox.isChecked() );

		checkbox = assembleAndVerify( "object", false );
		assertFalse( checkbox.isChecked() );

		checkbox = assembleAndVerify( "atomic", false );
		assertFalse( checkbox.isChecked() );
	}

	@Test
	public void checkedFromEntity() {
		when( properties.get( "primitive" ).getValueFetcher() ).thenReturn( new ValueFetcher()
		{
			@Override
			public Object getValue( Object entity ) {
				return true;
			}
		} );
		when( properties.get( "object" ).getValueFetcher() ).thenReturn( new ValueFetcher()
		{
			@Override
			public Object getValue( Object entity ) {
				return Boolean.TRUE;
			}
		} );
		when( properties.get( "atomic" ).getValueFetcher() ).thenReturn( new ValueFetcher()
		{
			@Override
			public Object getValue( Object entity ) {
				return new AtomicBoolean( true );
			}
		} );
		when( builderContext.getAttribute( EntityView.ATTRIBUTE_ENTITY ) ).thenReturn( "entity" );

		CheckboxFormElement checkbox = assembleAndVerify( "primitive", false );
		assertTrue( checkbox.isChecked() );

		checkbox = assembleAndVerify( "object", false );
		assertTrue( checkbox.isChecked() );

		checkbox = assembleAndVerify( "atomic", false );
		assertTrue( checkbox.isChecked() );
	}

	@Test
	public void uncheckedFromEntity() {
		when( properties.get( "primitive" ).getValueFetcher() ).thenReturn( new ValueFetcher()
		{
			@Override
			public Object getValue( Object entity ) {
				return false;
			}
		} );
		when( properties.get( "object" ).getValueFetcher() ).thenReturn( new ValueFetcher()
		{
			@Override
			public Object getValue( Object entity ) {
				return null;
			}
		} );
		when( properties.get( "atomic" ).getValueFetcher() ).thenReturn( new ValueFetcher()
		{
			@Override
			public Object getValue( Object entity ) {
				return new AtomicBoolean( false );
			}
		} );
		when( builderContext.getAttribute( EntityView.ATTRIBUTE_ENTITY ) ).thenReturn( "entity" );

		CheckboxFormElement checkbox = assembleAndVerify( "primitive", false );
		assertFalse( checkbox.isChecked() );

		checkbox = assembleAndVerify( "object", false );
		assertFalse( checkbox.isChecked() );

		checkbox = assembleAndVerify( "atomic", false );
		assertFalse( checkbox.isChecked() );
	}

	@SuppressWarnings("unchecked")
	private <V> V assembleAndVerify( String propertyName, boolean required ) {
		CheckboxFormElement control = assemble( propertyName, ViewElementMode.CONTROL );
		assertEquals( propertyName, control.getName() );
		assertEquals( "entity." + propertyName, control.getControlName() );
		assertEquals( "resolved: " + propertyName, control.getText() );
		assertFalse( control.isReadonly() );
		assertFalse( control.isDisabled() );
		assertEquals( required, control.isRequired() );
		assertEquals( "on", control.getValue() );

		return (V) control;
	}

	private static class Booleans
	{
		public boolean primitive;

		public Boolean object;

		public AtomicBoolean atomic;
	}

	@Configuration
	protected static class Config
	{
		@Bean
		public CheckboxFormElementBuilderFactory checkboxFormElementBuilderFactory() {
			return new CheckboxFormElementBuilderFactory();
		}

		@Bean
		public BootstrapUiFactory bootstrapUiFactory() {
			return new BootstrapUiFactoryImpl();
		}
	}
}
