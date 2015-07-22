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
 * Utility class for creating {@link java.util.Comparator<EntityPropertyDescriptor>} instances.
 *
 * @author Arne Vandamme
 * @see Ordered
 * @see EntityPropertyFilters
 */
public final class EntityPropertyComparators
{
	private EntityPropertyComparators() {
	}

	public static Ordered ordered( String... propertyNames ) {
		return new Ordered( propertyNames );
	}

	public static Ordered ordered( Collection<String> propertyNames ) {
		return new Ordered( propertyNames );
	}

	public static Comparator<EntityPropertyDescriptor> composite( Comparator<EntityPropertyDescriptor>... comparators ) {
		return new Composite( comparators );
	}

	/**
	 * Comparator that contains a map of property names with a specified order.
	 * Undefined properties are assumed to have an order index of 0.
	 */
	public static class Ordered extends LinkedHashMap<String, Integer> implements Comparator<EntityPropertyDescriptor>
	{
		public Ordered() {
		}

		public Ordered( String... ordered ) {
			for ( int i = 0; i < ordered.length; i++ ) {
				put( ordered[i], -ordered.length + i );
			}
		}

		public Ordered( Collection<String> ordered ) {
			int i = 0;
			for ( String item : ordered ) {
				put( item, ++i );
			}
		}

		public Ordered( Map<? extends String, ? extends Integer> m ) {
			super( m );
		}

		@Override
		public int compare( EntityPropertyDescriptor left, EntityPropertyDescriptor right ) {
			Integer orderLeft = applyDefault( get( left.getName() ) );
			Integer orderRight = applyDefault( get( right.getName() ) );

			return orderLeft.compareTo( orderRight );
		}

		private Integer applyDefault( Integer fixed ) {
			return fixed != null ? fixed : 0;
		}
	}

	/**
	 * Composite that runs through all {@link Comparator<EntityPropertyDescriptor>} instances in the
	 * order specified until one of them returns non-zero or the entire list has passed.
	 */
	public static class Composite implements Comparator<EntityPropertyDescriptor>
	{
		private final Collection<Comparator<EntityPropertyDescriptor>> comparators;

		public Composite( Comparator<EntityPropertyDescriptor>... comparators ) {
			this( Arrays.asList( comparators ) );
		}

		public Composite( Collection<Comparator<EntityPropertyDescriptor>> comparators ) {
			this.comparators = comparators;
		}

		@Override
		public int compare( EntityPropertyDescriptor left, EntityPropertyDescriptor right ) {
			for ( Comparator<EntityPropertyDescriptor> comparator : comparators ) {
				int comparison = comparator.compare( left, right );

				if ( comparison != 0 ) {
					return comparison;
				}
			}

			return 0;
		}
	}
}
