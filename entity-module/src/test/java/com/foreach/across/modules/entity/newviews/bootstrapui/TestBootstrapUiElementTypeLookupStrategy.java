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

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiElements;
import com.foreach.across.modules.entity.newviews.ViewElementMode;
import com.foreach.across.modules.entity.newviews.ViewElementTypeLookupStrategy;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.testmodules.springdata.business.Client;
import com.foreach.across.modules.entity.testmodules.springdata.business.CompanyStatus;
import com.foreach.common.test.MockedLoader;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = TestBootstrapUiElementTypeLookupStrategy.Config.class, loader = MockedLoader.class)
public class TestBootstrapUiElementTypeLookupStrategy
{
	@Autowired
	private ViewElementTypeLookupStrategy strategy;

	@Autowired
	private EntityRegistry entityRegistry;

	private EntityConfiguration entityConfiguration = mock( EntityConfiguration.class );
	private EntityPropertyDescriptor descriptor = mock( EntityPropertyDescriptor.class );

	@Before
	public void resetMocks() {
		reset( entityConfiguration, descriptor );

		when( descriptor.isWritable() ).thenReturn( true );
		when( descriptor.isReadable() ).thenReturn( true );
	}

	@Test
	public void formGroupIsReturnedForFormModes() {
		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( String.class, ViewElementMode.FORM_WRITE ) );
		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( Integer.class, ViewElementMode.FORM_WRITE ) );
		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( int.class, ViewElementMode.FORM_WRITE ) );
		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( String.class, ViewElementMode.FORM_READ ) );
		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( Integer.class, ViewElementMode.FORM_READ ) );
		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( int.class, ViewElementMode.FORM_READ ) );
	}

	@Test
	public void nullIsReturnedForFormModeReadWhenNotReadable() {
		when( descriptor.isReadable() ).thenReturn( false );

		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( String.class, ViewElementMode.FORM_WRITE ) );
		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( Integer.class, ViewElementMode.FORM_WRITE ) );
		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( int.class, ViewElementMode.FORM_WRITE ) );
		assertNull( lookup( String.class, ViewElementMode.FORM_READ ) );
		assertNull( lookup( Integer.class, ViewElementMode.FORM_READ ) );
		assertNull( lookup( int.class, ViewElementMode.FORM_READ ) );
	}

	@Test
	public void nullIsReturnedForFormModeWriteWhenNotWritable() {
		when( descriptor.isWritable() ).thenReturn( false );

		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( String.class, ViewElementMode.FORM_WRITE ) );
		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( Integer.class, ViewElementMode.FORM_WRITE ) );
		assertEquals( BootstrapUiElements.TEXT, lookup( int.class, ViewElementMode.CONTROL ) );
		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( String.class, ViewElementMode.FORM_READ ) );
		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( Integer.class, ViewElementMode.FORM_READ ) );
		assertEquals( BootstrapUiElements.FORM_GROUP, lookup( int.class, ViewElementMode.FORM_READ ) );
	}

	@Test
	public void textTypeForReadonlyValues() {
		assertEquals( BootstrapUiElements.TEXT, lookup( String.class, ViewElementMode.VALUE ) );
		assertEquals( BootstrapUiElements.TEXT, lookup( Integer.class, ViewElementMode.VALUE ) );
		assertEquals( BootstrapUiElements.TEXT, lookup( int.class, ViewElementMode.VALUE ) );
		assertEquals( BootstrapUiElements.TEXT, lookup( AtomicInteger.class, ViewElementMode.VALUE ) );
		assertEquals( BootstrapUiElements.TEXT, lookup( Long.class, ViewElementMode.VALUE ) );
		assertEquals( BootstrapUiElements.TEXT, lookup( BigDecimal.class, ViewElementMode.VALUE ) );
		assertEquals( BootstrapUiElements.TEXT, lookup( Date.class, ViewElementMode.VALUE ) );
		assertEquals( BootstrapUiElements.TEXT, lookup( String.class, ViewElementMode.LIST_VALUE ) );
		assertEquals( BootstrapUiElements.TEXT, lookup( Integer.class, ViewElementMode.LIST_VALUE ) );
		assertEquals( BootstrapUiElements.TEXT, lookup( int.class, ViewElementMode.LIST_VALUE ) );
		assertEquals( BootstrapUiElements.TEXT, lookup( AtomicInteger.class, ViewElementMode.LIST_VALUE ) );
		assertEquals( BootstrapUiElements.TEXT, lookup( Long.class, ViewElementMode.LIST_VALUE ) );
		assertEquals( BootstrapUiElements.TEXT, lookup( BigDecimal.class, ViewElementMode.LIST_VALUE ) );
		assertEquals( BootstrapUiElements.TEXT, lookup( Date.class, ViewElementMode.LIST_VALUE ) );
	}

	@Test
	public void labelTypeForLabelModes() {
		assertEquals( BootstrapUiElements.LABEL, lookup( String.class, ViewElementMode.LABEL ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( Integer.class, ViewElementMode.LABEL ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( int.class, ViewElementMode.LABEL ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( AtomicInteger.class, ViewElementMode.LABEL ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( Long.class, ViewElementMode.LABEL ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( BigDecimal.class, ViewElementMode.LABEL ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( Date.class, ViewElementMode.LABEL ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( String.class, ViewElementMode.LIST_LABEL ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( Integer.class, ViewElementMode.LIST_LABEL ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( int.class, ViewElementMode.LIST_LABEL ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( AtomicInteger.class, ViewElementMode.LIST_LABEL ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( Long.class, ViewElementMode.LIST_LABEL ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( BigDecimal.class, ViewElementMode.LIST_LABEL ) );
		assertEquals( BootstrapUiElements.LABEL, lookup( Date.class, ViewElementMode.LIST_LABEL ) );
	}

	@Test
	public void textboxTypeForPrimitives() {
		assertEquals( BootstrapUiElements.TEXTBOX, lookup( String.class, ViewElementMode.CONTROL ) );
		assertEquals( BootstrapUiElements.TEXTBOX, lookup( Integer.class, ViewElementMode.CONTROL ) );
		assertEquals( BootstrapUiElements.TEXTBOX, lookup( int.class, ViewElementMode.CONTROL ) );
		assertEquals( BootstrapUiElements.TEXTBOX, lookup( AtomicInteger.class, ViewElementMode.CONTROL ) );
		assertEquals( BootstrapUiElements.TEXTBOX, lookup( Long.class, ViewElementMode.CONTROL ) );
		assertEquals( BootstrapUiElements.TEXTBOX, lookup( BigDecimal.class, ViewElementMode.CONTROL ) );
		assertEquals( BootstrapUiElements.TEXTBOX, lookup( String.class, ViewElementMode.LIST_CONTROL ) );
		assertEquals( BootstrapUiElements.TEXTBOX, lookup( Integer.class, ViewElementMode.LIST_CONTROL ) );
		assertEquals( BootstrapUiElements.TEXTBOX, lookup( int.class, ViewElementMode.LIST_CONTROL ) );
		assertEquals( BootstrapUiElements.TEXTBOX, lookup( AtomicInteger.class, ViewElementMode.LIST_CONTROL ) );
		assertEquals( BootstrapUiElements.TEXTBOX, lookup( Long.class, ViewElementMode.LIST_CONTROL ) );
		assertEquals( BootstrapUiElements.TEXTBOX, lookup( BigDecimal.class, ViewElementMode.LIST_CONTROL ) );
	}

	@Ignore
	@Test
	public void dateTypeForDates() throws Exception {
		//assertEquals( BootstrapUiElements.DATE, lookup( Date.class, ViewElementMode.CONTROL ) );
	}

	@Test
	public void checkboxTypeForBooleans() {
		assertEquals( BootstrapUiElements.CHECKBOX, lookup( Boolean.class, ViewElementMode.CONTROL ) );
		assertEquals( BootstrapUiElements.CHECKBOX, lookup( boolean.class, ViewElementMode.CONTROL ) );
		assertEquals( BootstrapUiElements.CHECKBOX, lookup( AtomicBoolean.class, ViewElementMode.CONTROL ) );
		assertEquals( BootstrapUiElements.CHECKBOX, lookup( Boolean.class, ViewElementMode.LIST_CONTROL ) );
		assertEquals( BootstrapUiElements.CHECKBOX, lookup( boolean.class, ViewElementMode.LIST_CONTROL ) );
		assertEquals( BootstrapUiElements.CHECKBOX, lookup( AtomicBoolean.class, ViewElementMode.LIST_CONTROL ) );
	}

	@Test
	public void enumValueShouldReturnSelectType() {
		assertEquals( BootstrapUiElements.SELECT, lookup( CompanyStatus.class, ViewElementMode.CONTROL ) );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void singleEntityTypeShouldReturnSelectType() {
		EntityConfiguration clientConfig = mock( EntityConfiguration.class );

		when( entityConfiguration.getEntityType() ).thenReturn( (Class) Client.class );
		when( entityRegistry.getEntityConfiguration( Client.class ) ).thenReturn( clientConfig );

		assertEquals( BootstrapUiElements.SELECT, lookup( Client.class, ViewElementMode.CONTROL ) );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void collectionEntityTypeShouldReturnMultiCheckbox() {
		EntityConfiguration clientConfig = mock( EntityConfiguration.class );

		when( entityConfiguration.getEntityType() ).thenReturn( (Class) Client.class );
		when( entityRegistry.getEntityConfiguration( Client.class ) ).thenReturn( clientConfig );

		when( descriptor.getPropertyType() ).thenReturn( (Class) List.class );
		TypeDescriptor collectionTypeDescriptor = TypeDescriptor.collection(
				List.class, TypeDescriptor.valueOf( Client.class )
		);
		when( descriptor.getPropertyTypeDescriptor() ).thenReturn( collectionTypeDescriptor );

		assertEquals( BootstrapUiElements.MULTI_CHECKBOX,
		              strategy.findElementType( entityConfiguration, descriptor, ViewElementMode.CONTROL ) );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void collectionEnumShouldReturnMultiCheckbox() {
		EntityConfiguration clientConfig = mock( EntityConfiguration.class );

		when( entityConfiguration.getEntityType() ).thenReturn( (Class) Client.class );
		when( entityRegistry.getEntityConfiguration( Client.class ) ).thenReturn( clientConfig );

		when( descriptor.getPropertyType() ).thenReturn( (Class) Set.class );
		TypeDescriptor collectionTypeDescriptor = TypeDescriptor.collection(
				Set.class, TypeDescriptor.valueOf( CompanyStatus.class )
		);
		when( descriptor.getPropertyTypeDescriptor() ).thenReturn( collectionTypeDescriptor );

		assertEquals( BootstrapUiElements.MULTI_CHECKBOX,
		              strategy.findElementType( entityConfiguration, descriptor, ViewElementMode.CONTROL ) );
	}

	@Test
	public void unknownType() {
		assertNull( lookup( ViewElementMode.CONTROL ) );
		assertEquals( BootstrapUiElements.TEXT, lookup( ViewElementMode.VALUE ) );
	}

	@SuppressWarnings("unchecked")
	private String lookup( Class propertyType, ViewElementMode mode ) {
		when( descriptor.getPropertyType() ).thenReturn( propertyType );
		return strategy.findElementType( entityConfiguration, descriptor, mode );
	}

	private String lookup( ViewElementMode mode ) {
		return strategy.findElementType( entityConfiguration, descriptor, mode );
	}

	@Configuration
	protected static class Config
	{
		@Bean
		public BootstrapUiElementTypeLookupStrategy lookupStrategy() {
			return new BootstrapUiElementTypeLookupStrategy();
		}
	}
}
