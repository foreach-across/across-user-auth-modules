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
package com.foreach.across.modules.entity.newviews.bootstrapui.util;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.modules.bootstrapui.BootstrapUiModule;
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiFactory;
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiFactoryImpl;
import com.foreach.across.modules.bootstrapui.elements.Style;
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.config.ViewHelpers;
import com.foreach.across.modules.entity.newviews.EntityViewElementBuilderService;
import com.foreach.across.modules.entity.newviews.ViewElementMode;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementBuilder;
import com.foreach.across.modules.web.ui.elements.builder.TextViewElementBuilder;
import com.foreach.across.test.support.AbstractViewElementTemplateTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
@ContextConfiguration(classes = TestSortableTableBuilder.Config.class)
public class TestSortableTableBuilder extends AbstractViewElementTemplateTest
{
	private static final String TABLE_WITH_RESULT_NUMBER = "<div class='table-responsive'>" +
			"<table class='table table-hover' " +
			"data-tbl='sortableTable' data-tbl-type='paged' data-tbl-entity-type='entity' " +
			"data-tbl-current-page='0' data-tbl-total-pages='1' data-tbl-size='0'>" +
			"<thead>" +
			"<tr><th class='result-number'>#</th><th data-tbl-field='propertyOne'>Property name</th></tr>" +
			"</thead>" +
			"<tbody>" +
			"<tr class='odd'><td class='result-number'>1</td><td data-tbl-field='propertyOne'>Property value</td></tr>" +
			"</tbody>" +
			"</table>" +
			"</div>";

	private static final String TABLE_WITHOUT_RESULT_NUMBER = "<div class='table-responsive'>" +
			"<table class='table table-hover' " +
			"data-tbl='sortableTable' data-tbl-type='paged' data-tbl-entity-type='entity' " +
			"data-tbl-current-page='0' data-tbl-total-pages='1' data-tbl-size='0'>" +
			"<thead>" +
			"<tr><th data-tbl-field='propertyOne'>Property name</th></tr>" +
			"</thead>" +
			"<tbody>" +
			"<tr class='odd'><td data-tbl-field='propertyOne'>Property value</td></tr>" +
			"</tbody>" +
			"</table>" +
			"</div>";

	@Autowired
	private ViewHelpers viewHelpers;

	@Autowired
	private EntityViewElementBuilderService viewElementBuilderService;

	private EntityConfiguration entityConfiguration;
	private SortableTableBuilder tableBuilder;
	private EntityPropertyDescriptor descriptor;

	@Before
	public void before() {
		reset( viewElementBuilderService );

		tableBuilder = viewHelpers.createSortableTableBuilder();

		entityConfiguration = mock( EntityConfiguration.class );
		when( entityConfiguration.getName() ).thenReturn( "entity" );

		descriptor = mock( EntityPropertyDescriptor.class );

		tableBuilder.setEntityConfiguration( entityConfiguration );
		tableBuilder.setSortableProperties( Collections.<String>emptyList() );
		tableBuilder.setPropertyDescriptors( Collections.singleton( descriptor ) );

		Page page = new PageImpl<>( Collections.singletonList( "test" ) );
		tableBuilder.setPage( page );

		PagingMessages messages = mock( PagingMessages.class );
		when( messages.resultsFound( any( Page.class ), anyVararg() ) ).thenReturn( "xx results" );
		tableBuilder.setPagingMessages( messages );

		when( descriptor.getName() ).thenReturn( "propertyOne" );
		when( descriptor.getAttribute( EntityAttributes.SORTABLE_PROPERTY, String.class ) ).thenReturn( "sortOnMe" );

		when( viewElementBuilderService.getElementBuilder( entityConfiguration, descriptor,
		                                                   ViewElementMode.LIST_LABEL ) )
				.thenReturn( new TextViewElementBuilder().text( "Property name" ) );

		when( viewElementBuilderService.getElementBuilder( entityConfiguration, descriptor,
		                                                   ViewElementMode.LIST_VALUE ) )
				.thenReturn( new TextViewElementBuilder().text( "Property value" ) );
	}

	@Test
	public void tableOnly() {
		tableBuilder.setTableOnly( true );

		expect( TABLE_WITH_RESULT_NUMBER );
	}

	@Test
	public void simpleTable() {
		expect(
				"<div class='panel panel-default'>" +
						"<div class='panel-heading'>xx results</div>" +
						"<div class='panel-body'>" +
						TABLE_WITH_RESULT_NUMBER +
						"</div>" +
						"</div>"
		);
	}

	@Test
	public void noResultNumberTable() {
		tableBuilder.setShowResultNumber( false );

		expect(
				"<div class='panel panel-default'>" +
						"<div class='panel-heading'>xx results</div>" +
						"<div class='panel-body'>" +
						TABLE_WITHOUT_RESULT_NUMBER +
						"</div>" +
						"</div>"
		);
	}

	@Test
	@Ignore
	public void secondPageResults() {
		Pageable pageable = new PageRequest( 1, 20 );
		Page page = new PageImpl<>( Arrays.asList( "één", "twee" ), pageable, 57 );

		tableBuilder.setTableName( "entityList" );
		tableBuilder.setPage( page );

		expect(
				"<div class='panel panel-default'>" +
						"<div class='panel-heading'>xx results</div>" +
						"<div class='panel-body'>" +
						"<div class='table-responsive'>" +
						"<table class='table table-hover' " +
						"data-tbl='entityList' data-tbl-type='paged' data-tbl-entity-type='entity' " +
						"data-tbl-current-page='1' data-tbl-total-pages='3' data-tbl-size='20'>" +
						"<thead>" +
						"<tr><th class='result-number'>#</th><th data-tbl-field='propertyOne'>Property name</th></tr>" +
						"</thead>" +
						"<tbody>" +
						"<tr class='odd'><td class='result-number'>21</td><td data-tbl-field='propertyOne'>Property value</td></tr>" +
						"<tr class='even'><td class='result-number'>22</td><td data-tbl-field='propertyOne'>Property value</td></tr>" +
						"</tbody>" +
						"</table>" +
						"</div>" +
						"</div>" +
						"</div>"
		);
	}

	@Test
	public void defaultNoResultsPanel() {
		Page page = new PageImpl<>( Collections.emptyList() );
		tableBuilder.setPage( page );

		PagingMessages messages = mock( PagingMessages.class );
		when( messages.resultsFound( any( Page.class ), anyVararg() ) ).thenReturn( "Geen resultaten gevonden" );

		tableBuilder.setPagingMessages( messages );

		expect( "<div class='panel panel-warning'>" +
				        "<div class='panel-body'>Geen resultaten gevonden</div>" +
				        "</div>" );
	}

	@Test
	public void customNoResultsElement() {
		Page page = new PageImpl<>( Collections.emptyList() );
		tableBuilder.setPage( page );

		tableBuilder.setNoResultsElement( new TextViewElement( "empty" ) );
		expect( "empty" );

		tableBuilder.setNoResultsElementBuilder(
				new NodeViewElementBuilder()
						.tagName( "div" )
						.add( new TextViewElement( "empty" ) )
		);
		expect( "<div>empty</div>" );
	}

	@Test
	public void sorting() {
		tableBuilder.setTableOnly( true );
		tableBuilder.setSortableProperties( Arrays.asList( "propertyOne", "propertyTwo" ) );

		expect( "<div class='table-responsive'>" +
				        "<table class='table table-hover' " +
				        "data-tbl='sortableTable' data-tbl-type='paged' data-tbl-entity-type='entity' " +
				        "data-tbl-current-page='0' data-tbl-total-pages='1' data-tbl-size='0'>" +
				        "<thead>" +
				        "<tr><th class='result-number'>#</th>" +
				        "<th data-tbl-field='propertyOne' class='sortable' data-tbl-sort-property='sortOnMe' data-tbl='sortableTable'>" +
				        "Property name</th></tr>" +
				        "</thead>" +
				        "<tbody>" +
				        "<tr class='odd'><td class='result-number'>1</td><td data-tbl-field='propertyOne'>Property value</td></tr>" +
				        "</tbody>" +
				        "</table>" +
				        "</div>"
		);

		tableBuilder.setSortableProperties( null );

		expect( "<div class='table-responsive'>" +
				        "<table class='table table-hover' " +
				        "data-tbl='sortableTable' data-tbl-type='paged' data-tbl-entity-type='entity' " +
				        "data-tbl-current-page='0' data-tbl-total-pages='1' data-tbl-size='0'>" +
				        "<thead>" +
				        "<tr><th class='result-number'>#</th>" +
				        "<th data-tbl-field='propertyOne' class='sortable' data-tbl-sort-property='sortOnMe' data-tbl='sortableTable'>" +
				        "Property name</th></tr>" +
				        "</thead>" +
				        "<tbody>" +
				        "<tr class='odd'><td class='result-number'>1</td><td data-tbl-field='propertyOne'>Property value</td></tr>" +
				        "</tbody>" +
				        "</table>" +
				        "</div>"
		);
	}

	@Test
	public void tableStyle() {
		tableBuilder.setTableStyles( Style.Table.CONDENSED );
		tableBuilder.setTableOnly( true );

		expect(
				TABLE_WITH_RESULT_NUMBER.replace( "table-hover", "table-condensed" )
		);
	}

	@Test
	public void panelStyle() {

	}

	private void expect( String output ) {
		ViewElementBuilderContext ctx = mock( ViewElementBuilderContext.class );

		renderAndExpect( tableBuilder.build( ctx ), output );
	}

	@Configuration
	protected static class Config implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			context.addModule( new BootstrapUiModule() );
		}

		@Bean
		public EntityViewElementBuilderService viewElementBuilderService() {
			return mock( EntityViewElementBuilderService.class );
		}

		@Bean
		public BootstrapUiFactory bootstrapUiFactory() {
			return new BootstrapUiFactoryImpl();
		}

		@Bean
		public ViewHelpers viewHelpers() {
			return new ViewHelpers();
		}
	}
}
