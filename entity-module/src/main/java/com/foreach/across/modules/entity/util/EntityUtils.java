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
package com.foreach.across.modules.entity.util;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class EntityUtils
{
	public static String generateDisplayName( String propertyName ) {
		String cleaned = propertyName.replace( '.', ' ' ).replace( '_', ' ' ).replaceAll( "[^\\p{L}\\p{Nd} ]+", " " );

		List<String> finished = new LinkedList<>();
		for ( String part : StringUtils.split( cleaned, ' ' ) ) {
			String previous = null;
			String capitalized = StringUtils.isAllUpperCase( part )
					? StringUtils.capitalize( StringUtils.lowerCase( part ) ) : StringUtils.capitalize( part );

			for ( String subPart : StringUtils.splitByCharacterTypeCamelCase( capitalized ) ) {
				if ( previous != null && ( StringUtils.length( previous ) == 1 || StringUtils.isNumeric( subPart ) ) ) {
					previous = previous + subPart;
					finished.set( finished.size() - 1, previous );
				}
				else {
					previous = finished.isEmpty() ? subPart : StringUtils.lowerCase( subPart );
					finished.add( previous );
				}
			}
		}

		return StringUtils.join( finished, " " );
	}

	public static String combineDisplayNames( String first, String... propertyNames ) {
		List<String> finished = new LinkedList<>();
		finished.add( generateDisplayName( first ) );
		for ( String name : propertyNames ) {
			finished.add( generateDisplayName( name ).toLowerCase() );
		}
		return StringUtils.join( finished, " " );
	}

	/**
	 * Translates the {@link Sort} property attached to this pageable.  Will create a new {@link Pageable}
	 * if sorting is modified. See {@link #translateSort(Sort, EntityPropertyRegistry)} for more details.
	 *
	 * @param pageable         instance to be translated
	 * @param propertyRegistry to be used for looking up matching properties
	 * @return modified instance or same if unmodified
	 */
	public static Pageable translateSort( Pageable pageable, EntityPropertyRegistry propertyRegistry ) {
		Sort sort = pageable.getSort();

		if ( sort != null ) {
			Sort modifiedSort = translateSort( sort, propertyRegistry );
			if ( !sort.equals( modifiedSort ) ) {
				return new PageRequest( pageable.getPageNumber(), pageable.getPageSize(), modifiedSort );
			}
		}

		return pageable;
	}

	/**
	 * <p>Translates the {@link Sort} instance based on the specified {@link EntityPropertyRegistry}.  For every
	 * {@link org.springframework.data.domain.Sort.Order} entry a property will be looked up in the registry.
	 * If a property is found, the {@link org.springframework.data.domain.Sort.Order} attribute of the property
	 * will be fetched and if one is specified, it will be used as the basis for the new entry.  Especially
	 * the {@link Sort.Order#getNullHandling()} and {@link Sort.Order#isIgnoreCase()} settings will be copied.
	 * In case null handling is native, a fixed null handling will be applied depending on the direction.</p>
	 * <p>Order entries for which no property or property attribute can be found, will be left unchanged.</p>
	 *
	 * @param sort             instance to be translated
	 * @param propertyRegistry to be used for looking up matching properties
	 * @return modified or possibly same instance if unmodified
	 */
	public static Sort translateSort( Sort sort, EntityPropertyRegistry propertyRegistry ) {
		List<Sort.Order> translated = new ArrayList<>();

		for ( Sort.Order order : sort ) {
			EntityPropertyDescriptor descriptor = propertyRegistry.getProperty( order.getProperty() );
			Sort.Order template = descriptor != null ? descriptor.getAttribute( Sort.Order.class ) : null;

			if ( template != null ) {
				Sort.Order clone = new Sort.Order( order.getDirection(), template.getProperty() );

				if ( template.isIgnoreCase() ) {
					clone = clone.ignoreCase();
				}
				if ( template.getNullHandling() == Sort.NullHandling.NATIVE ) {
					clone = clone.getDirection() == Sort.Direction.ASC ? clone.nullsFirst() : clone.nullsLast();
				}
				else {
					clone = clone.with( template.getNullHandling() );
				}

				translated.add( clone );
			}
			else {
				translated.add( order );
			}
		}

		return translated.isEmpty() ? null : new Sort( translated );
	}

	/**
	 * Create a {@link org.springframework.data.domain.Page} from any {@link java.lang.Iterable}.
	 *
	 * @param collection contains the items in the page
	 * @return Page instance
	 */
	public static <Y> Page<Y> asPage( Iterable<Y> collection ) {
		return new PageImpl<Y>( asList( collection ) );
	}

	/**
	 * Create a {@link List} from any {@link Iterable}.  If the iterable is a list the same instance
	 * will be returned.  In all other cases a new instance will be created from the elements of
	 * the iterable.
	 *
	 * @param iterable containing the elements
	 * @param <Y>      element type
	 * @return List instance
	 */
	public static <Y> List<Y> asList( Iterable<Y> iterable ) {
		if ( iterable instanceof List ) {
			return (List<Y>) iterable;
		}

		if ( iterable instanceof Collection ) {
			return new ArrayList<>( (Collection<Y>) iterable );
		}

		List<Y> list = new ArrayList<>();
		for ( Y item : iterable ) {
			list.add( item );
		}

		return list;
	}
}
