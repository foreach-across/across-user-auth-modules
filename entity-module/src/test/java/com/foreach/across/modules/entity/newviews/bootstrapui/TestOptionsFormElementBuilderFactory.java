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
import com.foreach.across.modules.bootstrapui.elements.SelectFormElement;
import com.foreach.across.modules.entity.newviews.ViewElementMode;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.web.ui.elements.AbstractNodeViewElement;
import com.foreach.common.test.MockedLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = TestOptionsFormElementBuilderFactory.Config.class, loader = MockedLoader.class)
public class TestOptionsFormElementBuilderFactory extends ViewElementBuilderFactoryTestSupport<AbstractNodeViewElement>
{
	@Override
	protected Class getTestClass() {
		return Validators.class;
	}

	@Test
	public void enumNoValidator() {
		SelectFormElement select = assembleAndVerify( "enumNoValidator" );

		assertFalse( select.isRequired() );
		assertFalse( select.isMultiple() );
		assertEquals( 1, select.size() );
	}

	@Test
	public void enumNotNullValidator() {
		SelectFormElement select = assembleAndVerify( "enumNotNullValidator" );

		assertTrue( select.isRequired() );
		assertFalse( select.isMultiple() );
		assertEquals( 1, select.size() );
	}

	@Test
	public void enumManyToOneOptional() {
		SelectFormElement select = assembleAndVerify( "enumManyToOneOptional" );

		assertFalse( select.isRequired() );
		assertFalse( select.isMultiple() );
		assertEquals( 1, select.size() );
	}

	@Test
	public void enumManyToOneNonOptional() {
		SelectFormElement select = assembleAndVerify( "enumManyToOneNonOptional" );

		assertTrue( select.isRequired() );
		assertFalse( select.isMultiple() );
		assertEquals( 1, select.size() );
	}

	@SuppressWarnings("unchecked")
	private <V> V assembleAndVerify( String propertyName ) {
		EntityMessageCodeResolver codeResolver = mock( EntityMessageCodeResolver.class );
		when( builderContext.getAttribute( EntityMessageCodeResolver.class ) ).thenReturn( codeResolver );

		AbstractNodeViewElement control = assemble( propertyName, ViewElementMode.CONTROL );

		return (V) control;
	}

	@SuppressWarnings("unused")
	private enum TestEnum
	{
		ONE,
		TWO
	}

	@SuppressWarnings("unused")
	private static class Validators
	{
		public TestEnum enumNoValidator;

		@NotNull
		public TestEnum enumNotNullValidator;

		@Column
		public TestEnum enumManyToOneOptional;

		@Column(nullable = false)
		public TestEnum enumManyToOneNonOptional;
	}

	@Configuration
	protected static class Config
	{
		@Bean
		@Primary
		public OptionsFormElementBuilderFactory optionsFormElementBuilderFactory() {
			return new OptionsFormElementBuilderFactory();
		}

		@Bean
		public BootstrapUiFactory bootstrapUiFactory() {
			return new BootstrapUiFactoryImpl();
		}

		@Bean
		public ConversionService conversionService() {
			return mock( ConversionService.class );
		}
	}
}
