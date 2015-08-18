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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Selection predicate for a number of properties.  Allows incremental building of the selector.
 * Property names can be added with the following options:
 * <ul>
 * <li>.: keep all configured rules</li>
 * <li>*: represents all properties returned when applying the default filter</li>
 * <li>**: represents all properties registerd, ignoring any default filter</li>
 * <li>propertyName: exactly that property</li>
 * <li>~propertyName: not that property</li>
 * </ul>
 * Selecting properties happens in 3 stages:
 * <ol>
 * <li>selecting all properties using the include selectors</li>
 * <li>applying the {@link EntityPropertyFilter} to those properties if there is one</li>
 * <li>excluding all explicitly excluded properties</li>
 * </ol>
 *
 * @author Arne Vandamme
 */
public class EntityPropertySelector
{
	public static final String CONFIGURED = ".";
	public static final String ALL = "*";
	public static final String ALL_REGISTERED = "**";

	private final Map<String, Boolean> propertiesToSelect = new LinkedHashMap<>();

	private EntityPropertyFilter filter;

	public EntityPropertySelector() {
	}

	public EntityPropertySelector( String... propertyNames ) {
		configure( propertyNames );
	}

	public EntityPropertyFilter getFilter() {
		return filter;
	}

	public void setFilter( EntityPropertyFilter filter ) {
		this.filter = filter;
	}

	/**
	 * @return map of property names with boolean indicated if they should be selected or not
	 */
	public Map<String, Boolean> propertiesToSelect() {
		return Collections.unmodifiableMap( propertiesToSelect );
	}

	public void configure( String... propertyNames ) {
		boolean keepAlreadyConfigured = false;
		Map<String, Boolean> newProperties = new LinkedHashMap<>();

		for ( String propertyName : propertyNames ) {
			if ( CONFIGURED.equals( propertyName ) ) {
				keepAlreadyConfigured = true;
			}
			else {
				if ( propertyName.startsWith( "~" ) ) {
					String actualName = StringUtils.substring( propertyName, 1 );
					if ( isReservedKeyWord( actualName ) ) {
						throw new IllegalArgumentException( "Illegal property selector: " + propertyName );
					}
					newProperties.put( actualName, false );
				}
				else {
					newProperties.put( propertyName, true );
				}
			}
		}

		if ( !keepAlreadyConfigured ) {
			propertiesToSelect.clear();
		}

		propertiesToSelect.putAll( newProperties );
	}

	private boolean isReservedKeyWord( String propertyName ) {
		return ALL.equals( propertyName ) || ALL_REGISTERED.equals( propertyName ) || CONFIGURED.equals( propertyName );
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( !( o instanceof EntityPropertySelector ) ) {
			return false;
		}
		EntityPropertySelector that = (EntityPropertySelector) o;
		return Objects.equals( propertiesToSelect, that.propertiesToSelect ) &&
				Objects.equals( filter, that.filter );
	}

	@Override
	public int hashCode() {
		return Objects.hash( propertiesToSelect, filter );
	}
}
