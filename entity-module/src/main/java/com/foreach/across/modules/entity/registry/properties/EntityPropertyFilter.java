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

/**
 * Functional interface for determining which properties need to be included when fetching
 * from a {@link EntityPropertyRegistry}.  There is an accompanying utility class {@link EntityPropertyFilters}
 * that provides most common implementations.
 *
 * @see EntityPropertyFilters
 * @see EntityPropertyComparators
 */
public interface EntityPropertyFilter
{
	boolean shouldInclude( EntityPropertyDescriptor descriptor );

	/**
	 * <p>Sub interface stating that the property filter declares all included properties explicitly.
	 * Also a {@link Collection} that can be iterated for the property names that should be included.</p>
	 * <p>See the implementation details in {@link EntityPropertyRegistrySupport}.</p>
	 *
	 * @see com.foreach.across.modules.entity.registry.properties.EntityPropertyFilters.Inclusive
	 */
	interface Inclusive extends EntityPropertyFilter, Collection<String>
	{
	}
}
