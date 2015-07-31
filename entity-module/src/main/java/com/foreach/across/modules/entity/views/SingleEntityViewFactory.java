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

import com.foreach.across.modules.entity.newviews.EntityViewElementBuilderContext;
import com.foreach.across.modules.entity.newviews.ViewElementMode;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElements;
import com.foreach.across.modules.web.ui.elements.builder.ContainerViewElementBuilder;

import java.util.Collection;
import java.util.List;

/**
 * Simple implementation of {@link ConfigurablePropertiesEntityViewFactorySupport} that builds the view elements
 * in the mode specified, for the properties configured.
 */
public abstract class SingleEntityViewFactory<V extends ViewCreationContext, T extends EntityView>
		extends ConfigurablePropertiesEntityViewFactorySupport<V, T>
{
	private ViewElementMode viewElementMode = ViewElementMode.FORM_READ;

	protected ViewElementMode getViewElementMode() {
		return viewElementMode;
	}

	/**
	 * Set the mode for which the {@link com.foreach.across.modules.web.ui.ViewElement}s should be created.
	 * Defaults to {@link ViewElementMode#FORM_READ}.
	 *
	 * @param viewElementMode to generate controls for
	 */
	protected void setViewElementMode( ViewElementMode viewElementMode ) {
		this.viewElementMode = viewElementMode;
	}

	@Override
	protected ViewElements buildViewElements(
			V viewCreationContext,
			EntityViewElementBuilderContext<T> viewElementBuilderContext,
			EntityMessageCodeResolver messageCodeResolver
	) {
		EntityConfiguration entityConfiguration = viewCreationContext.getEntityConfiguration();
		List<EntityPropertyDescriptor> descriptors = getPropertyDescriptors( entityConfiguration );

		ContainerViewElementBuilder container = bootstrapUi.container();
		Collection<ViewElementBuilder> builders
				= getViewElementBuilders( entityConfiguration, descriptors, viewElementMode );

		for ( ViewElementBuilder builder : builders ) {
			if ( builder != null ) {
				container.add( builder );
			}
		}

		return container.build( viewElementBuilderContext );
	}
}
