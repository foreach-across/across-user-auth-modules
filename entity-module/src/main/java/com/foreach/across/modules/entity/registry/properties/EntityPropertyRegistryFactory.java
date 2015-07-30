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

/**
 * @author Arne Vandamme
 */
public interface EntityPropertyRegistryFactory
{
	EntityPropertyRegistry getOrCreate( Class<?> entityType );

	EntityPropertyRegistry get( Class<?> entityType );

	/**
	 * Creates a new registry that uses the existing as parent.  Will usually return a
	 * {@link MergingEntityPropertyRegistry} where the property descriptors are also inherited.
	 *
	 * @param entityPropertyRegistry that is the parent
	 * @return new registry instance for the parent source
	 */
	EntityPropertyRegistry createWithParent( EntityPropertyRegistry entityPropertyRegistry );
}
