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
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.web.ui.ViewElementBuilder;

/**
 * Processor interface for modifying a {@link ViewElementBuilder} that is being created for a particular
 * {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor}.
 *
 * @author Arne Vandamme
 */
public interface EntityViewElementBuilderProcessor<T extends ViewElementBuilder>
{
	/**
	 * Process the builder instance.
	 *
	 * @param propertyDescriptor     for which the builder is being created
	 * @param entityPropertyRegistry that owns the property descriptor
	 * @param entityConfiguration    entity configuration context (can be null)
	 * @param viewElementMode        mode for which the builder is being created
	 * @param builder                builder instance already created
	 */
	void process( EntityPropertyDescriptor propertyDescriptor,
	              EntityPropertyRegistry entityPropertyRegistry,
	              EntityConfiguration entityConfiguration,
	              ViewElementMode viewElementMode,
	              T builder );
}
