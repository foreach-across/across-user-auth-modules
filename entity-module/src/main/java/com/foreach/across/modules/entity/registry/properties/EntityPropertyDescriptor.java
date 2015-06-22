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

import com.foreach.across.core.support.ReadableAttributes;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import org.springframework.core.convert.TypeDescriptor;

public interface EntityPropertyDescriptor extends ReadableAttributes
{
	/**
	 * @return Property name.
	 */
	String getName();

	String getDisplayName();

	boolean isReadable();

	boolean isWritable();

	boolean isHidden();

	Class<?> getPropertyType();

	/**
	 * @return more detailed information about the property type (supporting generics)
	 */
	TypeDescriptor getPropertyTypeDescriptor();

	/**
	 * @return Associated instance that can fetch the property value from an instance.
	 */
	ValueFetcher getValueFetcher();

	/**
	 * Creates a new instance that is the result of merging the other descriptor into this one:
	 * properties set on the other descriptor will override this one.
	 *
	 * @param other EntityPropertyDescriptor to be merged into this one.
	 * @return New descriptor representing the merged instance.
	 */
	EntityPropertyDescriptor merge( EntityPropertyDescriptor other );
}
