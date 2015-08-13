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
import com.foreach.across.modules.bootstrapui.elements.builder.OptionsFormElementBuilder;
import com.foreach.across.modules.entity.query.EntityQueryExecutor;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactorySupport;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.options.EntityQueryOptionIterableBuilder;
import com.foreach.across.modules.entity.views.bootstrapui.options.EnumOptionIterableBuilder;
import com.foreach.across.modules.entity.views.bootstrapui.options.OptionGenerator;
import com.foreach.across.modules.entity.views.bootstrapui.processors.builder.PersistenceAnnotationBuilderProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.processors.builder.ValidationConstraintsBuilderProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.EntityPropertyControlPostProcessor;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.data.mapping.PersistentProperty;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.metadata.ConstraintDescriptor;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;

/**
 * Builds a {@link OptionsFormElementBuilder} for a given {@link EntityPropertyDescriptor}.
 *
 * @author Arne Vandamme
 */
public class OptionsFormElementBuilderFactory extends EntityViewElementBuilderFactorySupport<OptionsFormElementBuilder>
{
	@Autowired
	private BootstrapUiFactory bootstrapUi;

	@Autowired
	private EntityRegistry entityRegistry;

	public OptionsFormElementBuilderFactory() {
		addProcessor( new PersistenceAnnotationsBuilderProcessor() );
		addProcessor( new OptionsRequiredBuilderProcessor() );
	}

	@Override
	public boolean supports( String viewElementType ) {
		return StringUtils.equals( BootstrapUiElements.SELECT, viewElementType )
				|| StringUtils.equals( BootstrapUiElements.MULTI_CHECKBOX, viewElementType );
	}

	@Override
	@SuppressWarnings("unchecked")
	protected OptionsFormElementBuilder createInitialBuilder( EntityPropertyDescriptor descriptor,
	                                                          ViewElementMode viewElementMode ) {

		boolean isCollection = isCollection( descriptor );
		Class<?> memberType = isCollection ? determineCollectionMemberType( descriptor ) : descriptor.getPropertyType();

		if ( memberType == null ) {
			throw new RuntimeException( "Unable to determine property type specific enough for form element assembly "
					                            + descriptor.getName() );
		}

		OptionsFormElementBuilder options
				= bootstrapUi.options()
				             .name( descriptor.getName() )
				             .controlName( EntityPropertyControlPostProcessor.PREFIX + descriptor.getName() );

		OptionGenerator optionGenerator = new OptionGenerator();
		optionGenerator.setSorted( true );

		if ( memberType.isEnum() ) {
			EnumOptionIterableBuilder iterableBuilder = new EnumOptionIterableBuilder();
			iterableBuilder.setEnumType( (Class<? extends Enum>) memberType );
			iterableBuilder.setValueFetcher( descriptor.getValueFetcher() );

			optionGenerator.setOptions( iterableBuilder );
		}
		else {
			EntityConfiguration optionType = entityRegistry.getEntityConfiguration( memberType );

			if ( optionType != null && optionType.hasAttribute( EntityQueryExecutor.class ) ) {
				EntityQueryOptionIterableBuilder iterableBuilder =
						EntityQueryOptionIterableBuilder.forEntityConfiguration( optionType );
				iterableBuilder.setValueFetcher( descriptor.getValueFetcher() );

				optionGenerator.setOptions( iterableBuilder );
			}
		}

		options.add( optionGenerator );

		if ( isCollection ) {
			options.checkbox().multiple( true );
		}

		return options;
	}

	private boolean isCollection( EntityPropertyDescriptor descriptor ) {
		return descriptor.getPropertyType().isArray()
				|| Collection.class.isAssignableFrom( descriptor.getPropertyType() );
	}

	private Class determineCollectionMemberType( EntityPropertyDescriptor descriptor ) {
		if ( descriptor.getPropertyType().isArray() ) {
			return descriptor.getPropertyType().getComponentType();
		}

		ResolvableType resolvableType = descriptor.getPropertyTypeDescriptor().getResolvableType();

		if ( resolvableType != null && resolvableType.hasGenerics() ) {
			return resolvableType.resolveGeneric( 0 );
		}

		return null;
	}

	public static class OptionsRequiredBuilderProcessor
			extends ValidationConstraintsBuilderProcessor<OptionsFormElementBuilder>
	{
		@Override
		protected void handleConstraint( OptionsFormElementBuilder builder,
		                                 Annotation annotation,
		                                 Map<String, Object> annotationAttributes,
		                                 ConstraintDescriptor constraint ) {
			if ( isOfType( annotation, NotNull.class, NotEmpty.class ) ) {
				builder.required();
			}
		}
	}

	public static class PersistenceAnnotationsBuilderProcessor
			extends PersistenceAnnotationBuilderProcessor<OptionsFormElementBuilder>
	{
		@Override
		protected void handleAnnotation( OptionsFormElementBuilder builder,
		                                 Annotation annotation,
		                                 Map<String, Object> annotationAttributes,
		                                 PersistentProperty property ) {
			if ( isOfType( annotation, ManyToOne.class, OneToOne.class ) ) {
				Boolean optional = (Boolean) annotationAttributes.get( "optional" );

				if ( optional != null && !optional ) {
					builder.required();
				}
			}
			else if ( isOfType( annotation, Column.class ) ) {
				Boolean nullable = (Boolean) annotationAttributes.get( "nullable" );

				if ( nullable != null && !nullable ) {
					builder.required();
				}
			}
		}
	}
}
