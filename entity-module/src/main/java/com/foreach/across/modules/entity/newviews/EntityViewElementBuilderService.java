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
package com.foreach.across.modules.entity.newviews;

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.web.ui.ViewElementBuilder;

/**
 * Central service interface for retrieving or creating {@link com.foreach.across.modules.web.ui.ViewElementBuilder}
 * instances for {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor}s.
 *
 * @author Arne Vandamme
 * @see com.foreach.across.modules.entity.newviews.ViewElementTypeLookupStrategy
 */
public interface EntityViewElementBuilderService
{
	/**
	 * Find the best matching element type for a given {@link EntityPropertyDescriptor}.  Either a matching type
	 * will be found on the descriptor for the mode requested, or a resolving strategy will be used.
	 *
	 * @param entityConfiguration the descriptor belongs to
	 * @param descriptor          of the specific property
	 * @param mode                for which we are requesting the type
	 * @return element type
	 */
	String getElementType( EntityConfiguration entityConfiguration,
	                       EntityPropertyDescriptor descriptor,
	                       ViewElementMode mode );

	/**
	 * Creates a new {@link ViewElementBuilder} instance for the given property descriptor.  The type of the
	 * builder will be determined automatically based on the descriptor and requested mode.
	 *
	 * @param entityConfiguration the descriptor belongs to
	 * @param descriptor          of the specific property
	 * @param mode                for which we are requesting the builder
	 * @return newly created builder instance
	 */
	ViewElementBuilder createElementBuilder( EntityConfiguration entityConfiguration,
	                                         EntityPropertyDescriptor descriptor,
	                                         ViewElementMode mode );

	/**
	 * Creates a new {@link ViewElementBuilder} instance of the given elementType for the specific property descriptor.
	 *
	 * @param entityConfiguration the descriptor belongs to
	 * @param descriptor          of the specific property
	 * @param mode                for which we are requesting the builder
	 * @param elementType         of the builder that should be created
	 * @return newly created builder instance
	 */
	ViewElementBuilder createElementBuilder( EntityConfiguration entityConfiguration,
	                                         EntityPropertyDescriptor descriptor,
	                                         ViewElementMode mode,
	                                         String elementType );

	/**
	 * Retrieves a {@link ViewElementBuilder} for a property descriptor.  Depending on the backing strategy a new
	 * or reusable instance will be returned.
	 *
	 * @param entityConfiguration the descriptor belongs to
	 * @param descriptor          of the specific property
	 * @param mode                for which we are requesting the builder
	 * @return builder instance
	 */
	ViewElementBuilder getElementBuilder( EntityConfiguration entityConfiguration,
	                                      EntityPropertyDescriptor descriptor,
	                                      ViewElementMode mode );
}
