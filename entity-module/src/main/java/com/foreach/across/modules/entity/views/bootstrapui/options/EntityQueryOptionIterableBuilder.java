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
package com.foreach.across.modules.entity.views.bootstrapui.options;

import com.foreach.across.modules.bootstrapui.elements.builder.OptionFormElementBuilder;
import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.query.EntityQueryExecutor;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Generates {@link OptionFormElementBuilder}s for a particular entity type, where the list of options
 * is fetched through an {@link EntityQueryExecutor} and custom {@link EntityQuery}.
 * By default an {@link EntityQuery} without parameters will be used, resulting in all entities being returned.
 *
 * @author Arne Vandamme
 */
public class EntityQueryOptionIterableBuilder extends SelectedOptionIterableBuilderSupport
{
	private EntityModel<Object, Serializable> entityModel;
	private EntityQueryExecutor<Object> entityQueryExecutor;
	private EntityQuery entityQuery = EntityQuery.all();
	private boolean selfOptionIncluded;

	/**
	 * Creates an {@link EntityQueryOptionIterableBuilder} for all entities of a particular {@link EntityConfiguration}.
	 * By default the current entity being treated will not be provided as an option.
	 *
	 * @param entityConfiguration whose options to select
	 * @return option builder
	 */
	public static EntityQueryOptionIterableBuilder forEntityConfiguration( EntityConfiguration entityConfiguration ) {
		EntityQueryOptionIterableBuilder iterableBuilder = new EntityQueryOptionIterableBuilder();
		iterableBuilder.setEntityModel( entityConfiguration.getEntityModel() );
		iterableBuilder.setEntityQueryExecutor( entityConfiguration.getAttribute( EntityQueryExecutor.class ) );
		iterableBuilder.setSelfOptionIncluded( false );

		return iterableBuilder;
	}

	public EntityModel getEntityModel() {
		return entityModel;
	}

	@SuppressWarnings("unchecked")
	public void setEntityModel( EntityModel entityModel ) {
		Assert.notNull( entityModel );
		this.entityModel = entityModel;
	}

	public EntityQuery getEntityQuery() {
		return entityQuery;
	}

	public void setEntityQuery( EntityQuery entityQuery ) {
		Assert.notNull( entityQuery );
		this.entityQuery = entityQuery;
	}

	public EntityQueryExecutor getEntityQueryExecutor() {
		return entityQueryExecutor;
	}

	@SuppressWarnings("unchecked")
	public void setEntityQueryExecutor( EntityQueryExecutor entityQueryExecutor ) {
		Assert.notNull( entityQueryExecutor );
		this.entityQueryExecutor = entityQueryExecutor;
	}

	public boolean isSelfOptionIncluded() {
		return selfOptionIncluded;
	}

	/**
	 * In case of options that are the same type as the entity being built, should the entity
	 * itself be provided as an option.  Default value is {@code false}.
	 *
	 * @param selfOptionIncluded True when entity itself should be included.
	 */
	public void setSelfOptionIncluded( boolean selfOptionIncluded ) {
		this.selfOptionIncluded = selfOptionIncluded;
	}

	@Override
	public Iterable<OptionFormElementBuilder> buildOptions( ViewElementBuilderContext builderContext ) {
		Assert.notNull( entityModel );
		Assert.notNull( entityQuery );
		Assert.notNull( entityQueryExecutor );

		List<OptionFormElementBuilder> options = new ArrayList<>();

		Object entityBeingBuilt = EntityViewElementUtils.currentEntity( builderContext );
		Collection selected = retrieveSelected( builderContext );

		for ( Object entityOption : entityQueryExecutor.findAll( entityQuery ) ) {
			if ( selfOptionIncluded || !entityOption.equals( entityBeingBuilt ) ) {
				OptionFormElementBuilder option = new OptionFormElementBuilder();

				option.label( entityModel.getLabel( entityOption ) );
				option.value( entityModel.getId( entityOption ) );

				option.selected( selected.contains( entityOption ) );

				options.add( option );
			}
		}

		return options;
	}
}
