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
package com.foreach.across.modules.entity.views.helpers;

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderService;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper to quickly generate {@link com.foreach.across.modules.web.ui.ViewElement} instances
 * for a number of properties of a certain {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry}.
 * <p>
 * The properties are determined by the selector assigned through {@link #setPropertySelector(EntityPropertySelector)}.
 *
 * @author Arne Vandamme
 */
public class EntityViewElementBatch<T>
{
	private final EntityViewElementBuilderService viewElementBuilderService;

	private ViewElementBuilderContext viewElementBuilderContext;
	private EntityPropertyRegistry propertyRegistry;
	private EntityPropertySelector propertySelector;
	private EntityConfiguration entityConfiguration;

	private ViewElementMode viewElementMode;

	private Map<String, Object> builderHints = Collections.emptyMap();

	@Autowired
	public EntityViewElementBatch( EntityViewElementBuilderService viewElementBuilderService ) {
		this.viewElementBuilderService = viewElementBuilderService;
	}

	public void setEntityConfiguration( EntityConfiguration entityConfiguration ) {
		this.entityConfiguration = entityConfiguration;
	}

	public void setPropertyRegistry( EntityPropertyRegistry propertyRegistry ) {
		this.propertyRegistry = propertyRegistry;
	}

	public void setViewElementBuilderContext( ViewElementBuilderContext viewElementBuilderContext ) {
		this.viewElementBuilderContext = viewElementBuilderContext;
	}

	public void setPropertySelector( EntityPropertySelector propertySelector ) {
		this.propertySelector = propertySelector;
	}

	public void setViewElementMode( ViewElementMode viewElementMode ) {
		this.viewElementMode = viewElementMode;
	}

	/**
	 * Set a map of property builder hints.  The keys are the property names, the value should be one of the
	 * following:
	 * <ul>
	 * <li>{@link ViewElementMode}: for which specific mode the builder should be retrieved</li>
	 * <li>{@link String}: which element type should be generated for the property</li>
	 * <li>{@link ViewElementBuilder}: element builder that should be used for building the element</li>
	 * <li>{@link ViewElement}: fixed element that should be returned</li>
	 * </ul>
	 *
	 * @param builderHints map instance, should not be null
	 */
	public void setBuilderHints( Map<String, Object> builderHints ) {
		Assert.notNull( builderHints );
		this.builderHints = builderHints;
	}

	/**
	 * Generates the final elements.
	 *
	 * @return map of property name/ element
	 */
	public Map<String, ViewElement> build() {
		List<EntityPropertyDescriptor> descriptors = propertyRegistry.select( propertySelector );

		Map<String, ViewElement> elements = new LinkedHashMap<>();

		for ( EntityPropertyDescriptor descriptor : descriptors ) {
			String propertyName = descriptor.getName();
			Object builderHint = builderHints.get( propertyName );

			elements.put( propertyName, getViewElement( descriptor, builderHint ) );
		}

		return elements;
	}

	private ViewElement getViewElement( EntityPropertyDescriptor descriptor, Object builderHint ) {
		if ( builderHint instanceof ViewElement ) {
			return (ViewElement) builderHint;
		}

		ViewElementBuilder builder = getViewElementBuilder( descriptor, builderHint );
		return builder != null ? builder.build( viewElementBuilderContext ) : null;
	}

	private ViewElementBuilder getViewElementBuilder( EntityPropertyDescriptor descriptor, Object builderHint ) {
		if ( builderHint instanceof ViewElementBuilder ) {
			return (ViewElementBuilder) builderHint;
		}

		if ( builderHint instanceof String ) {
			return viewElementBuilderService.createElementBuilder(
					descriptor, viewElementMode, (String) builderHint
			);
		}

		ViewElementMode elementMode
				= builderHint instanceof ViewElementMode ? (ViewElementMode) builderHint : viewElementMode;

		return viewElementBuilderService.getElementBuilder( descriptor, elementMode );
	}
}
