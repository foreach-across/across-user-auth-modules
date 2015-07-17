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
package com.foreach.across.modules.entity.newviews;

import com.foreach.across.core.annotations.RefreshableCollection;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * @author Arne Vandamme
 */
@Service
public class EntityViewElementBuilderServiceImpl implements EntityViewElementBuilderService
{
	@RefreshableCollection(incremental = true, includeModuleInternals = true)
	private Collection<ViewElementTypeLookupStrategy> elementTypeLookupStrategies;

	@RefreshableCollection(incremental = true, includeModuleInternals = true)
	private Collection<EntityViewElementBuilderFactory> builderFactories;

	@Override
	public ViewElementBuilder getElementBuilder( EntityConfiguration entityConfiguration,
	                                             EntityPropertyDescriptor descriptor,
	                                             ViewElementMode mode ) {
		// One registered on descriptor?
		return createElementBuilder( entityConfiguration, descriptor, mode );
	}

	@Override
	public ViewElementBuilder createElementBuilder( EntityConfiguration entityConfiguration,
	                                                EntityPropertyDescriptor descriptor,
	                                                ViewElementMode mode ) {
		String elementType = getElementType( entityConfiguration, descriptor, mode );

		return createElementBuilder( entityConfiguration, descriptor, mode, elementType );
	}

	@Override
	public ViewElementBuilder createElementBuilder( EntityConfiguration entityConfiguration,
	                                                EntityPropertyDescriptor descriptor,
	                                                ViewElementMode mode,
	                                                String elementType ) {
		for ( EntityViewElementBuilderFactory builderFactory : builderFactories ) {
			if ( builderFactory.supports( elementType ) ) {
				return builderFactory.createBuilder( descriptor, entityConfiguration.getPropertyRegistry(),
				                                     entityConfiguration, mode );
			}
		}

		return null;
	}

	@Override
	public String getElementType( EntityConfiguration entityConfiguration,
	                              EntityPropertyDescriptor descriptor,
	                              ViewElementMode mode ) {
		// descriptor has one configured? use it

		// if not, fetch one
		for ( ViewElementTypeLookupStrategy lookupStrategy : elementTypeLookupStrategies ) {
			String elementType = lookupStrategy.findElementType( entityConfiguration, descriptor, mode );

			if ( elementType != null ) {
				return elementType;
			}
		}

		return null;
	}
}
