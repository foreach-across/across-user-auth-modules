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

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Registry containing the property information for a particular entity type.
 */
public interface EntityPropertyRegistry
{
	/**
	 * Name of the label property.
	 */
	String LABEL = "#label";

	/**
	 * @param propertyName Name of the property.
	 * @return True if a property with that name is registered.
	 */
	boolean contains( String propertyName );

	// todo: move to mutable
	void register( MutableEntityPropertyDescriptor descriptor );

	EntityPropertyDescriptor getProperty( String propertyName );

	List<EntityPropertyDescriptor> getProperties();

	List<EntityPropertyDescriptor> getProperties( EntityPropertyFilter filter );

	List<EntityPropertyDescriptor> getProperties( EntityPropertyFilter filter,
	                                              Comparator<EntityPropertyDescriptor> comparator );

	void setDefaultOrder( String... propertyNames );

	void setDefaultOrder( Comparator<EntityPropertyDescriptor> defaultOrder );

	Collection<EntityPropertyDescriptor> getRegisteredDescriptors();

	Comparator<EntityPropertyDescriptor> getDefaultOrder();

	void setDefaultFilter( EntityPropertyFilter filter );

	EntityPropertyFilter getDefaultFilter();
}
