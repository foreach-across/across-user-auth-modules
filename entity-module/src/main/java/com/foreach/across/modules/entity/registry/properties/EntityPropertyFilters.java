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

import java.util.*;

/**
 * Utility class for creating {@link EntityPropertyFilter} instances.
 *
 * @see EntityPropertyComparators
 */
public final class EntityPropertyFilters
{
	/**
	 * No-op filter that does not filter at all but includes all properties.
	 */
	public static EntityPropertyFilter NOOP = new EntityPropertyFilter()
	{
		@Override
		public boolean shouldInclude( EntityPropertyDescriptor descriptor ) {
			return true;
		}
	};

	/**
	 * Only properties where {@link EntityPropertyDescriptor#isReadable()} returns {@code true}.
	 */
	public static EntityPropertyFilter READABLE = new EntityPropertyFilter()
	{
		@Override
		public boolean shouldInclude( EntityPropertyDescriptor descriptor ) {
			return descriptor.isReadable();
		}
	};

	/**
	 * Only properties where {@link EntityPropertyDescriptor#isHidden()} returns {@code false}.
	 * This is usually the default configured on a {@link EntityPropertyRegistry}.
	 */
	public static EntityPropertyFilter NOT_HIDDEN = new EntityPropertyFilter()
	{
		@Override
		public boolean shouldInclude( EntityPropertyDescriptor descriptor ) {
			return !descriptor.isHidden();
		}
	};

	private EntityPropertyFilters() {
	}

	/**
	 * @param propertyNames of properties to be included
	 * @return {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyFilters.Inclusive} instance
	 */
	public static EntityPropertyFilter include( String... propertyNames ) {
		return new Inclusive( Arrays.asList( propertyNames ) );
	}

	/**
	 * @param propertyNames of properties to be included
	 * @return {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyFilters.Inclusive} instance
	 */
	public static EntityPropertyFilter include( Collection<String> propertyNames ) {
		return new Inclusive( propertyNames );
	}

	/**
	 * @param propertyNames of properties to be excluded
	 * @return {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyFilters.Exclusive} instance
	 */
	public static EntityPropertyFilter exclude( String... propertyNames ) {
		return exclude( Arrays.asList( propertyNames ) );
	}

	/**
	 * @param propertyNames of properties to be excluded
	 * @return {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyFilters.Exclusive} instance
	 */
	public static EntityPropertyFilter exclude( Collection<String> propertyNames ) {
		return new Exclusive( propertyNames );
	}

	/**
	 * @param candidates filters to be included (nulls will be ignored)
	 * @return {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyFilters.Composite} instance
	 */
	public static EntityPropertyFilter composite( EntityPropertyFilter... candidates ) {
		List<EntityPropertyFilter> filters = new ArrayList<>( candidates.length );
		for ( EntityPropertyFilter candidate : candidates ) {
			if ( candidate != null ) {
				filters.add( candidate );
			}
		}

		return new Composite( filters );
	}

	/**
	 * Filter built around a {@link HashSet} containing the property names that should be included.
	 * This is the default implementation of the generic interface
	 * {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyFilter.Inclusive}.
	 */
	public static class Inclusive extends HashSet<String> implements EntityPropertyFilter.Inclusive
	{
		public Inclusive() {
		}

		public Inclusive( Collection<? extends String> c ) {
			super( c );
		}

		@Override
		public boolean shouldInclude( EntityPropertyDescriptor descriptor ) {
			return contains( descriptor.getName() );
		}
	}

	/**
	 * Filter built around a {@link HashSet} containing the property names that should <strong>not</strong>
	 * be included.
	 */
	public static class Exclusive extends HashSet<String> implements EntityPropertyFilter
	{
		public Exclusive() {
		}

		public Exclusive( Collection<? extends String> c ) {
			super( c );
		}

		@Override
		public boolean shouldInclude( EntityPropertyDescriptor descriptor ) {
			return !contains( descriptor.getName() );
		}
	}

	/**
	 * Simple composite that requires all member filters to include a property before this filter includes it.
	 */
	public static class Composite implements EntityPropertyFilter
	{
		private final Collection<EntityPropertyFilter> filters;

		public Composite( EntityPropertyFilter... filters ) {
			this.filters = Arrays.asList( filters );
		}

		public Composite( Collection<EntityPropertyFilter> filters ) {
			this.filters = filters;
		}

		@Override
		public boolean shouldInclude( EntityPropertyDescriptor descriptor ) {
			for ( EntityPropertyFilter filter : filters ) {
				if ( !filter.shouldInclude( descriptor ) ) {
					return false;
				}
			}
			return true;
		}
	}
}
