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
package com.foreach.across.modules.entity.newviews.bootstrapui.processors.builder;

import com.foreach.across.modules.entity.newviews.EntityViewElementBuilderProcessor;
import com.foreach.across.modules.entity.newviews.ViewElementMode;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.annotation.AnnotationUtils;

import javax.validation.groups.Default;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.Map;

/**
 * Base implementation for handling javax.validation constraints.  This implementation will see if the
 * {@link EntityPropertyDescriptor} has a {@link PropertyDescriptor} registered that can be used
 * for fetching validation {@link ConstraintDescriptor} instances.  Constraint annotations will be passed
 * to the {@link #handleConstraint(ViewElementBuilder, Annotation, Map, ConstraintDescriptor)} implementation.
 *
 * @author Arne Vandamme
 */
public abstract class ValidationConstraintsBuilderProcessor<T extends ViewElementBuilder> implements EntityViewElementBuilderProcessor<T>
{
	@Override
	public void process( EntityPropertyDescriptor propertyDescriptor,
	                     EntityPropertyRegistry entityPropertyRegistry,
	                     EntityConfiguration entityConfiguration,
	                     ViewElementMode viewElementMode,
	                     T builder ) {
		PropertyDescriptor validationDescriptor = propertyDescriptor.getAttribute( PropertyDescriptor.class );

		if ( validationDescriptor != null && validationDescriptor.hasConstraints() ) {
			for ( ConstraintDescriptor constraint : validationDescriptor.getConstraintDescriptors() ) {
				Annotation annotation = constraint.getAnnotation();

				handleConstraint( builder,
				                  annotation,
				                  AnnotationUtils.getAnnotationAttributes( annotation ),
				                  constraint );
			}
		}
	}

	/**
	 * Helper that checks if the validation annotation should be applied to the default validation group.
	 * Either the {@link javax.validation.groups.Default} is explicitly added, either no groups are specified.
	 *
	 * @return true if no groups specified or default group is present
	 */
	@SuppressWarnings("all")
	protected boolean hasDefaultGroup( Map<String, Object> annotationAttributes ) {
		Object groupsValues = annotationAttributes.get( "groups" );

		if ( groupsValues != null && groupsValues instanceof Array ) {
			Object[] groups = (Object[]) groupsValues;

			if ( groups.length != 0 ) {
				return ArrayUtils.contains( groups, Default.class );
			}
		}

		return true;
	}

	/**
	 * Helper that that checks if the annotation is any of the types specified.
	 *
	 * @param annotation to validate
	 * @param types Array of types
	 * @return true if annotation is of any type specified in the array
	 */
	@SafeVarargs
	protected final boolean isOfType( Annotation annotation, Class<? extends Annotation>... types ) {
		return ArrayUtils.contains( types, annotation.annotationType() );
	}

	protected abstract void handleConstraint( T builder,
	                                          Annotation annotation,
	                                          Map<String, Object> annotationAttributes,
	                                          ConstraintDescriptor constraint );
}
