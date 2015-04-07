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
import com.foreach.across.modules.bootstrapui.elements.LabelFormElement;
import com.foreach.across.modules.entity.newviews.ViewElementMode;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import com.foreach.common.test.MockedLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = TestLabelFormElementBuilderFactory.Config.class, loader = MockedLoader.class)
public class TestLabelFormElementBuilderFactory extends ViewElementBuilderFactoryTestSupport<ViewElement>
{
	@Override
	protected Class getTestClass() {
		return Validators.class;
	}

	@Test
	public void labelTextOnlyIfLabel() {
		TextViewElement text = assemble( "noValidator", ViewElementMode.LABEL );
		assertNotNull( text );
		assertEquals( "resolved: novalidator", text.getText() );
	}

	@Test
	public void labelTextOnlyIfListLabel() {
		TextViewElement text = assemble( "noValidatorNumber", ViewElementMode.LIST_LABEL );
		assertNotNull( text );
		assertEquals( "resolved: novalidatornumber", text.getText() );
	}

	@Test
	public void labelElementIsReturnedByDefault() {
		LabelFormElement label = assemble( "noValidator", ViewElementMode.CONTROL );
		assertNotNull( label );

		TextViewElement text = (TextViewElement) label.iterator().next();
		assertNotNull( text );
		assertEquals( "resolved: novalidator", text.getText() );
	}

	@SuppressWarnings( "unused" )
	private static class Validators
	{
		public String noValidator;

		public int noValidatorNumber;
	}

	@Configuration
	protected static class Config
	{
		@Bean
		@Primary
		public LabelFormElementBuilderFactory labelFormElementBuilderFactory() {
			return new LabelFormElementBuilderFactory();
		}

		@Bean
		public BootstrapUiFactory bootstrapUiFactory() {
			return new BootstrapUiFactoryImpl();
		}
	}
}
