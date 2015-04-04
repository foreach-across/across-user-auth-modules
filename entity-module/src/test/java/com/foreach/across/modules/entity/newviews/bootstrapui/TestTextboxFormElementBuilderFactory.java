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

import com.foreach.across.modules.bootstrapui.elements.*;
import com.foreach.across.modules.entity.newviews.EntityViewElementBuilderFactory;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import com.foreach.common.test.MockedLoader;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = TestTextboxFormElementBuilderFactory.Config.class, loader = MockedLoader.class)
public class TestTextboxFormElementBuilderFactory extends ViewElementBuilderFactoryTestSupport<FormGroupElement>
{
	@Autowired
	private TextboxFormElementBuilderFactory factory;

	@Autowired
	private ConversionService conversionService;

	@Override
	protected Class getTestClass() {
		return Validators.class;
	}

	@Override
	protected EntityViewElementBuilderFactory builderFactory() {
		return factory;
	}

	// depends on qualifiers should be set

	@Test
	public void noValidator() {
		TextareaFormElement textarea = assembleAndVerify( "noValidator", false );
		assertEquals( TextareaFormElement.Type.TEXTAREA, textarea.getType() );
		assertNull( textarea.getMaxLength() );
	}

	@Test
	public void noValidatorNumber() {
		TextboxFormElement textbox = assembleAndVerify( "noValidatorNumber", false );
		assertEquals( TextboxFormElement.Type.TEXT, textbox.getType() );
		assertNull( textbox.getMaxLength() );
	}

	@Test
	public void notNullValidator() {
		TextareaFormElement textarea = assembleAndVerify( "notNullValidator", true );
		assertNull( textarea.getMaxLength() );
	}

	@Test
	public void notBlankValidator() {
		TextareaFormElement textarea = assembleAndVerify( "notBlankValidator", true );
		assertNull( textarea.getMaxLength() );
	}

	@Test
	public void notEmptyValidator() {
		TextareaFormElement textarea = assembleAndVerify( "notEmptyValidator", true );
		assertNull( textarea.getMaxLength() );
	}

	@Test
	public void sizeValidator() {
		TextboxFormElement textbox = assembleAndVerify( "sizeValidator", false );
		assertEquals( Integer.valueOf( 200 ), textbox.getMaxLength() );
		assertFalse( textbox instanceof TextareaFormElement );
	}

	@Test
	public void sizeForMultiLineValidator() {
		TextareaFormElement textarea = assembleAndVerify( "sizeForMultiLineValidator", false );
		assertEquals( Integer.valueOf( 201 ), textarea.getMaxLength() );
	}

	@Test
	public void lengthValidator() {
		TextboxFormElement textbox = assembleAndVerify( "lengthValidator", false );
		assertEquals( Integer.valueOf( 10 ), textbox.getMaxLength() );
		assertFalse( textbox instanceof TextareaFormElement );
	}

	@Test
	public void combinedValidator() {
		TextboxFormElement textbox = assembleAndVerify( "combinedValidator", true );
		assertEquals( Integer.valueOf( 50 ), textbox.getMaxLength() );
		assertFalse( textbox instanceof TextareaFormElement );
	}

	@Test
	public void valueSetFromEntity() {
		when( properties.get( "noValidator" ).getValueFetcher() )
				.thenReturn( new ValueFetcher()
				{
					@Override
					public Object getValue( Object entity ) {
						return "fetchedValue";
					}
				} );

		when( conversionService.convert( eq( "fetchedValue" ), any( TypeDescriptor.class ),
		                                 any( TypeDescriptor.class ) ) )
				.thenReturn( "converted-value" );

		when( builderContext.getAttribute( EntityView.ATTRIBUTE_ENTITY ) ).thenReturn( "entity" );

		TextareaFormElement textarea = assembleAndVerify( "noValidator", false );
		assertEquals( "converted-value", textarea.getText() );
	}

	@SuppressWarnings("unchecked")
	private <V> V assembleAndVerify( String propertyName, boolean required ) {
		FormGroupElement group = assemble( propertyName );
		assertNotNull( group );
		assertEquals( required, group.isRequired() );

		LabelFormElement label = group.getLabel();
		assertEquals( "resolved: " + StringUtils.lowerCase( propertyName ), label.getText() );

		FormControlElementSupport control = group.getControl();
		assertEquals( propertyName, control.getName() );
		assertEquals( propertyName, control.getControlName() );
		assertFalse( control.isReadonly() );
		assertFalse( control.isDisabled() );
		assertEquals( required, control.isRequired() );

		return (V) control;
	}

	private static class Validators
	{
		public String noValidator;

		public int noValidatorNumber;

		@NotNull
		public String notNullValidator;

		@NotBlank
		public String notBlankValidator;

		@NotEmpty
		public String notEmptyValidator;

		@Size(min = 5, max = 200)
		public String sizeValidator;

		@Size(max = 201)
		public String sizeForMultiLineValidator;

		@Length(min = 1, max = 10)
		public String lengthValidator;

		@NotBlank
		@Size(max = 50)
		public String combinedValidator;
	}

	@Configuration
	protected static class Config
	{
		@Bean
		public TextboxFormElementBuilderFactory textboxFormElementBuilderFactory() {
			TextboxFormElementBuilderFactory factory = new TextboxFormElementBuilderFactory();
			factory.setMaximumSingleLineLength( 200 );

			return factory;
		}

		@Bean
		public ConversionService conversionService() {
			return mock( ConversionService.class );
		}
	}
}
