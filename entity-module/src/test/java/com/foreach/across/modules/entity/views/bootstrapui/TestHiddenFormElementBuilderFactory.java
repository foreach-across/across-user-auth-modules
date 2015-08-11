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
package com.foreach.across.modules.entity.views.bootstrapui;

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiFactory;
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiFactoryImpl;
import com.foreach.across.modules.bootstrapui.elements.HiddenFormElement;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.testmodules.springdata.business.Client;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.common.test.MockedLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = TestHiddenFormElementBuilderFactory.Config.class, loader = MockedLoader.class)
public class TestHiddenFormElementBuilderFactory extends ViewElementBuilderFactoryTestSupport<HiddenFormElement>
{
	@Autowired
	private ConversionService conversionService;

	@Override
	protected Class getTestClass() {
		return Instance.class;
	}

	@Test
	public void stringValue() {
		when( properties.get( "name" ).getValueFetcher() ).thenReturn( entity -> "fetchedValue" );
		when( conversionService.convert( eq( "fetchedValue" ), any( TypeDescriptor.class ),
		                                 any( TypeDescriptor.class ) ) )
				.thenReturn( "some value" );
		when( builderContext.getAttribute( EntityView.ATTRIBUTE_ENTITY ) ).thenReturn( "entity" );

		HiddenFormElement hidden = assembleAndVerify( "name" );
		assertEquals( "some value", hidden.getValue() );
	}

	@Test
	public void longValue() {
		when( properties.get( "number" ).getValueFetcher() ).thenReturn( entity -> 123L );
		when( conversionService.convert( eq( 123L ), any( TypeDescriptor.class ),
		                                 any( TypeDescriptor.class ) ) )
				.thenReturn( "321" );
		when( builderContext.getAttribute( EntityView.ATTRIBUTE_ENTITY ) ).thenReturn( "entity" );

		HiddenFormElement hidden = assembleAndVerify( "number" );
		assertEquals( "321", hidden.getValue() );
	}

	@Test
	public void unknownEntityValue() {
		Client client = new Client();
		when( properties.get( "client" ).getValueFetcher() ).thenReturn( entity -> client );
		when( conversionService.convert( eq( client ), any( TypeDescriptor.class ),
		                                 any( TypeDescriptor.class ) ) )
				.thenReturn( "some client" );
		when( builderContext.getAttribute( EntityView.ATTRIBUTE_ENTITY ) ).thenReturn( "entity" );

		HiddenFormElement hidden = assembleAndVerify( "client" );
		assertEquals( "some client", hidden.getValue() );

		verify( entityRegistry ).getEntityConfiguration( client );
	}

	@Test
	public void entityValue() {
		Client client = new Client();
		when( properties.get( "client" ).getValueFetcher() ).thenReturn( entity -> client );

		EntityConfiguration clientConfig = mock( EntityConfiguration.class );
		when( entityRegistry.getEntityConfiguration( client ) ).thenReturn( clientConfig );
		when( clientConfig.getId( client ) ).thenReturn( 9999L );

		when( conversionService.convert( eq( 9999L ), eq( String.class ) ) ).thenReturn( "ENTITY ID" );

		when( builderContext.getAttribute( EntityView.ATTRIBUTE_ENTITY ) ).thenReturn( "entity" );

		HiddenFormElement hidden = assembleAndVerify( "client" );
		assertEquals( "ENTITY ID", hidden.getValue() );
	}

	@SuppressWarnings("unchecked")
	private <V> V assembleAndVerify( String propertyName ) {
		HiddenFormElement control = assemble( propertyName, ViewElementMode.CONTROL );
		assertEquals( propertyName, control.getName() );
		assertEquals( "entity." + propertyName, control.getControlName() );
		assertFalse( control.isDisabled() );

		return (V) control;
	}

	@SuppressWarnings("unused")
	private static class Instance
	{
		public long number;

		public String name;

		public Client client;
	}

	@Configuration
	protected static class Config
	{
		@Bean
		@Primary
		public HiddenFormElementBuilderFactory labelFormElementBuilderFactory() {
			return new HiddenFormElementBuilderFactory();
		}

		@Bean
		public BootstrapUiFactory bootstrapUiFactory() {
			return new BootstrapUiFactoryImpl();
		}
	}
}
