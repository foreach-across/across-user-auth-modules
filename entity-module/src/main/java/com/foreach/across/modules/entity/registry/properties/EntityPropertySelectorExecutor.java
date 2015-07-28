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
package com.foreach.across.modules.entity.registry.properties;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author Arne Vandamme
 */
public class EntityPropertySelectorExecutor
{
	private EntityPropertyRegistry current;
	private EntityPropertyRegistries propertyRegistries;

	public EntityPropertySelectorExecutor( EntityPropertyRegistry current,
	                                       EntityPropertyRegistries propertyRegistries ) {
		this.current = current;
		this.propertyRegistries = propertyRegistries;
	}

	public List<EntityPropertyDescriptor> select( EntityPropertySelector selector ) {
		List<EntityPropertyDescriptor> properties = new ArrayList<>();
		Set<String> excluded = new HashSet<>();

		for ( Map.Entry<String, Boolean> candidate : selector.propertiesToSelect().entrySet() ) {
			String propertyName = candidate.getKey();
			if ( candidate.getValue() ) {
				if ( EntityPropertySelector.ALL.equals( propertyName ) ) {
					properties.addAll( current.getProperties() );
				}
				else if ( EntityPropertySelector.ALL_REGISTERED.equals( propertyName ) ) {
					properties.addAll( current.getRegisteredDescriptors() );
				}
				else if ( propertyName.endsWith( "." + EntityPropertySelector.ALL ) ) {
					properties.addAll(
							selectNestedProperties(
									StringUtils.removeEnd( propertyName, "." + EntityPropertySelector.ALL ),
									EntityPropertySelector.ALL
							)
					);
				}
				else if ( propertyName.endsWith( "." + EntityPropertySelector.ALL_REGISTERED ) ) {
					properties.addAll(
							selectNestedProperties(
									StringUtils.removeEnd( propertyName, "." + EntityPropertySelector.ALL_REGISTERED ),
									EntityPropertySelector.ALL_REGISTERED
							)
					);
				}
				else {
					properties.add( current.getProperty( propertyName ) );
				}
			}
			else {
				excluded.add( propertyName );
			}
		}

		EntityPropertyFilter filter = selector.getFilter();

		if ( filter == null ) {
			filter = EntityPropertyFilters.NOOP;
		}

		Iterator<EntityPropertyDescriptor> iterator = properties.iterator();
		while ( iterator.hasNext() ) {
			EntityPropertyDescriptor candidate = iterator.next();
			if ( excluded.contains( candidate.getName() ) || !filter.shouldInclude( candidate ) ) {
				iterator.remove();
			}
		}

		return properties;
	}

	private List<EntityPropertyDescriptor> selectNestedProperties( String propertyName, String selectorString ) {
		EntityPropertyDescriptor descriptor = current.getProperty( propertyName );

		if ( descriptor.getPropertyType() != null ) {
			EntityPropertyRegistry registry = propertyRegistries.getRegistry( descriptor.getPropertyType(), true );

			List<EntityPropertyDescriptor> subProperties = registry.select(
					new EntityPropertySelector( selectorString )
			);
			List<EntityPropertyDescriptor> properties = new ArrayList<>( subProperties.size() );

			for ( EntityPropertyDescriptor subProperty : subProperties ) {
				properties.add( current.getProperty( propertyName + "." + subProperty.getName() ) );
			}

			return properties;
		}

		return Collections.emptyList();
	}
}
