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
package com.foreach.across.modules.entity.views.bootstrapui.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiFactory;
import com.foreach.across.modules.bootstrapui.elements.GlyphIcon;
import com.foreach.across.modules.bootstrapui.elements.Style;
import com.foreach.across.modules.bootstrapui.elements.TableViewElement;
import com.foreach.across.modules.bootstrapui.elements.builder.TableViewElementBuilder;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderService;
import com.foreach.across.modules.web.ui.*;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.*;

import static com.foreach.across.modules.entity.views.ViewElementMode.LIST_LABEL;
import static com.foreach.across.modules.entity.views.ViewElementMode.LIST_VALUE;

/**
 * Helper that aids in building a sortable {@link com.foreach.across.modules.bootstrapui.elements.TableViewElement}
 * for a list of {@link EntityPropertyDescriptor}s.
 *
 * @author Arne Vandamme
 */
public class SortableTableBuilder implements ViewElementBuilder<ViewElement>
{
	/**
	 * Sets an 'odd' or 'even' class on a table row depending on the iterator index.
	 */
	public static final ViewElementPostProcessor<TableViewElement.Row> CSS_ODD_EVEN_ROW_PROCESSOR
			= new ViewElementPostProcessor<TableViewElement.Row>()
	{
		@Override
		public void postProcess( ViewElementBuilderContext builderContext, TableViewElement.Row element ) {
			if ( builderContext instanceof IteratorViewElementBuilderContext ) {
				boolean even = ( ( (IteratorViewElementBuilderContext) builderContext ).getIndex() + 1 ) % 2 == 0;
				element.addCssClass( even ? "even" : "odd" );
			}
		}
	};
	public static String DATA_ATTR_FIELD = "data-tbl-field";
	public static String DATA_ATTR_TABLE_NAME = "data-tbl";
	public static String DATA_ATTR_TABLE_TYPE = "data-tbl-type";
	public static String DATA_ATTR_ENTITY_TYPE = "data-tbl-entity-type";
	public static String DATA_ATTR_CURRENT_PAGE = "data-tbl-current-page";
	public static String DATA_ATTR_PAGE = "data-tbl-page";
	public static String DATA_ATTR_PAGES = "data-tbl-total-pages";
	public static String DATA_ATTR_PAGE_SIZE = "data-tbl-size";
	public static String DATA_ATTR_SORT = "data-tbl-sort";
	public static String DATA_ATTR_SORT_PROPERTY = "data-tbl-sort-property";

	protected final EntityViewElementBuilderService viewElementBuilderService;
	protected final BootstrapUiFactory bootstrapUi;

	private String tableName = "sortableTable";
	private EntityConfiguration entityConfiguration;
	private Collection<String> sortableProperties;
	private Collection<EntityPropertyDescriptor> propertyDescriptors;
	private boolean tableOnly, showResultNumber = true;
	private Page<Object> page;
	private Style[] tableStyles = new Style[] { Style.Table.HOVER };
	private PagingMessages pagingMessages;
	private ViewElementBuilderSupport.ElementOrBuilder noResultsElement;
	private Collection<ViewElementPostProcessor<TableViewElement.Row>> headerRowProcessors = new ArrayList<>();
	private Collection<ViewElementPostProcessor<TableViewElement.Row>> valueRowProcessors = new ArrayList<>();

	@Autowired
	public SortableTableBuilder( EntityViewElementBuilderService viewElementBuilderService,
	                             BootstrapUiFactory bootstrapUi ) {
		this.viewElementBuilderService = viewElementBuilderService;
		this.bootstrapUi = bootstrapUi;
	}

	/**
	 * Set a custom no results view element to be returned
	 *
	 * @param element to be shown in case the page is empty
	 */
	public void setNoResultsElement( ViewElement element ) {
		this.noResultsElement = ViewElementBuilderSupport.ElementOrBuilder.wrap( element );
	}

	/**
	 * Set a custom no results view element to be returned if the data page is empty.
	 * If not set a default panel with text will be created.
	 *
	 * @param builder to be shown in case the page is empty
	 */
	public void setNoResultsElementBuilder( ViewElementBuilder builder ) {
		this.noResultsElement = ViewElementBuilderSupport.ElementOrBuilder.wrap( builder );
	}

	public void addHeaderRowProcessor( ViewElementPostProcessor<TableViewElement.Row> processor ) {
		headerRowProcessors.add( processor );
	}

	public void addValueRowProcessor( ViewElementPostProcessor<TableViewElement.Row> processor ) {
		valueRowProcessors.add( processor );
	}

	public String getTableName() {
		return tableName;
	}

	/**
	 * @param tableName to be used both internally and as data attribute on the resulting html table
	 */
	public void setTableName( String tableName ) {
		this.tableName = tableName;
	}

	public EntityConfiguration getEntityConfiguration() {
		return entityConfiguration;
	}

	public void setEntityConfiguration( EntityConfiguration entityConfiguration ) {
		this.entityConfiguration = entityConfiguration;
	}

	public Collection<EntityPropertyDescriptor> getPropertyDescriptors() {
		return propertyDescriptors;
	}

	public void setPropertyDescriptors( Collection<EntityPropertyDescriptor> propertyDescriptors ) {
		this.propertyDescriptors = propertyDescriptors;
	}

	public boolean isTableOnly() {
		return tableOnly;
	}

	/**
	 * @param tableOnly true if only the table should be returned (no panel)
	 */
	public void setTableOnly( boolean tableOnly ) {
		this.tableOnly = tableOnly;
	}

	public Page getPage() {
		return page;
	}

	/**
	 * @param page of data items to be shown
	 */
	@SuppressWarnings("unchecked")
	public void setPage( Page page ) {
		this.page = page;
	}

	public boolean isShowResultNumber() {
		return showResultNumber;
	}

	/**
	 * @param showResultNumber true if result number should be included
	 */
	public void setShowResultNumber( boolean showResultNumber ) {
		this.showResultNumber = showResultNumber;
	}

	public Style[] getTableStyles() {
		return tableStyles;
	}

	/**
	 * @param tableStyles that should be applied to the generated table
	 */
	public void setTableStyles( Style... tableStyles ) {
		this.tableStyles = tableStyles;
	}

	public PagingMessages getPagingMessages() {
		return pagingMessages;
	}

	/**
	 * @param pagingMessages to be used for the result text
	 */
	public void setPagingMessages( PagingMessages pagingMessages ) {
		this.pagingMessages = pagingMessages;
	}

	public Collection<String> getSortableProperties() {
		return sortableProperties;
	}

	/**
	 * Limit the properties that can be sorted on by specifiying them explicitly.  If the collection
	 * is null then all properties that have a {@link org.springframework.data.domain.Sort.Order} attribute will
	 * be sortable.
	 *
	 * @param sortableProperties collection of property names that can be sorted on
	 */
	public void setSortableProperties( Collection<String> sortableProperties ) {
		this.sortableProperties = sortableProperties;
	}

	public Collection<ViewElementPostProcessor<TableViewElement.Row>> getHeaderRowProcessors() {
		return Collections.unmodifiableCollection( headerRowProcessors );
	}

	public void setHeaderRowProcessors( Collection<ViewElementPostProcessor<TableViewElement.Row>> headerRowProcessors ) {
		this.headerRowProcessors.clear();
		this.headerRowProcessors.addAll( headerRowProcessors );
	}

	public Collection<ViewElementPostProcessor<TableViewElement.Row>> getValueRowProcessors() {
		return Collections.unmodifiableCollection( valueRowProcessors );
	}

	public void setValueRowProcessors( Collection<ViewElementPostProcessor<TableViewElement.Row>> valueRowProcessors ) {
		this.valueRowProcessors.clear();
		this.valueRowProcessors.addAll( valueRowProcessors );
	}

	/**
	 * Create a {@link ViewElement} containing the configured table.
	 *
	 * @param builderContext for element creation
	 * @return viewElement
	 */
	@Override
	public ViewElement build( ViewElementBuilderContext builderContext ) {
		if ( !page.hasContent() ) {
			if ( noResultsElement != null ) {
				return noResultsElement.get( builderContext );
			}

			return createDefaultNoResultsPanel().build( builderContext );
		}

		TableViewElementBuilder table = createTable();

		return ( isTableOnly() ? table : createPanelForTable( table ) ).build( builderContext );
	}

	protected TableViewElementBuilder createTable() {
		TableViewElementBuilder table = bootstrapUi.table()
		                                           .responsive()
		                                           .style( tableStyles )
		                                           .attributes( createTableAttributes() );

		createTableHeader( table );
		createTableBody( table );

		return table;
	}

	private Map<String, Object> createTableAttributes() {
		Page currentPage = getPage();

		Map<String, Object> attributes = new HashMap<>();
		attributes.put( DATA_ATTR_TABLE_NAME, getTableName() );
		attributes.put( DATA_ATTR_TABLE_TYPE, "paged" );
		attributes.put( DATA_ATTR_ENTITY_TYPE, getEntityConfiguration().getName() );
		attributes.put( DATA_ATTR_CURRENT_PAGE, currentPage.getNumber() );
		attributes.put( DATA_ATTR_PAGES, currentPage.getTotalPages() );
		attributes.put( DATA_ATTR_PAGE_SIZE, currentPage.getSize() );
		attributes.put( DATA_ATTR_SORT, convertSortAttribute( currentPage.getSort() ) );

		return attributes;
	}

	protected List<OrderPair> convertSortAttribute( Sort sort ) {
		if ( sort == null ) {
			return null;
		}

		List<OrderPair> orderPairs = new ArrayList<>();

		for ( Sort.Order order : sort ) {
			orderPairs.add( new OrderPair( order.getProperty(), order.getDirection().name() ) );
		}

		return orderPairs;
	}

	protected void createTableHeader( TableViewElementBuilder table ) {
		TableViewElementBuilder.Row headerRow = table.row();

		if ( isShowResultNumber() ) {
			headerRow.add(
					table.heading()
					     .attribute( "class", "result-number" )
					     .text( "#" )
			);
		}

		for ( EntityPropertyDescriptor descriptor : getPropertyDescriptors() ) {
			TableViewElementBuilder.Cell heading = table.heading()
			                                            .attribute( DATA_ATTR_FIELD, descriptor.getName() )
			                                            .add( createLabel( descriptor ) );

			String sortsOn = determineSortableProperty( descriptor );

			if ( sortsOn != null ) {
				heading.attribute( "class", "sortable" )
				       .attribute( DATA_ATTR_SORT_PROPERTY, sortsOn )
				       .attribute( DATA_ATTR_TABLE_NAME, getTableName() );
			}

			headerRow.add( heading );
		}

		for ( ViewElementPostProcessor<TableViewElement.Row> postProcessor : getHeaderRowProcessors() ) {
			headerRow.postProcessor( postProcessor );
		}

		table.header().add( headerRow );
	}

	protected String determineSortableProperty( EntityPropertyDescriptor descriptor ) {
		if ( sortableProperties == null || sortableProperties.contains( descriptor.getName() ) ) {
			Sort.Order order = descriptor.getAttribute( Sort.Order.class );
			return order != null ? order.getProperty() : null;
		}

		return null;
	}

	protected void createTableBody( TableViewElementBuilder table ) {
		TableViewElementBuilder.Row valueRow = table.row()
		                                            .postProcessor( CSS_ODD_EVEN_ROW_PROCESSOR );

		if ( isShowResultNumber() ) {
			int startIndex = Math.max( 0, page.getNumber() ) * page.getSize();
			valueRow.add(
					table.cell()
					     .attribute( "class", "result-number" )
					     .add(
							     bootstrapUi.text().postProcessor( new ResultNumberProcessor( startIndex ) )
					     )
			);
		}

		for ( EntityPropertyDescriptor descriptor : getPropertyDescriptors() ) {
			ViewElementBuilder valueBuilder = createValue( descriptor );

			TableViewElementBuilder.Cell cell = table.cell()
			                                         .attribute( DATA_ATTR_FIELD, descriptor.getName() );

			if ( valueBuilder != null ) {
				cell.add( valueBuilder );
			}

			valueRow.add( cell );
		}

		for ( ViewElementPostProcessor<TableViewElement.Row> postProcessor : getValueRowProcessors() ) {
			valueRow.postProcessor( postProcessor );
		}

		table.body()
		     .add(
				     bootstrapUi.generator( Object.class, TableViewElement.Row.class )
				                .itemBuilder( valueRow )
				                .items( page.getContent() )
		     );
	}

	protected ViewElementBuilder createLabel( EntityPropertyDescriptor descriptor ) {
		return viewElementBuilderService.getElementBuilder( entityConfiguration, descriptor, LIST_LABEL );
	}

	protected ViewElementBuilder createValue( EntityPropertyDescriptor descriptor ) {
		return viewElementBuilderService.getElementBuilder( entityConfiguration, descriptor, LIST_VALUE );
	}

	protected ViewElementBuilder createPanelForTable( TableViewElementBuilder tableBody ) {
		NodeViewElementBuilder panel = bootstrapUi.node( "div" )
		                                          .attribute( "class", "panel panel-default" )
		                                          .add(
				                                          bootstrapUi.node( "div" )
				                                                     .attribute( "class", "panel-heading" )
				                                                     .add( bootstrapUi.text(
						                                                     pagingMessages.resultsFound(
								                                                     getPage() ) ) )
		                                          )
		                                          .add(
				                                          bootstrapUi.node( "div" )
				                                                     .attribute( "class", "panel-body" )
				                                                     .add( tableBody )
		                                          );

		if ( page.getTotalPages() > 1 ) {
			panel.add(
					bootstrapUi.node( "div" )
					           .attribute( "class", "panel-footer" )
					           .add( createPager() )
			);
		}
		return panel;
	}

	protected ViewElementBuilder createDefaultNoResultsPanel() {
		return bootstrapUi.node( "div" )
		                  .attribute( "class", "panel panel-warning" )
		                  .add(
				                  bootstrapUi.node( "div" )
				                             .attribute( "class", "panel-body" )
				                             .add( bootstrapUi.text( getPagingMessages().resultsFound( getPage() ) ) )
		                  );
	}

	protected ViewElementBuilder createPager() {
		Page currentPage = getPage();
		PagingMessages messages = getPagingMessages();

		NodeViewElementBuilder pager = bootstrapUi.node( "div" )
		                                          .attribute( "class", "pager-form form-inline text-center" );

		if ( currentPage.hasPrevious() ) {
			pager.add(
					bootstrapUi.button()
					           .link( "#" )
					           .icon( new GlyphIcon( GlyphIcon.STEP_BACKWARD ) )
					           .title( messages.previousPage( currentPage ) )
					           .attribute( DATA_ATTR_PAGE, currentPage.getNumber() - 1 )
					           .attribute( DATA_ATTR_TABLE_NAME, getTableName() )
			);
		}
		else {
			pager.add( bootstrapUi.node( "span" ).attribute( "class", "no-btn" ) );
		}

		pager.add(
				bootstrapUi.label()
				           .add( bootstrapUi.node( "span" ).add( bootstrapUi.text( messages.page( currentPage ) ) ) )
				           .add(
						           bootstrapUi.textbox()
						                      .attribute( "data-tbl-page-selector", "selector" )
						                      .attribute( DATA_ATTR_TABLE_NAME, getTableName() )
						                      .text( String.valueOf( currentPage.getNumber() + 1 ) )
				           )
		)
		     .add( bootstrapUi.node( "span" ).add( bootstrapUi.text( messages.ofPages( currentPage ) ) ) )
		     .add(
				     bootstrapUi.node( "a" )
				                .attribute( "href", "#" )
				                .attribute( "class", "total-pages-link" )
				                .attribute( DATA_ATTR_PAGE, currentPage.getTotalPages() - 1 )
				                .attribute( DATA_ATTR_TABLE_NAME, getTableName() )
				                .add( bootstrapUi.text( String.valueOf( currentPage.getTotalPages() ) ) )
		     );

		if ( currentPage.hasNext() ) {
			pager.add(
					bootstrapUi.button()
					           .link( "#" )
					           .icon( new GlyphIcon( GlyphIcon.STEP_FORWARD ) )
					           .title( messages.nextPage( currentPage ) )
					           .attribute( DATA_ATTR_PAGE, currentPage.getNumber() + 1 )
					           .attribute( DATA_ATTR_TABLE_NAME, getTableName() )
			);
		}
		else {
			pager.add( bootstrapUi.node( "span" ).attribute( "class", "no-btn" ) );
		}

		return pager;
	}

	/**
	 * Simple class for JSON serializing sortable properties.
	 */
	public static class OrderPair
	{
		@JsonProperty(value = "prop")
		private String property;

		@JsonProperty(value = "dir")
		private String direction;

		public OrderPair( String property, String direction ) {
			this.property = property;
			this.direction = direction;
		}

		public String getProperty() {
			return property;
		}

		public String getDirection() {
			return direction;
		}
	}

	/**
	 * Sets the position of the item being processed (in an
	 * {@link IteratorViewElementBuilderContext}) as the
	 * text of a {@link TextViewElement}.
	 */
	public static class ResultNumberProcessor implements ViewElementPostProcessor<TextViewElement>
	{
		private final int startIndex;

		public ResultNumberProcessor( int startIndex ) {
			this.startIndex = startIndex;
		}

		@Override
		public void postProcess( ViewElementBuilderContext builderContext, TextViewElement element ) {
			if ( builderContext instanceof IteratorViewElementBuilderContext ) {
				IteratorViewElementBuilderContext ctx = (IteratorViewElementBuilderContext) builderContext;
				element.setText( String.valueOf( ctx.getIndex() + 1 + startIndex ) );
			}
		}
	}
}
