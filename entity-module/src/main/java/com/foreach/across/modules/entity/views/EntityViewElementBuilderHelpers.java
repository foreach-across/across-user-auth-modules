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
	 * the configuration will be used.  The entity will be set as {@link EntityView#ATTRIBUTE_ENTITY} attribute.
	 *
	 * @param entity instance, should not be null
	 * @return batch instance
	 */
	public <V> EntityViewElementBatch<V> createBatchForEntity( V entity ) {
		Assert.notNull( entity );

		EntityViewElementBatch<V> batch = createBatchForEntityType( (Class<V>) ClassUtils.getUserClass( entity ) );
		batch.setEntity( entity );

		return batch;
	}

	/**
	 * Create a new batch builder for a given entity.  Requires an {@link EntityConfiguration} to exist for that
	 * entity type.  The default {@link com.foreach.across.modules.entity.support.EntityMessageCodeResolver} for
	 * the configuration will be used.
	 *
	 * @param entityType should not be null
	 * @return batch instance
	 */
	public <V> EntityViewElementBatch<V> createBatchForEntityType( Class<V> entityType ) {
		Assert.notNull( entityType );

		EntityViewElementBatch<V> batch = new EntityViewElementBatch<>( builderService );
		EntityConfiguration entityConfiguration = entityRegistry.getEntityConfiguration( entityType );
		batch.setPropertyRegistry( entityConfiguration.getPropertyRegistry() );
		batch.setPropertySelector( new EntityPropertySelector( EntityPropertySelector.ALL ) );

		batch.setAttribute( EntityMessageCodeResolver.class, entityConfiguration.getEntityMessageCodeResolver() );

		return batch;
	}
}
