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

import org.springframework.core.convert.Property;

import java.beans.PropertyDescriptor;

/**
 * Central interface through which {@link EntityPropertyDescriptor} instances can be created.
 *
 * @author Arne Vandamme
 * @see SimpleEntityPropertyDescriptor
 * @see EntityPropertyDescriptorFactoryImpl
 */
public interface EntityPropertyDescriptorFactory
{
	MutableEntityPropertyDescriptor create( PropertyDescriptor prop, Class<?> entityType );

	MutableEntityPropertyDescriptor create( Property property );

	/**
	 * Create a new descriptor with the given name, using an existing instance as parent.
	 * The new descriptor will inherit all attributes ans properties from the parent, but can
	 * override them.
	 *
	 * @param name   the new name of the descriptor
	 * @param parent descriptor
	 * @return descriptor instance
	 */
	MutableEntityPropertyDescriptor createWithParent( String name, EntityPropertyDescriptor parent );
}
