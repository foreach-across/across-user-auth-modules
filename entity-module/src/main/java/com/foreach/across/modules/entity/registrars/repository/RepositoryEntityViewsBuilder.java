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
package com.foreach.across.modules.entity.registrars.repository;

import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyComparators;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyFilters;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.MergingEntityPropertyRegistry;
import com.foreach.across.modules.entity.views.*;
import com.foreach.across.modules.hibernate.business.Auditable;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Attempts to create default views for an EntityConfiguration.
 * Creates a list, read, create, update and delete view if possible.
 */
public class RepositoryEntityViewsBuilder
{
	@Autowired
	private BeanFactory beanFactory;

	public void buildViews( MutableEntityConfiguration entityConfiguration ) {
		buildCreateView( entityConfiguration );
		buildUpdateView( entityConfiguration );

		// todo: support regular repository to be used instead of specific CrudRepository interface (use repo information)
		buildListView( entityConfiguration, (CrudRepository) entityConfiguration.getAttribute( Repository.class ) );
	}

	private void buildCreateView( MutableEntityConfiguration entityConfiguration ) {
		EntityFormViewFactory viewFactory = beanFactory.getBean( EntityFormViewFactory.class );
		viewFactory.setMessagePrefixes( "entityViews." + EntityFormView.CREATE_VIEW_NAME, "entityViews" );

		EntityPropertyRegistry registry = new MergingEntityPropertyRegistry(
				entityConfiguration.getPropertyRegistry()
		);

		viewFactory.setPropertyRegistry( registry );
		viewFactory.setTemplate( EntityFormView.VIEW_TEMPLATE );

		entityConfiguration.registerView( EntityFormView.CREATE_VIEW_NAME, viewFactory );
	}

	private void buildUpdateView( MutableEntityConfiguration entityConfiguration ) {
		EntityFormViewFactory viewFactory = beanFactory.getBean( EntityFormViewFactory.class );
		viewFactory.setMessagePrefixes( "entityViews." + EntityFormView.UPDATE_VIEW_NAME, "entityViews" );

		EntityPropertyRegistry registry = new MergingEntityPropertyRegistry(
				entityConfiguration.getPropertyRegistry()
		);

		viewFactory.setPropertyRegistry( registry );
		viewFactory.setTemplate( EntityFormView.VIEW_TEMPLATE );

		entityConfiguration.registerView( EntityFormView.UPDATE_VIEW_NAME, viewFactory );
	}

	private void buildListView( MutableEntityConfiguration entityConfiguration, CrudRepository repository ) {
		EntityListViewFactory viewFactory = beanFactory.getBean( EntityListViewFactory.class );
		viewFactory.setMessagePrefixes( "entityViews.listView", "entityViews" );

		EntityPropertyRegistry registry = new MergingEntityPropertyRegistry(
				entityConfiguration.getPropertyRegistry()
		);

		viewFactory.setPropertyRegistry( registry );
		viewFactory.setTemplate( EntityListView.VIEW_TEMPLATE );
		viewFactory.setPageFetcher( new RepositoryEntityListViewPageFetcher( repository ) );

		LinkedList<String> defaultProperties = new LinkedList<>();
		defaultProperties.add( EntityPropertyRegistry.LABEL );

		if ( SecurityPrincipal.class.isAssignableFrom( entityConfiguration.getEntityType() ) ) {
			defaultProperties.addFirst( "principalName" );
		}

		if ( Auditable.class.isAssignableFrom( entityConfiguration.getEntityType() ) ) {
			defaultProperties.add( "lastModified" );
		}

		viewFactory.setPropertyFilter( EntityPropertyFilters.include( defaultProperties ) );
		viewFactory.setPropertyComparator( EntityPropertyComparators.ordered( defaultProperties ) );
		viewFactory.setDefaultSort( determineDefaultSort( defaultProperties ) );

		entityConfiguration.registerView( EntityListView.VIEW_NAME, viewFactory );
	}

	private Sort determineDefaultSort( Collection<String> defaultProperties ) {
		String propertyName = null;

		if ( defaultProperties.contains( "name" ) ) {
			propertyName = "name";
		}
		else if ( defaultProperties.contains( "title" ) ) {
			propertyName = "title";
		}

		if ( propertyName != null ) {
			return new Sort( propertyName );
		}

		return null;
	}
}
