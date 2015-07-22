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

import com.foreach.across.modules.bootstrapui.elements.Grid;
import com.foreach.across.modules.bootstrapui.elements.Style;
import com.foreach.across.modules.entity.config.ViewHelpers;
import com.foreach.across.modules.entity.newviews.EntityViewElementBuilderContext;
import com.foreach.across.modules.entity.newviews.bootstrapui.processors.element.EntityListActionsProcessor;
import com.foreach.across.modules.entity.newviews.bootstrapui.processors.element.EntitySummaryViewActionProcessor;
import com.foreach.across.modules.entity.newviews.bootstrapui.util.SortableTableBuilder;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyFilters;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.entity.views.support.ListViewEntityMessages;
import com.foreach.across.modules.entity.web.EntityLinkBuilder;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
import com.foreach.across.modules.spring.security.actions.AllowableActions;
import com.foreach.across.modules.web.ui.elements.builder.ContainerViewElementBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.ui.ModelMap;

import java.util.Collection;
import java.util.List;

/**
 * Handles a list of items (entities) with support for the properties to show,
 * paging, sorting and configuring the sortable properties.
 *
 * @author Arne Vandamme
 */
public class EntityListViewFactory<V extends ViewCreationContext> extends ConfigurablePropertiesEntityViewFactorySupport<V, EntityListView>
{
	@Autowired
	private ViewHelpers viewHelpers;

	private int pageSize = 50;
	private boolean showResultNumber = true;

	private Sort defaultSort;
	private Collection<String> sortableProperties;
	private EntityListViewPageFetcher pageFetcher;

	public EntityListViewFactory() {
		setPropertyFilter( EntityPropertyFilters.READABLE );
	}

	public EntityListViewPageFetcher getPageFetcher() {
		return pageFetcher;
	}

	/**
	 * @param pageFetcher The ListViewPageFetcher to use for retrieving the actual items.
	 */
	public void setPageFetcher( EntityListViewPageFetcher pageFetcher ) {
		this.pageFetcher = pageFetcher;
	}

	public int getPageSize() {
		return pageSize;
	}

	/**
	 * @param pageSize The default page size to use if no custom Pageable is passed.
	 */
	public void setPageSize( int pageSize ) {
		this.pageSize = pageSize;
	}

	public Sort getDefaultSort() {
		return defaultSort;
	}

	/**
	 * @param defaultSort The default sort to use if no custom Pageable or Sort is passed.
	 */
	public void setDefaultSort( Sort defaultSort ) {
		this.defaultSort = defaultSort;
	}

	public Collection<String> getSortableProperties() {
		return sortableProperties;
	}

	/**
	 * @param sortableProperties Names of the properties that should be sortable in the UI.
	 */
	public void setSortableProperties( Collection<String> sortableProperties ) {
		this.sortableProperties = sortableProperties;
	}

	public boolean isShowResultNumber() {
		return showResultNumber;
	}

	/**
	 * @param showResultNumber True if the index of an entity in the total results should be displayed.
	 */
	public void setShowResultNumber( boolean showResultNumber ) {
		this.showResultNumber = showResultNumber;
	}

	@Override
	protected EntityListView createEntityView( ModelMap model ) {
		return new EntityListView( model );
	}

	@Override
	protected com.foreach.across.modules.web.ui.ViewElements buildViewElements( V viewCreationContext,
	                                                                            EntityViewElementBuilderContext<EntityListView> viewElementBuilderContext,
	                                                                            EntityMessageCodeResolver messageCodeResolver ) {
		EntityListView view = viewElementBuilderContext.getEntityView();
		final EntityLinkBuilder linkBuilder = view.getEntityLinkBuilder();

		Pageable pageable = buildPageable( view );
		Page page = getPageFetcher().fetchPage( viewCreationContext, pageable, view );

		view.setPageable( pageable );
		view.setPage( page );

		AllowableActions allowableActions = viewCreationContext.getEntityConfiguration().getAllowableActions();
		EntityMessages messages = view.getEntityMessages();

		ContainerViewElementBuilder container = bootstrapUi.container();

		if ( allowableActions.contains( AllowableAction.CREATE ) ) {
			container.add(
					bootstrapUi.row().add(
							bootstrapUi.column( Grid.Device.MD.width( Grid.Width.FULL ) )
							           .name( "top-buttons" )
							           .add(
									           bootstrapUi.node( "p" )
									                      .add(
											                      bootstrapUi.button()
											                                 .name( "btn-create" )
											                                 .link( linkBuilder.create() )
											                                 .style( Style.Button.PRIMARY )
											                                 .text( messages.createAction() )
									                      )
							           )
					)
			);
		}

		EntityConfiguration entityConfiguration = viewCreationContext.getEntityConfiguration();
		List<EntityPropertyDescriptor> descriptors = getPropertyDescriptors( entityConfiguration );

		SortableTableBuilder tableBuilder = viewHelpers.createSortableTableBuilder();
		tableBuilder.setTableName( "entityList" );
		tableBuilder.setEntityConfiguration( entityConfiguration );
		tableBuilder.setPropertyDescriptors( descriptors );
		tableBuilder.setPagingMessages( (ListViewEntityMessages) view.getEntityMessages() );
		tableBuilder.setPage( page );
		tableBuilder.setSortableProperties( getSortableProperties() );
		tableBuilder.setShowResultNumber( isShowResultNumber() );

		EntityListActionsProcessor actionsProcessor
				= new EntityListActionsProcessor( bootstrapUi, entityConfiguration, linkBuilder, messages );
		tableBuilder.addHeaderRowProcessor( actionsProcessor );
		tableBuilder.addValueRowProcessor( actionsProcessor );

		EntitySummaryViewActionProcessor.autoRegister( viewCreationContext, tableBuilder,
		                                               EntityListView.SUMMARY_VIEW_NAME );

		container.add( tableBuilder );

		return container.build( viewElementBuilderContext );
	}

	private Pageable buildPageable( EntityListView view ) {
		Pageable existing = view.getPageable();

		if ( existing == null ) {
			existing = new PageRequest( 0, getPageSize(), getDefaultSort() );
		}

		return existing;
	}

	@Override
	protected ListViewEntityMessages createEntityMessages( EntityMessageCodeResolver codeResolver ) {
		return new ListViewEntityMessages( codeResolver );
	}
}
