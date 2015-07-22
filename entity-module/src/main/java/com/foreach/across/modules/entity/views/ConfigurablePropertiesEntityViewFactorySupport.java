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
package com.foreach.across.modules.entity.views;

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiFactory;
import com.foreach.across.modules.entity.newviews.EntityViewElementBuilderContext;
import com.foreach.across.modules.entity.newviews.EntityViewElementBuilderService;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyFilter;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyFilters;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.elements.ViewElementBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Extends the {@link SimpleEntityViewFactorySupport} with support for configuring a set of properties
 * that should be rendered for a given entity type.
 *
 * @author Arne Vandamme
 */
public abstract class ConfigurablePropertiesEntityViewFactorySupport<V extends ViewCreationContext, T extends EntityView>
		extends SimpleEntityViewFactorySupport<V, T>
{
	protected static final Logger LOG = LoggerFactory.getLogger( ConfigurablePropertiesEntityViewFactorySupport.class );

	@Autowired
	protected BootstrapUiFactory bootstrapUi;

	@Autowired
	protected EntityViewElementBuilderService viewElementBuilderService;

	private EntityPropertyRegistry propertyRegistry;
	private EntityPropertyFilter propertyFilter;
	private Comparator<EntityPropertyDescriptor> propertyComparator;

	public EntityPropertyRegistry getPropertyRegistry() {
		return propertyRegistry;
	}

	/**
	 * Set the {@link EntityPropertyRegistry} from which the {@link EntityPropertyDescriptor}s should be fetched.
	 */
	public void setPropertyRegistry( EntityPropertyRegistry propertyRegistry ) {
		this.propertyRegistry = propertyRegistry;
	}

	public EntityPropertyFilter getPropertyFilter() {
		return propertyFilter;
	}

	/**
	 * Set the {@link EntityPropertyFilter} that specifies which properties should be selected.
	 */
	public void setPropertyFilter( EntityPropertyFilter propertyFilter ) {
		this.propertyFilter = propertyFilter;
	}

	public Comparator<EntityPropertyDescriptor> getPropertyComparator() {
		return propertyComparator;
	}

	/**
	 * Set the {@link Comparator} that should be used for sorting the properties.
	 */
	public void setPropertyComparator( Comparator<EntityPropertyDescriptor> propertyComparator ) {
		this.propertyComparator = propertyComparator;
	}

	@Override
	protected void buildViewModel( V viewCreationContext,
	                               EntityConfiguration entityConfiguration,
	                               EntityMessageCodeResolver messageCodeResolver,
	                               T view ) {
		EntityViewElementBuilderContext<T> viewElementBuilderContext = createEntityViewElementBuilderContext( view );

		view.setViewElements(
				buildViewElements( viewCreationContext, viewElementBuilderContext, messageCodeResolver )
		);
	}

	protected EntityViewElementBuilderContext<T> createEntityViewElementBuilderContext( T view ) {
		return new EntityViewElementBuilderContext<>( view );
	}

	/**
	 * Get the actual property descriptors that are configured on this {@link EntityViewFactory}.
	 *
	 * @param entityConfiguration from which to fetch the properties by default
	 * @return list of {@link EntityPropertyDescriptor}s
	 */
	protected List<EntityPropertyDescriptor> getPropertyDescriptors( EntityConfiguration entityConfiguration ) {
		EntityPropertyFilter filter = getPropertyFilter() != null ? getPropertyFilter() : EntityPropertyFilters.NOOP;
		EntityPropertyRegistry registry = getPropertyRegistry( entityConfiguration );

		if ( getPropertyComparator() != null ) {
			return registry.getProperties( filter, getPropertyComparator() );
		}

		return registry.getProperties( filter );
	}

	/**
	 * @param entityConfiguration form which to fetch the properties by default
	 * @return registry instance to use
	 */
	protected EntityPropertyRegistry getPropertyRegistry( EntityConfiguration entityConfiguration ) {
		EntityPropertyRegistry registry = getPropertyRegistry();

		if ( registry == null ) {
			registry = entityConfiguration.getPropertyRegistry();
		}

		return registry;
	}

	/**
	 * Creates a {@link ViewElementBuilder} for every property for the given mode.
	 * If a given descriptor does not create a valid {@link com.foreach.across.modules.web.ui.ViewElementBuilder},
	 * a null entry will be inserted in the resulting list.
	 *
	 * @param viewElementMode for the builders
	 * @return collection of builders
	 */
	protected Collection<com.foreach.across.modules.web.ui.ViewElementBuilder> getViewElementBuilders(
			EntityConfiguration entityConfiguration,
			Collection<EntityPropertyDescriptor> descriptors,
			com.foreach.across.modules.entity.newviews.ViewElementMode viewElementMode ) {
		List<com.foreach.across.modules.web.ui.ViewElementBuilder> builders = new ArrayList<>( descriptors.size() );

		for ( EntityPropertyDescriptor descriptor : descriptors ) {
			builders.add(
					viewElementBuilderService.getElementBuilder( entityConfiguration, descriptor, viewElementMode )
			);
		}

		return builders;
	}


	protected abstract com.foreach.across.modules.web.ui.ViewElements buildViewElements(
			V viewCreationContext,
			EntityViewElementBuilderContext<T> viewElementBuilderContext,
			EntityMessageCodeResolver messageCodeResolver
	);
}
