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

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiElements;
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiFactory;
import com.foreach.across.modules.bootstrapui.elements.TextboxFormElement;
import com.foreach.across.modules.bootstrapui.elements.builder.TextboxFormElementBuilder;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactorySupport;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderHelpers;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderProcessor;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.processors.builder.FormControlRequiredBuilderProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.processors.builder.ValidationConstraintsBuilderProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.PlaceholderTextPostProcessor;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;

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
	private EntityViewElementBuilderHelpers viewElementBuilderHelpers;

	@Autowired
	private BootstrapUiFactory bootstrapUi;

	private int maximumSingleLineLength = 300;

	public TextboxFormElementBuilderFactory() {
		addProcessor( new FormControlRequiredBuilderProcessor<>() );
		addProcessor( new TextboxConstraintsProcessor() );
		addProcessor( new EmailTypeDetectionProcessor() );
		addProcessor( new PasswordTypeDetectionProcessor() );
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
	                                                          ViewElementMode viewElementMode ) {
		TextboxFormElementBuilder textboxBuilder = bootstrapUi
				.textbox()
				.name( propertyDescriptor.getName() )
				.controlName( propertyDescriptor.getName() )
				.postProcessor( viewElementBuilderHelpers.createDefaultValueTextPostProcessor( propertyDescriptor ) )
				.postProcessor( new PlaceholderTextPostProcessor<>( propertyDescriptor ) );

		if ( propertyDescriptor.hasAttribute( TextboxFormElement.Type.class ) ) {
			textboxBuilder.type( propertyDescriptor.getAttribute( TextboxFormElement.Type.class ) );
		}
		else {
			textboxBuilder.multiLine( String.class.equals( propertyDescriptor.getPropertyType() ) );
		}

		return textboxBuilder;
	}

	/**
	 * Detects email types based on the {@link Email} annotation presence.  Only if there was no specific type
	 * set as attribute.
	 */
	public static class EmailTypeDetectionProcessor extends ValidationConstraintsBuilderProcessor<TextboxFormElementBuilder>
	{
		@Override
		public void process( EntityPropertyDescriptor propertyDescriptor,
		                     ViewElementMode viewElementMode,
		                     TextboxFormElementBuilder builder ) {
			if ( !propertyDescriptor.hasAttribute( TextboxFormElement.Type.class ) ) {
				super.process( propertyDescriptor, viewElementMode, builder );
			}
		}

		@Override
		protected void handleConstraint( TextboxFormElementBuilder builder,
		                                 Annotation annotation,
		                                 Map<String, Object> annotationAttributes,
		                                 ConstraintDescriptor constraint ) {
			if ( isOfType( annotation, Email.class ) ) {
				builder.type( TextboxFormElement.Type.EMAIL );
			}
		}
	}

	/**
	 * Any property named password is considered a password property.  This will also clear the set text
	 * so no password is communicated back to the user.
	 */
	public static class PasswordTypeDetectionProcessor implements EntityViewElementBuilderProcessor<TextboxFormElementBuilder>
	{
		@Override
		public void process( EntityPropertyDescriptor propertyDescriptor,
		                     ViewElementMode viewElementMode,
		                     TextboxFormElementBuilder builder ) {
			if ( !propertyDescriptor.hasAttribute( TextboxFormElement.Type.class ) ) {
				if ( "password".equals( propertyDescriptor.getName() ) ) {
					builder.type( TextboxFormElement.Type.PASSWORD )
					       .postProcessor( ( builderContext, element ) -> element.setText( null ) );
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
