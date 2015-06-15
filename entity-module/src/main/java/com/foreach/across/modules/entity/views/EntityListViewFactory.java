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

import com.foreach.across.modules.adminweb.AdminWeb;
import com.foreach.across.modules.bootstrapui.elements.GlyphIcon;
import com.foreach.across.modules.bootstrapui.elements.Grid;
import com.foreach.across.modules.bootstrapui.elements.Style;
import com.foreach.across.modules.bootstrapui.elements.builder.TableViewElementBuilder;
import com.foreach.across.modules.entity.newviews.EntityViewElementBuilderContext;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.elements.ViewElementBuilderContext;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.entity.views.support.ListViewEntityMessages;
import com.foreach.across.modules.entity.web.EntityLinkBuilder;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
import com.foreach.across.modules.spring.security.actions.AllowableActions;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import com.foreach.across.modules.web.ui.elements.IteratorViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import com.foreach.across.modules.web.ui.elements.builder.ContainerViewElementBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.ui.ModelMap;

import java.util.Collection;
import java.util.List;

import static com.foreach.across.modules.entity.newviews.ViewElementMode.LIST_LABEL;
import static com.foreach.across.modules.entity.newviews.ViewElementMode.LIST_VALUE;

/**
 * Handles a list of items (entities) with support for the properties to show,
 * paging, sorting and configuring the sortable properties.
 *
 * @author Arne Vandamme
 */
public class EntityListViewFactory<V extends ViewCreationContext> extends ConfigurablePropertiesEntityViewFactorySupport<V, EntityListView>
{
	@Autowired
	private AdminWeb adminWeb;

	private int pageSize = 50;
	private boolean showResultNumber = true;

	private Sort defaultSort;
	private Collection<String> sortableProperties;
	private EntityListViewPageFetcher pageFetcher;

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
		view.setShowResultNumber( isShowResultNumber() );

		AllowableActions allowableActions = viewCreationContext.getEntityConfiguration().getAllowableActions();
		EntityMessages messages = view.getEntityMessages();

		ContainerViewElementBuilder container = bootstrapUi.container();

		if ( allowableActions.contains( AllowableAction.CREATE ) ) {
			container.add(
					bootstrapUi.row().add(
							bootstrapUi.column( Grid.Device.MD.width( Grid.Width.FULL ) )
							           .name( "top-buttons" )
							           .add(
									           bootstrapUi.button()
									                      .name( "btn-create" )
									                      .link( view.getEntityLinkBuilder().create() )
									                      .style( Style.Button.PRIMARY )
									                      .text( messages.createAction() )
							           )
					)
			);
		}

		EntityConfiguration entityConfiguration = viewCreationContext.getEntityConfiguration();
		List<EntityPropertyDescriptor> descriptors = getPropertyDescriptors( entityConfiguration );

		TableViewElementBuilder table = bootstrapUi.table().name( "__tbl" );
		TableViewElementBuilder.Row headerRow = table.row();

		headerRow.add( table.heading().add( bootstrapUi.text( "#" ) ) );

		Collection<ViewElementBuilder> builders
				= getViewElementBuilders( entityConfiguration, descriptors, LIST_LABEL );

		// Create header cells
		for ( ViewElementBuilder labelBuilder : builders ) {
			headerRow.add( table.heading().add( labelBuilder ) );
		}

		table.header().add( headerRow );

		TableViewElementBuilder.Row valueRow = table.row();

		valueRow.add( table.cell().add( bootstrapUi.text().postProcessor(
				new ViewElementPostProcessor<TextViewElement>()
				{
					@Override
					public void postProcess( com.foreach.across.modules.web.ui.ViewElementBuilderContext builderContext,
					                         TextViewElement element ) {
						IteratorViewElementBuilderContext iteratorViewElementBuilderContext =
								(IteratorViewElementBuilderContext) builderContext;
						element.setText( String.valueOf( iteratorViewElementBuilderContext.getIndex() + 1 ) );
					}
				} ) ) );

		// Create value cells
		for ( EntityPropertyDescriptor descriptor : descriptors ) {
			ViewElementBuilder listValueBuilder = viewElementBuilderService.getElementBuilder(
					viewCreationContext.getEntityConfiguration(), descriptor, LIST_VALUE
			);

			if ( listValueBuilder != null ) {
				valueRow.add( table.cell().attribute( "data-field", descriptor.getName() ).add( listValueBuilder ) );
			}
			else {
				valueRow.add( table.cell().attribute( "data-field", descriptor.getName() ) );
				LOG.debug( "No LIST_VALUE element for {}", descriptor.getName() );
			}
		}

		if ( allowableActions.contains( AllowableAction.UPDATE ) ) {
			headerRow.add( table.heading() );

			valueRow.add( table.cell()
			                   .add(
					                   bootstrapUi.button()
					                              .link()
					                              .iconOnly( new GlyphIcon( GlyphIcon.EDIT ) )
					                              .text( messages.updateAction() )
					                              .postProcessor(
							                              new ViewElementPostProcessor<com.foreach.across.modules.bootstrapui.elements.ButtonViewElement>()
							                              {
								                              @Override
								                              public void postProcess( com.foreach.across.modules.web.ui.ViewElementBuilderContext builderContext,
								                                                       com.foreach.across.modules.bootstrapui.elements.ButtonViewElement element ) {
									                              IteratorViewElementBuilderContext ctx =
											                              (IteratorViewElementBuilderContext) builderContext;

									                              element.setUrl( linkBuilder.update( ctx.getItem() ) );
								                              }
							                              } )
			                   )
		                   /*.add(
				                   bootstrapUi.button()
				                              .link()
				                              .iconOnly( new GlyphIcon( GlyphIcon.REMOVE ) )
				                              .text( "Delete group" )
		                   )*/
			);
		}

		table.body()
		     .add(
				     bootstrapUi.generator( Object.class,
				                            com.foreach.across.modules.bootstrapui.elements.TableViewElement.Row.class )
				                .name( "__rows" )
				                .itemBuilder( valueRow )
				                .items( page.getContent() )
		     );

		container.add( table );

		return container.build( viewElementBuilderContext );
	}

	protected void buildNewViewElements( V viewCreationContext,
	                                     ViewElementBuilderContext builderContext,
	                                     Collection<EntityPropertyDescriptor> descriptors,
	                                     ContainerViewElementBuilder container ) {

		container.add(
				bootstrapUi.button()
				           .link( "create new" )
				           .style( Style.Button.PRIMARY )
				           .text( "Create a new group" )
		);

		TableViewElementBuilder table = bootstrapUi.table().name( "__tbl" );
		TableViewElementBuilder.Row headerRow = table.row();

		headerRow.add( table.heading().add( bootstrapUi.text( "#" ) ) );

		// Create header cells
		for ( EntityPropertyDescriptor descriptor : descriptors ) {
			headerRow.add(
					table.heading()
					     .add(
							     viewElementBuilderService.getElementBuilder(
									     viewCreationContext.getEntityConfiguration(),
									     descriptor,
									     LIST_LABEL )
					     )
			);
		}

		table.header().add( headerRow.add( table.heading() ) );

		TableViewElementBuilder.Row valueRow = table.row();

		valueRow.add( table.cell().add( bootstrapUi.text().postProcessor(
				new ViewElementPostProcessor<TextViewElement>()
				{
					@Override
					public void postProcess( com.foreach.across.modules.web.ui.ViewElementBuilderContext builderContext,
					                         TextViewElement element ) {
						IteratorViewElementBuilderContext iteratorViewElementBuilderContext =
								(IteratorViewElementBuilderContext) builderContext;
						element.setText( String.valueOf( iteratorViewElementBuilderContext.getIndex() + 1 ) );
					}
				} ) ) );

		// Create value cells
		for ( EntityPropertyDescriptor descriptor : descriptors ) {
			ViewElementBuilder listValueBuilder =
					viewElementBuilderService.getElementBuilder(
							viewCreationContext.getEntityConfiguration(),
							descriptor,
							LIST_VALUE );

			if ( listValueBuilder != null ) {
				valueRow.add( table.cell().attribute( "data-field", descriptor.getName() ).add( listValueBuilder ) );
			}
			else {
				valueRow.add( table.cell().attribute( "data-field", descriptor.getName() ) );
				LOG.debug( "No LIST_VALUE element for {}", descriptor.getName() );
			}
		}

		/*
		ContainerViewElement itemButtons = new ContainerViewElement( "itemButtons" );

			ButtonViewElement edit = new ButtonViewElement()
			{
				@Override
				public String print( Object entity ) {
					return view.getEntityLinkBuilder().update( entity );
				}
			};
			edit.setName( "btn-edit" );
			edit.setElementType( CommonViewElements.LINK_BUTTON );
			edit.setStyle( ButtonViewElement.Style.ICON );
			edit.setIcon( "edit" );
			edit.setLabel( messages.updateAction() );

			itemButtons.add( edit );

			( (ViewElements) table.getColumns() ).add( itemButtons );
		 */

		valueRow.add( table.cell()
		                   .add(
				                   bootstrapUi.button()
				                              .link()
				                              .iconOnly( new GlyphIcon( GlyphIcon.EDIT ) )
				                              .text( "Modify group" )
				                              .postProcessor(
						                              new ViewElementPostProcessor<com.foreach.across.modules.bootstrapui.elements.ButtonViewElement>()
						                              {
							                              @Override
							                              public void postProcess( com.foreach.across.modules.web.ui.ViewElementBuilderContext builderContext,
							                                                       com.foreach.across.modules.bootstrapui.elements.ButtonViewElement element ) {
								                              element.setUrl( "hip hoi" );
							                              }
						                              } )
		                   )
		                   .add(
				                   bootstrapUi.button()
				                              .link()
				                              .iconOnly( new GlyphIcon( GlyphIcon.REMOVE ) )
				                              .text( "Delete group" )
		                   )
		);

		//ViewElementGenerator<Object, com.foreach.across.modules.bootstrapui.elements.TableViewElement.Row>
		//		rowGenerator = new ViewElementGenerator<>();
		//rowGenerator.setItemTemplate( valueRow );
		//rowGenerator.setItems( page.setContent() );

		table.body()
		     .add(
				     bootstrapUi.generator( Object.class,
				                            com.foreach.across.modules.bootstrapui.elements.TableViewElement.Row.class )
				                .name( "__rows" )
				                .itemBuilder( valueRow )
		     );

		container.add( table );
	}

//	//@Override
//	protected void extendBlaViewModel( V viewCreationContext, final EntityListView view ) {
//		Pageable pageable = buildPageable( view );
//		Page page = getPageFetcher().fetchPage( viewCreationContext, pageable, view );
//
//		view.setPageable( pageable );
//		view.setPage( page );
//		view.setShowResultNumber( isShowResultNumber() );
//
//		ViewElementGenerator generator =
//				( (com.foreach.across.modules.web.ui.elements.ContainerViewElement) view.getAttribute( "newElements" ) )
//						.<com.foreach.across.modules.bootstrapui.elements.TableViewElement>get( "__tbl" )
//						.getBody().get( "__rows" );
//		generator.setItems( page.getContent() );
//
//		SortableTableHeaderCellProcessor sortableTableHeaderCellProcessor = new SortableTableHeaderCellProcessor();
//		sortableTableHeaderCellProcessor.setSortableProperties( sortableProperties );
//		sortableTableHeaderCellProcessor.setViewElementDescriptorMap(
//				(Map<ViewElement, EntityPropertyDescriptor>)
//						viewCreationContext.removeAttribute( "descriptorElementsMap" )
//		);
//
//		TableViewElement table = new TableViewElement();
//		table.getHeader().setCellProcessor( sortableTableHeaderCellProcessor );
//		table.setName( "resultsTable" );
//		table.setPage( page );
//		table.setShowResultNumber( isShowResultNumber() );
//		table.setColumns( (Iterable<ViewElement>) view.getEntityProperties().remove( "table" ) );
//
//		Map<String, String> tableAttributs = new HashMap<>();
//		tableAttributs.put( "data-tbl", "entity-list" );
//		tableAttributs.put( "data-tbl-type", "paged" );
//		tableAttributs.put( "data-tbl-entity-type", view.getEntityConfiguration().getName() );
//		tableAttributs.put( "data-tbl-current-page", "" + page.getNumber() );
//		tableAttributs.put( "data-tbl-size", "" + page.getSize() );
//		tableAttributs.put( "data-tbl-sort", "" + page.getSort() );
//
//		table.setAttributes( tableAttributs );
//
//		boolean hasListSummaryView = viewCreationContext.isForAssociation()
//				? viewCreationContext.getEntityAssociation().hasView( EntityListView.SUMMARY_VIEW_NAME )
//				: viewCreationContext.getEntityConfiguration().hasView( EntityListView.SUMMARY_VIEW_NAME );
//
//		if ( hasListSummaryView ) {
//			table.setRowProcessor( new TableRowProcessor()
//			{
//				@Override
//				public Map<String, String> attributes( Object entity ) {
//					return Collections.singletonMap(
//							"data-summary-url",
//
//							ServletUriComponentsBuilder
//									.fromCurrentContextPath()
//									.path( adminWeb.path( view.getEntityLinkBuilder().view( entity ) ) )
//									.queryParam( "view", EntityListView.SUMMARY_VIEW_NAME )
//									.queryParam( "_partial", "content" )
//									.toUriString()
//					);
//				}
//			} );
//
//			if ( viewCreationContext instanceof WebViewCreationContext ) {
//				( (WebViewCreationContext) viewCreationContext )
//						.getWebResourceRegistry()
//						.add( WebResource.JAVASCRIPT_PAGE_END, "/js/entity/expandable.js", WebResource.VIEWS );
//			}
//		}
//
//		AllowableActions allowableActions = viewCreationContext.getEntityConfiguration().getAllowableActions();
//		EntityMessages messages = view.getEntityMessages();
//
//		ContainerViewElement buttons = null;
//
//		if ( allowableActions.contains( AllowableAction.CREATE ) ) {
//			buttons = new ContainerViewElement( "buttons" );
//			buttons.setElementType( "paragraph" );
//
//			ButtonViewElement create = new ButtonViewElement();
//			create.setName( "btn-create" );
//			create.setElementType( CommonViewElements.LINK_BUTTON );
//			create.setLink( view.getEntityLinkBuilder().create() );
//			create.setLabel( messages.createAction() );
//			buttons.add( create );
//		}
//
//		if ( allowableActions.contains( AllowableAction.UPDATE ) ) {
//			ContainerViewElement itemButtons = new ContainerViewElement( "itemButtons" );
//
//			ButtonViewElement edit = new ButtonViewElement()
//			{
//				@Override
//				public String print( Object entity ) {
//					return view.getEntityLinkBuilder().update( entity );
//				}
//			};
//			edit.setName( "btn-edit" );
//			edit.setElementType( CommonViewElements.LINK_BUTTON );
//			edit.setStyle( ButtonViewElement.Style.ICON );
//			edit.setIcon( "edit" );
//			edit.setLabel( messages.updateAction() );
//
//			itemButtons.add( edit );
//
//			( (ViewElements) table.getColumns() ).add( itemButtons );
//		}
//
//		view.getEntityProperties().addFirst( table );
//
//		if ( buttons != null ) {
//			view.getEntityProperties().addFirst( buttons );
//		}
//	}

	private Pageable buildPageable( EntityListView view ) {
		Pageable existing = view.getPageable();

		if ( existing == null ) {
			existing = new PageRequest( 0, getPageSize(), getDefaultSort() );
		}

		return existing;
	}

	@Override
	protected EntityMessages createEntityMessages( EntityMessageCodeResolver codeResolver ) {
		return new ListViewEntityMessages( codeResolver );
	}
}
