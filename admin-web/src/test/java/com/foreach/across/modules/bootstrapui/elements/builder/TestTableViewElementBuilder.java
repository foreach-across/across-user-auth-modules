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
package com.foreach.across.modules.bootstrapui.elements.builder;

import com.foreach.across.modules.bootstrapui.elements.Style;
import com.foreach.across.modules.bootstrapui.elements.TableViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderFactory;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.test.support.AbstractViewElementBuilderTest;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * @author Arne Vandamme
 */
public class TestTableViewElementBuilder extends AbstractViewElementBuilderTest<TableViewElementBuilder, TableViewElement>
{
	@Override
	protected TableViewElementBuilder createBuilder( ViewElementBuilderFactory builderFactory ) {
		return new TableViewElementBuilder();
	}

	@Test
	public void emptyTable() {
		build();

		assertFalse( element.isResponsive() );
		assertNull( element.getHeader() );
		assertNull( element.getBody() );
		assertNull( element.getCaption() );
		assertNull( element.getFooter() );
		assertTrue( element.getStyles().isEmpty() );
	}

	@Test
	public void styles() {
		builder.style( Style.Table.BORDERED, Style.Table.STRIPED ).style( Style.Table.CONDENSED );

		build();

		assertEquals( new HashSet<>( Arrays.asList(
				              Style.Table.BORDERED, Style.Table.STRIPED, Style.Table.CONDENSED
		              ) ),
		              element.getStyles() );
	}

	@Test
	public void headerFooterAndBody() {
		assertNotNull( builder.header() );
		assertNotNull( builder.body() );
		assertNotNull( builder.footer() );

		build();

		assertNotNull( element.getHeader() );
		assertNotNull( element.getFooter() );
		assertNotNull( element.getBody() );
	}

	@Test
	public void detachedHeaderFooterAndBody() {
		assertNotNull( TableViewElementBuilder.createHeader() );
		assertNotNull( TableViewElementBuilder.createBody() );
		assertNotNull( TableViewElementBuilder.createFooter() );

		build();

		assertNull( element.getHeader() );
		assertNull( element.getFooter() );
		assertNull( element.getBody() );
	}

	@Test
	public void rowsAndCellsAreDetached() {
		assertNotNull( builder.row() );
		assertNotNull( builder.heading() );
		assertNotNull( builder.cell() );

		build();

		assertNull( element.getBody() );
		assertTrue( element.isEmpty() );
	}

	@Test
	public void caption() {
		assertNotNull( builder.caption() );

		build();

		assertNotNull( element.getCaption() );
	}

	@Test
	public void captionWithText() {
		assertSame( builder, builder.caption( "Simple caption text" ) );

		build();

		assertNotNull( element.getCaption() );
		assertEquals( "Simple caption text", element.getCaption().getText() );
	}

	@Test
	public void detachedCaption() {
		assertNotNull( TableViewElementBuilder.createCaption() );

		build();

		assertNull( element.getCaption() );
	}

	@Test
	public void commonTable() {
		TableViewElementBuilder table = builder;
		table.responsive();
		table.caption( "Table caption" );
		table.header()
		     .add(
				     table.row()
				          .name( "headerRow" )
				          .add( table.heading().name( "one" ).text( "Heading 1" ) )
				          .add( table.heading().name( "two" ).text( "Heading 2" ) )
		     );
		table.body()
		     .add(
				     table.row().add( table.cell().name( "rowOne" ).text( "row 1 - cell" ) )
		     )
		     .add(
				     table.row().add( table.cell().name( "rowTwo" ).text( "row 2 - cell" ) )
		     );

		build();

		assertEquals( "Table caption", element.getCaption().getText() );

		assertTrue( element.isResponsive() );
		assertEquals( 1, element.getHeader().size() );
		assertEquals( 2, element.getBody().size() );

		TableViewElement.Cell cell = element.getHeader().<ContainerViewElement>get( "headerRow" ).get( "one" );
		assertNotNull( cell );
		assertEquals( "Heading 1", cell.getText() );
		assertTrue( cell.isHeading() );

		cell = element.getHeader().get( "two" );
		assertNotNull( cell );
		assertEquals( "Heading 2", cell.getText() );
		assertTrue( cell.isHeading() );

		cell = element.getBody().get( "rowOne" );
		assertNotNull( cell );
		assertEquals( "row 1 - cell", cell.getText() );
		assertFalse( cell.isHeading() );

		cell = element.getBody().get( "rowTwo" );
		assertNotNull( cell );
		assertEquals( "row 2 - cell", cell.getText() );
		assertFalse( cell.isHeading() );
	}
}
