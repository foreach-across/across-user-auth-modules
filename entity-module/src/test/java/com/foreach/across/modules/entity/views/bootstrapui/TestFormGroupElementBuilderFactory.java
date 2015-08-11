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
import com.foreach.across.modules.bootstrapui.elements.FormGroupElement;
import com.foreach.across.modules.bootstrapui.elements.builder.LabelFormElementBuilder;
import com.foreach.across.modules.bootstrapui.elements.builder.TextboxFormElementBuilder;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderService;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.common.test.MockedLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = TestFormGroupElementBuilderFactory.Config.class, loader = MockedLoader.class)
public class TestFormGroupElementBuilderFactory extends ViewElementBuilderFactoryTestSupport<FormGroupElement>
{
	@Autowired
	private EntityViewElementBuilderService viewElementBuilderService;

	@Override
	protected Class getTestClass() {
		return Instance.class;
	}

	@Test
	public void noDescriptionTextMeansNoHelpBlock() {
		when( properties.get( "text" ).isWritable() ).thenReturn( true );

		ViewElementBuilder controlBuilder = new TextboxFormElementBuilder().required();
		when( viewElementBuilderService
				      .getElementBuilder( entityConfiguration, properties.get( "text" ), ViewElementMode.CONTROL ) )
				.thenReturn( controlBuilder );

		ViewElementBuilder labelBuilder = new LabelFormElementBuilder();
		when( viewElementBuilderService
				      .getElementBuilder( entityConfiguration, properties.get( "text" ), ViewElementMode.LABEL ) )
				.thenReturn( labelBuilder );

		FormGroupElement group = assembleAndVerify( "text", true );
		assertNull( group.getHelpBlock() );
	}

	@Test
	public void descriptionTextIsBeingLookedUp() {
		when( properties.get( "text" ).isWritable() ).thenReturn( true );

		ViewElementBuilder controlBuilder = new TextboxFormElementBuilder().required();
		when( viewElementBuilderService
				      .getElementBuilder( entityConfiguration, properties.get( "text" ), ViewElementMode.CONTROL ) )
				.thenReturn( controlBuilder );

		ViewElementBuilder labelBuilder = new LabelFormElementBuilder();
		when( viewElementBuilderService
				      .getElementBuilder( entityConfiguration, properties.get( "text" ), ViewElementMode.LABEL ) )
				.thenReturn( labelBuilder );

		EntityMessageCodeResolver codeResolver = mock( EntityMessageCodeResolver.class );
		when( codeResolver.getMessageWithFallback( "properties.text[description]", "" ) ).thenReturn( "help text" );
		when( builderContext.getAttribute( EntityMessageCodeResolver.class ) ).thenReturn( codeResolver );

		FormGroupElement group = assembleAndVerify( "text", true );
		assertNotNull( group.getHelpBlock() );
	}

	@SuppressWarnings("unchecked")
	private <V> V assembleAndVerify( String propertyName, boolean required ) {
		FormGroupElement control = assemble( propertyName, ViewElementMode.FORM_WRITE );
		//assertEquals( propertyName, control.getName() );
		assertEquals( required, control.isRequired() );

		return (V) control;
	}

	@SuppressWarnings( "unused" )
	private static class Instance
	{
		private String text;
	}

	@Configuration
	protected static class Config
	{
		@Bean
		@Primary
		public FormGroupElementBuilderFactory formGroupElementBuilderFactory() {
			return new FormGroupElementBuilderFactory();
		}

		@Bean
		public BootstrapUiFactory bootstrapUiFactory() {
			return new BootstrapUiFactoryImpl();
		}
	}
}
