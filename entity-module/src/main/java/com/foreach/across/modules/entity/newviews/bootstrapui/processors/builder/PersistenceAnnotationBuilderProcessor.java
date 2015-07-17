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
import org.springframework.data.mapping.PersistentProperty;

import javax.persistence.*;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * In case of a {@link EntityPropertyDescriptor} that has a {@link org.springframework.data.mapping.PersistentProperty}
 * attribute, this processor will check and execute for the configured annotation types.
 *
 * @author Arne Vandamme
 */
public abstract class PersistenceAnnotationBuilderProcessor<T extends ViewElementBuilder>
		implements EntityViewElementBuilderProcessor<T>
{
	private final Set<Class<? extends Annotation>> annotationTypes = new HashSet<>();

	public PersistenceAnnotationBuilderProcessor() {
		this(
				Column.class,
				OneToMany.class,
				OneToOne.class,
				ManyToMany.class,
				ManyToOne.class,
				Embedded.class,
				Id.class,
				EmbeddedId.class
		);
	}

	public PersistenceAnnotationBuilderProcessor( Class<? extends Annotation>... annotations ) {
		for ( Class<? extends Annotation> c : annotations ) {
			annotationTypes.add( c );
		}
	}

	@Override
	public void process( EntityPropertyDescriptor propertyDescriptor,
	                     EntityPropertyRegistry entityPropertyRegistry,
	                     EntityConfiguration entityConfiguration,
	                     ViewElementMode viewElementMode,
	                     T builder ) {
		PersistentProperty property = propertyDescriptor.getAttribute( PersistentProperty.class );

		if ( property != null ) {
			for ( Class<? extends Annotation> annotationType : annotationTypes ) {
				if ( property.isAnnotationPresent( annotationType ) ) {
					Annotation annotation = property.findAnnotation( annotationType );

					handleAnnotation( builder, annotation, AnnotationUtils.getAnnotationAttributes( annotation ),
					                  property );
				}
			}
		}
	}

	/**
	 * Helper that that checks if the annotation is any of the types specified.
	 *
	 * @param annotation to validate
	 * @param types      Array of types
	 * @return true if annotation is of any type specified in the array
	 */
	@SafeVarargs
	protected final boolean isOfType( Annotation annotation, Class<? extends Annotation>... types ) {
		return ArrayUtils.contains( types, annotation.annotationType() );
	}

	protected abstract void handleAnnotation( T builder,
	                                          Annotation annotation,
	                                          Map<String, Object> annotationAttributes,
	                                          PersistentProperty property );
}
