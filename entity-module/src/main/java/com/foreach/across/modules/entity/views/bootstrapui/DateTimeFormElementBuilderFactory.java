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
import com.foreach.across.modules.bootstrapui.elements.DateTimeFormElementConfiguration;
import com.foreach.across.modules.bootstrapui.elements.DateTimeFormElementConfiguration.Format;
import com.foreach.across.modules.bootstrapui.elements.builder.DateTimeFormElementBuilder;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactorySupport;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.processors.builder.FormControlRequiredBuilderProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.processors.builder.PersistenceAnnotationBuilderProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.processors.builder.ValidationConstraintsBuilderProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.PlaceholderTextPostProcessor;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.mapping.PersistentProperty;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Future;
import javax.validation.constraints.Past;
import javax.validation.metadata.ConstraintDescriptor;
import java.lang.annotation.Annotation;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * @author Arne Vandamme
 */
public class DateTimeFormElementBuilderFactory extends EntityViewElementBuilderFactorySupport<DateTimeFormElementBuilder>
{
	@Autowired
	private BootstrapUiFactory bootstrapUi;

	public DateTimeFormElementBuilderFactory() {
		addProcessor( new FormControlRequiredBuilderProcessor<>() );
		addProcessor( new TemporalAnnotationProcessor() );
		addProcessor( new PastAndFutureValidationProcessor() );
	}

	@Override
	public boolean supports( String viewElementType ) {
		return BootstrapUiElements.DATETIME.equals( viewElementType );
	}

	@Override
	public DateTimeFormElementBuilder createBuilder( EntityPropertyDescriptor propertyDescriptor,
	                                                 ViewElementMode viewElementMode ) {
		DateTimeFormElementBuilder builder = super.createBuilder( propertyDescriptor, viewElementMode );

		// Apply custom configuration
		DateTimeFormElementConfiguration configuration = propertyDescriptor.getAttribute(
				DateTimeFormElementConfiguration.class );

		if ( configuration != null ) {
			builder.format( configuration.getFormat() ).configuration( configuration );
		}
		else {
			configuration = builder.getConfiguration();
			configuration.setShowClearButton( !Boolean.TRUE.equals( builder.getRequired() ) );

			if ( propertyDescriptor.hasAttribute( Format.class ) ) {
				builder.format( propertyDescriptor.getAttribute( Format.class ) );
			}
		}

		return builder;
	}

	@Override
	protected DateTimeFormElementBuilder createInitialBuilder( EntityPropertyDescriptor propertyDescriptor,
	                                                           ViewElementMode viewElementMode ) {

		return bootstrapUi
				.datetime()
				.name( propertyDescriptor.getName() )
				.controlName( propertyDescriptor.getName() )
				.postProcessor( new PlaceholderTextPostProcessor<>( propertyDescriptor ) )
				.postProcessor(
						( builderContext, datetime ) ->
						{
							Object entity = EntityViewElementUtils.currentEntity( builderContext );
							ValueFetcher valueFetcher = propertyDescriptor.getValueFetcher();

							if ( entity != null && valueFetcher != null ) {
								Date propertyValue = (Date) valueFetcher.getValue( entity );

								if ( propertyValue != null ) {
									datetime.setValue( propertyValue );
								}
							}
						}
				)
				.postProcessor(
						( builderContext, datetime ) -> {
							LocaleContext localeContext = LocaleContextHolder.getLocaleContext();

							if ( localeContext != null && localeContext.getLocale() != null
									&& !localeContext.getLocale().equals( Locale.ENGLISH ) ) {
								datetime.getConfiguration().setLocale( localeContext.getLocale() );
							}
						}
				);
	}

	/**
	 * Change the basic format of the datetime picker if a {@link Temporal} annotation is present.
	 */
	public static class TemporalAnnotationProcessor extends PersistenceAnnotationBuilderProcessor<DateTimeFormElementBuilder>
	{
		@Override
		protected void handleAnnotation( DateTimeFormElementBuilder builder,
		                                 Annotation annotation,
		                                 Map<String, Object> annotationAttributes,
		                                 PersistentProperty property ) {
			if ( isOfType( annotation, Temporal.class ) ) {
				TemporalType temporalType = (TemporalType) annotationAttributes.get( "value" );

				if ( temporalType == TemporalType.DATE ) {
					builder.date();
				}
				else if ( temporalType == TemporalType.TIME ) {
					builder.time();
				}
			}
		}
	}

	/**
	 * Apply {@link Past} and {@link Future} validation constraints that determine min and max date of the datetime picker.
	 */
	public static class PastAndFutureValidationProcessor extends ValidationConstraintsBuilderProcessor<DateTimeFormElementBuilder>
	{
		@Override
		protected void handleConstraint( DateTimeFormElementBuilder builder,
		                                 Annotation annotation,
		                                 Map<String, Object> annotationAttributes,
		                                 ConstraintDescriptor constraint ) {
			DateTimeFormElementConfiguration configuration = builder.getConfiguration();

			if ( configuration != null ) {
				if ( isOfType( annotation, Past.class ) ) {
					configuration.setMaxDate( new Date() );
				}
				else if ( isOfType( annotation, Future.class ) ) {
					configuration.setMinDate( new Date() );
				}
			}
		}
	}
}
