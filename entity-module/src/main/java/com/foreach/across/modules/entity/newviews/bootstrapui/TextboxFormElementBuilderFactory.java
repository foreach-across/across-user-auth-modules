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
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiFactory;
import com.foreach.across.modules.bootstrapui.elements.TextboxFormElement;
import com.foreach.across.modules.bootstrapui.elements.builder.TextboxFormElementBuilder;
import com.foreach.across.modules.entity.newviews.EntityViewElementBuilderFactorySupport;
import com.foreach.across.modules.entity.newviews.ViewElementMode;
import com.foreach.across.modules.entity.newviews.bootstrapui.processors.builder.FormControlRequiredBuilderProcessor;
import com.foreach.across.modules.entity.newviews.bootstrapui.processors.builder.ValidationConstraintsBuilderProcessor;
import com.foreach.across.modules.entity.newviews.bootstrapui.processors.element.EntityPropertyControlPostProcessor;
import com.foreach.across.modules.entity.newviews.bootstrapui.processors.element.EntityPropertyValuePostProcessor;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;

import javax.validation.constraints.Size;
import javax.validation.metadata.ConstraintDescriptor;
import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Builds a {@link TextboxFormElement} for a given {@link EntityPropertyDescriptor}.
 *
 * @author Arne Vandamme
 */
public class TextboxFormElementBuilderFactory extends EntityViewElementBuilderFactorySupport<TextboxFormElementBuilder>
{
	@Autowired
	private ConversionService conversionService;

	@Autowired
	private BootstrapUiFactory bootstrapUi;

	private int maximumSingleLineLength = 300;

	public TextboxFormElementBuilderFactory() {
		addProcessor( new FormControlRequiredBuilderProcessor<TextboxFormElementBuilder>() );
		addProcessor( new TextboxConstraintsProcessor() );
	}

	public void setMaximumSingleLineLength( int maximumSingleLineLength ) {
		this.maximumSingleLineLength = maximumSingleLineLength;
	}

	@Override
	public boolean supports( String viewElementType ) {
		return BootstrapUiElements.TEXTAREA.equals( viewElementType )
				|| BootstrapUiElements.TEXTBOX.equals( viewElementType );
	}

	@Override
	protected TextboxFormElementBuilder createInitialBuilder( EntityPropertyDescriptor propertyDescriptor,
	                                                          EntityPropertyRegistry entityPropertyRegistry,
	                                                          EntityConfiguration entityConfiguration,
	                                                          ViewElementMode viewElementMode ) {
		return bootstrapUi.textbox()
		                  .name( propertyDescriptor.getName() )
		                  .controlName( propertyDescriptor.getName() )
		                  .multiLine( String.class.equals( propertyDescriptor.getPropertyType() ) )
		                  .postProcessor(
				                  new EntityPropertyValuePostProcessor<TextboxFormElement>( conversionService,
				                                                                            propertyDescriptor )
		                  )
		                  .postProcessor( new EntityPropertyControlPostProcessor<TextboxFormElement>() )
		                  .postProcessor( new TextboxPlaceholderProcessor( propertyDescriptor ) );

	}

	/**
	 * Resolves placeholder text for a property.
	 */
	private class TextboxPlaceholderProcessor implements ViewElementPostProcessor<TextboxFormElement>
	{
		private final EntityPropertyDescriptor propertyDescriptor;

		public TextboxPlaceholderProcessor( EntityPropertyDescriptor propertyDescriptor ) {
			this.propertyDescriptor = propertyDescriptor;
		}

		@Override
		public void postProcess( ViewElementBuilderContext builderContext, TextboxFormElement element ) {
			EntityMessageCodeResolver codeResolver = builderContext.getAttribute( EntityMessageCodeResolver.class );

			if ( codeResolver != null ) {
				String placeholder = codeResolver.getMessageWithFallback(
						"properties." + propertyDescriptor.getName() + "[placeholder]", ""
				);

				if ( !StringUtils.isBlank( placeholder ) ) {
					element.setPlaceholder( placeholder );
				}
			}
		}
	}

	/**
	 * Responsible for calculating max length and multi/single line type based on validation constraints.
	 */
	private class TextboxConstraintsProcessor extends ValidationConstraintsBuilderProcessor<TextboxFormElementBuilder>
	{
		@Override
		protected void handleConstraint( TextboxFormElementBuilder textbox,
		                                 Annotation annotation,
		                                 Map<String, Object> attributes,
		                                 ConstraintDescriptor constraint ) {
			if ( isOfType( annotation, Size.class, Length.class ) ) {
				Integer max = (Integer) attributes.get( "max" );

				if ( max != Integer.MAX_VALUE ) {
					textbox.maxLength( max );
					textbox.multiLine( max > maximumSingleLineLength );
				}
			}
		}
	}

}
