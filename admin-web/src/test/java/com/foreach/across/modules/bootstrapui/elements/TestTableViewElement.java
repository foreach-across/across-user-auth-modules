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
package com.foreach.across.modules.bootstrapui.elements;

import com.foreach.across.modules.web.ui.elements.TextViewElement;
import org.junit.Test;

import java.util.Collections;

/**
 * @author Arne Vandamme
 */
public class TestTableViewElement extends AbstractBootstrapViewElementTest
{
	@Test
	public void simple() {
		TableViewElement table = new TableViewElement();

		renderAndExpect(
				table,
				"<table class='table' />"
		);
	}

	@Test
	public void responsive() {
		TableViewElement table = new TableViewElement();
		table.setAttribute( "data-test", "test" );
		table.setResponsive( true );

		renderAndExpect(
				table,
		        "<div class='table-responsive'><table class='table' data-test='test'></table></div>"
		);
	}

	@Test
	public void styles() {
		TableViewElement table = new TableViewElement();
		table.addStyle( Style.Table.CONDENSED );

		renderAndExpect(
				table,
				"<table class='table table-condensed' />"
		);

		table.addStyle( Style.Table.HOVER );
		renderAndExpect(
				table,
				"<table class='table table-condensed table-hover' />"
		);

		table.clearStyles();
		table.setStyles( Collections.singleton( Style.Table.STRIPED ) );
		renderAndExpect(
				table,
				"<table class='table table-striped' />"
		);
	}

	@Test
	public void rowsAsChildrenOfTable() {
		TableViewElement table = new TableViewElement();

		TableViewElement.Row headerRow = new TableViewElement.Row();
		headerRow.add( new TableViewElement.Cell() );

		table.add( row( heading( "heading 1" ), heading( "heading 2" ) ) );
		table.add( row( cell( "one" ), cell( "two" ) ) );

		TableViewElement.Cell warning = cell( "three" );
		warning.setStyle( Style.TableCell.WARNING );

		table.add( row( warning, cell( "four" ) ) );

		TableViewElement.Cell doubleCell = cell( "five" );
		doubleCell.setColumnSpan( 2 );

		TableViewElement.Row activeRow = row( doubleCell );
		activeRow.setStyle( Style.ACTIVE );

		table.add( activeRow );

		renderAndExpect(
				table,
				"<table class='table'>" +
						"<tr><th>heading 1</th><th>heading 2</th></tr>" +
						"<tr><td>one</td><td>two</td></tr>" +
						"<tr><td class='warning'>three</td><td>four</td></tr>" +
						"<tr class='active'><td colspan='2'>five</td></tr>" +
						"</table>"
		);
	}

	@Test
	public void manualCaptionHeaderBodyFooterAndColGroup() {
		TableViewElement table = new TableViewElement();
		table.add( caption( "table caption" ) );
		table.add( header( row( heading( "heading 1" ), heading( "heading 2" ) ) ) );
		table.add( body( row( cell( "one" ), cell( "two" ) ) ) );
		table.add( footer() );
		table.add( colgroup() );

		renderAndExpect(
				table,
				"<table class='table'>" +
						"<caption>table caption</caption>" +
						"<thead>" +
						"<tr><th>heading 1</th><th>heading 2</th></tr>" +
						"</thead>" +
						"<tbody>" +
						"<tr><td>one</td><td>two</td></tr>" +
						"</tbody>" +
						"<tfoot/>" +
						"<colgroup><col span='2' class='column-class'/></colgroup>" +
						"</table>"
		);
	}

	@Test
	public void captionHeaderBodyFooterAndColGroupAsProperties() {
		TableViewElement table = new TableViewElement();
		table.setCaption( caption( "table caption" ) );
		table.setHeader( header( row( heading( "heading 1" ), heading( "heading 2" ) ) ) );
		table.setBody( body( row( cell( "one" ), cell( "two" ) ) ) );
		table.setFooter( footer( row( cell( "three" ), cell( "four" ) ) ) );
		table.setColumnGroup( colgroup() );

		renderAndExpect(
				table,
				"<table class='table'>" +
						"<caption>table caption</caption>" +
						"<colgroup><col span='2' class='column-class'/></colgroup>" +
						"<thead>" +
						"<tr><th>heading 1</th><th>heading 2</th></tr>" +
						"</thead>" +
						"<tbody>" +
						"<tr><td>one</td><td>two</td></tr>" +
						"</tbody>" +
						"<tfoot>" +
						"<tr><td>three</td><td>four</td></tr>" +
						"</tfoot>" +
						"</table>"
		);

	}

	private TableViewElement.ColumnGroup colgroup() {
		TableViewElement.ColumnGroup columnGroup = new TableViewElement.ColumnGroup();
		TableViewElement.ColumnGroup.Column column = new TableViewElement.ColumnGroup.Column();
		column.setSpan( 2 );
		column.setAttribute( "class", "column-class" );

		columnGroup.add( column );

		return columnGroup;
	}

	private TableViewElement.Caption caption( String text ) {
		TableViewElement.Caption caption = new TableViewElement.Caption();
		caption.setText( text );

		return caption;
	}

	private TableViewElement.Header header( TableViewElement.Row... rows ) {
		TableViewElement.Header header = new TableViewElement.Header();

		for ( TableViewElement.Row row : rows ) {
			header.add( row );
		}

		return header;
	}

	private TableViewElement.Footer footer( TableViewElement.Row... rows ) {
		TableViewElement.Footer footer = new TableViewElement.Footer();

		for ( TableViewElement.Row row : rows ) {
			footer.add( row );
		}

		return footer;
	}

	private TableViewElement.Body body( TableViewElement.Row... rows ) {
		TableViewElement.Body body = new TableViewElement.Body();

		for ( TableViewElement.Row row : rows ) {
			body.add( row );
		}

		return body;
	}

	private TableViewElement.Row row( TableViewElement.Cell... cells ) {
		TableViewElement.Row row = new TableViewElement.Row();

		for ( TableViewElement.Cell cell : cells ) {
			row.add( cell );
		}

		return row;
	}

	private TableViewElement.Cell heading( String text ) {
		TableViewElement.Cell cell = new TableViewElement.Cell();
		cell.setHeading( true );
		cell.setText( text );

		return cell;
	}

	private TableViewElement.Cell cell( String text ) {
		TableViewElement.Cell cell = new TableViewElement.Cell();
		cell.add( new TextViewElement( text ) );

		return cell;
	}
}
