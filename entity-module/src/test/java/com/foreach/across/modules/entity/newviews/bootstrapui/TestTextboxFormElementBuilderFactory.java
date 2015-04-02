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

import com.foreach.across.modules.bootstrapui.elements.FormGroupElement;
import com.foreach.across.modules.bootstrapui.elements.LabelFormElement;
import com.foreach.across.modules.bootstrapui.elements.TextareaFormElement;
import com.foreach.across.modules.entity.newviews.EntityViewElementBuilderFactory;
import com.foreach.common.test.MockedLoader;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static org.junit.Assert.*;

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

	@Override
	protected Class getTestClass() {
		return Validators.class;
	}

	@Override
	protected EntityViewElementBuilderFactory builderFactory() {
		return factory;
	}

	@Test
	public void noValidator() {
		FormGroupElement group = assemble( "noValidator" );
		assertNotNull( group );

		LabelFormElement label = group.getLabel();
		assertEquals( "novalidator", label.getText() );

		TextareaFormElement textbox = group.getControl();
		assertEquals( "noValidator", textbox.getName() );
		assertEquals( "noValidator", textbox.getControlName() );
		assertFalse( textbox.isReadonly() );
		assertFalse( textbox.isDisabled() );
		assertFalse( textbox.isRequired() );

		// Verify label text was looked up
		// Verify textbox placeholder text was looked up

		assertEquals( TextareaFormElement.Type.TEXTAREA, textbox.getType() );

		// required
		// depends on attributes
		// value setting

		/*
			template = assembleAndVerify( "noValidator" );
			assertNull( template.getMaxLength() );
			assertFalse( template.isRequired() );
			assertTrue( template.isMultiLine() );
			*/
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
			//assembler.setMaximumSingleLineLength( 200 );
			return factory;
		}
	}
}
