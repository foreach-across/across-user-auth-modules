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
import com.foreach.across.modules.entity.services.EntityFormService;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.elements.*;
import com.foreach.across.modules.web.ui.elements.builder.ContainerViewElementBuilder;
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
	private EntityFormService entityFormService;

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

		// createBuilder

		// buildElements

		// customizeElements

		view.setEntityProperties( getOldEntityProperties( viewCreationContext, entityConfiguration,
		                                                  messageCodeResolver ) );

		view.addAttribute( "newElements",
		                   buildViewElements( viewCreationContext, viewElementBuilderContext, messageCodeResolver )
		);

		extendViewModel( viewCreationContext, view );
	}

	protected EntityViewElementBuilderContext createEntityViewElementBuilderContext( T view ) {
		return new EntityViewElementBuilderContext<>( view );
	}

	protected com.foreach.across.modules.web.ui.ViewElements buildViewElements(
			V viewCreationContext,
			EntityViewElementBuilderContext<T> viewElementBuilderContext,
			EntityMessageCodeResolver messageCodeResolver
	) {
		EntityConfiguration entityConfiguration = viewCreationContext.getEntityConfiguration();
		List<EntityPropertyDescriptor> descriptors = getPropertyDescriptors( entityConfiguration );

		ContainerViewElementBuilder container = bootstrapUi.container();

		Collection<com.foreach.across.modules.web.ui.ViewElementBuilder> builders
				= getViewElementBuilders( entityConfiguration, descriptors,
				                          com.foreach.across.modules.entity.newviews.ViewElementMode.FORM_WRITE );

		for ( com.foreach.across.modules.web.ui.ViewElementBuilder builder : builders ) {
			if ( builder != null ) {
				container.add( builder );
			}
		}

		return container.build( viewElementBuilderContext );
	}

	/**
	 * Get the actual property descriptors that are configured on this {@link EntityViewFactory}.
	 *
	 * @param entityConfiguration from which to fetch the properties by default
	 * @return list of {@link EntityPropertyDescriptor}s
	 */
	protected List<EntityPropertyDescriptor> getPropertyDescriptors( EntityConfiguration entityConfiguration ) {
		EntityPropertyFilter filter = getPropertyFilter() != null ? getPropertyFilter() : EntityPropertyFilters.NoOp;
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

	private ViewElements getOldEntityProperties( V viewCreationContext,
	                                             EntityConfiguration entityConfiguration,
	                                             EntityMessageCodeResolver messageCodeResolver ) {
		EntityPropertyFilter filter = getPropertyFilter() != null ? getPropertyFilter() : EntityPropertyFilters.NoOp;

		EntityPropertyRegistry registry = getPropertyRegistry();

		if ( registry == null ) {
			registry = entityConfiguration.getPropertyRegistry();
		}

		ViewElementBuilderContext builderContext
				= entityFormService.createBuilderContext( entityConfiguration, registry, messageCodeResolver,
				                                          getMode() );

		List<EntityPropertyDescriptor> descriptors;

		if ( getPropertyComparator() != null ) {
			descriptors = registry.getProperties( filter, getPropertyComparator() );
		}
		else {
			descriptors = registry.getProperties( filter );
		}

		ViewElements propertyViews = new ViewElements();
		buildOldViewElements( viewCreationContext, builderContext, descriptors, propertyViews );

		return customizeViewElements( propertyViews );
	}

	protected ViewElements customizeViewElements( ViewElements elements ) {
		return elements;
	}

	@Deprecated
	protected void buildOldViewElements( V viewCreationContext,
	                                     ViewElementBuilderContext builderContext,
	                                     Collection<EntityPropertyDescriptor> descriptors,
	                                     ViewElements viewElements ) {
		for ( EntityPropertyDescriptor descriptor : descriptors ) {
			ViewElement propertyView = createPropertyView( builderContext, descriptor );

			if ( propertyView != null ) {
				viewElements.add( propertyView );
			}
		}
	}

	@Deprecated
	protected ViewElement createPropertyView( ViewElementBuilderContext builderContext,
	                                          EntityPropertyDescriptor descriptor ) {
		ViewElement element = builderContext.getViewElement( descriptor );
		applyNamePrefix( element, "entity." );

		return element;
	}

	@Deprecated
	private void applyNamePrefix( ViewElement element, String prefix ) {
		if ( element instanceof ViewElementSupport && element.isField() ) {
			( (ViewElementSupport) element ).setName( prefix + element.getName() );

		}

		if ( element instanceof ViewElements ) {
			for ( ViewElement child : ( (ViewElements) element ) ) {
				applyNamePrefix( child, prefix );
			}
		}
	}

	@Deprecated
	protected abstract ViewElementMode getMode();

	protected abstract void extendViewModel( V viewCreationContext, T view );
}
