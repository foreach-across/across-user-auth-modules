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
package com.foreach.across.modules.entity.views;

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.helpers.EntityViewElementBatch;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementBuilderContextImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * @author Arne Vandamme
 */
@Service
public class EntityViewElementBuilderHelpers
{
	@Autowired
	private EntityViewElementBuilderService builderService;

	@Autowired
	private EntityRegistry entityRegistry;

	/**
	 * Create a new batch builder for a given entity.  Requires an {@link EntityConfiguration} to exist for that
	 * entity type.  The default {@link com.foreach.across.modules.entity.support.EntityMessageCodeResolver} for
	 * the configuration will be used.
	 *
	 * @param entity instance, should not be null
	 * @return batch instance
	 */
	public <V> EntityViewElementBatch<V> createBatchForEntity( V entity ) {
		Assert.notNull( entity );

		EntityViewElementBatch<V> batch = new EntityViewElementBatch<>( builderService );
		EntityConfiguration entityConfiguration
				= entityRegistry.getEntityConfiguration( ClassUtils.getUserClass( entity ) );
		batch.setEntityConfiguration( entityConfiguration );
		batch.setPropertyRegistry( entityConfiguration.getPropertyRegistry() );
		batch.setPropertySelector( new EntityPropertySelector( EntityPropertySelector.ALL ) );

		ViewElementBuilderContext builderContext = new ViewElementBuilderContextImpl();
		builderContext.setAttribute( EntityMessageCodeResolver.class,
		                             entityConfiguration.getEntityMessageCodeResolver() );
		builderContext.setAttribute( EntityView.ATTRIBUTE_ENTITY, entity );

		batch.setViewElementBuilderContext( builderContext );

		return batch;
	}
}
