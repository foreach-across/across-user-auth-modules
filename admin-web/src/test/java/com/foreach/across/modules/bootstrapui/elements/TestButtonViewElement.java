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

import org.junit.Test;

/**
 * @author Arne Vandamme
 */
public class TestButtonViewElement extends AbstractBootstrapViewElementTest
{
	@Test
	public void buttonTypes() {
		ButtonViewElement button = new ButtonViewElement();
		button.setText( "Link button" );

		renderAndExpect( button, "<button type='button' class='btn btn-default'>Link button</button>" );

		button.setType( ButtonViewElement.Type.BUTTON_SUBMIT );
		renderAndExpect( button, "<button type='submit' class='btn btn-default'>Link button</button>" );

		button.setType( ButtonViewElement.Type.BUTTON_RESET );
		renderAndExpect( button, "<button type='reset' class='btn btn-default'>Link button</button>" );

		button.setType( ButtonViewElement.Type.INPUT );
		renderAndExpect( button, "<input type='button' class='btn btn-default' value='Link button' />" );

		button.setType( ButtonViewElement.Type.INPUT_SUBMIT );
		renderAndExpect( button, "<input type='submit' class='btn btn-default' value='Link button' />" );

		button.setType( ButtonViewElement.Type.INPUT_RESET );
		renderAndExpect( button, "<input type='reset' class='btn btn-default' value='Link button' />" );

		button.setType( ButtonViewElement.Type.LINK );
		renderAndExpect( button, "<a class='btn btn-default' href='#' role='button'>Link button</a>" );
	}

	@Test
	public void buttonStyles() {
		ButtonViewElement button = new ButtonViewElement();
		button.setText( "Link button" );

		button.setStyle( Style.Button.DANGER );
		renderAndExpect( button, "<button type='button' class='btn btn-danger'>Link button</button>" );

		button.setStyle( new Style( "custom-style" ) );
		renderAndExpect( button, "<button type='button' class='btn custom-style'>Link button</button>" );

		button.setType( ButtonViewElement.Type.INPUT );
		button.setStyle( Style.Button.PRIMARY );
		renderAndExpect( button, "<input type='button' class='btn btn-primary' value='Link button' />" );

		button.setType( ButtonViewElement.Type.LINK );
		button.setStyle( Style.Button.LINK );
		renderAndExpect( button, "<a class='btn btn-link' href='#' role='button'>Link button</a>" );
	}

	@Test
	public void buttonSizes() {
		ButtonViewElement button = new ButtonViewElement();
		button.setText( "Link button" );
		button.setStyle( Style.Button.DANGER );

		button.setSize( Size.LARGE );
		renderAndExpect( button, "<button type='button' class='btn btn-danger btn-lg'>Link button</button>" );

		button.setType( ButtonViewElement.Type.INPUT );
		button.setSize( Size.EXTRA_SMALL );
		renderAndExpect( button, "<input type='button' class='btn btn-danger btn-xs' value='Link button' />" );

		button.setType( ButtonViewElement.Type.LINK );
		button.setSize( Size.SMALL );
		renderAndExpect( button, "<a class='btn btn-danger btn-sm' href='#' role='button'>Link button</a>" );

		button.setSize( Size.DEFAULT );
		renderAndExpect( button, "<a class='btn btn-danger' href='#' role='button'>Link button</a>" );

		button.setSize( null );
		renderAndExpect( button, "<a class='btn btn-danger' href='#' role='button'>Link button</a>" );
	}

	@Test
	public void blockLevel() {
		ButtonViewElement button = new ButtonViewElement();
		button.setText( "Link button" );
		button.setStyle( Style.Button.DANGER );

		button.setSize( Size.LARGE.asBlock() );
		renderAndExpect( button, "<button type='button' class='btn btn-danger btn-lg btn-block'>Link button</button>" );

		button.setType( ButtonViewElement.Type.INPUT );
		button.setSize( Size.EXTRA_SMALL.asBlock() );
		renderAndExpect( button,
		                 "<input type='button' class='btn btn-danger btn-xs btn-block' value='Link button' />" );

		button.setType( ButtonViewElement.Type.LINK );
		button.setSize( Size.SMALL.asBlock() );
		renderAndExpect( button, "<a class='btn btn-danger btn-sm btn-block' href='#' role='button'>Link button</a>" );

		button.setSize( Size.BLOCK );
		renderAndExpect( button, "<a class='btn btn-danger btn-block' href='#' role='button'>Link button</a>" );

		button.setSize( Size.BLOCK.asBlock() );
		renderAndExpect( button, "<a class='btn btn-danger btn-block' href='#' role='button'>Link button</a>" );
	}

	@Test
	public void activeState() {
		ButtonViewElement button = new ButtonViewElement();
		button.setText( "active button" );
		button.setState( ButtonViewElement.State.ACTIVE );

		renderAndExpect( button, "<button type='button' class='btn btn-default active'>active button</button>" );

		button.setType( ButtonViewElement.Type.INPUT );
		renderAndExpect( button, "<input type='button' class='btn btn-default active' value='active button' />" );

		button.setType( ButtonViewElement.Type.LINK );
		renderAndExpect( button, "<a class='btn btn-default active' href='#' role='button'>active button</a>" );
	}

	@Test
	public void disabledState() {
		ButtonViewElement button = new ButtonViewElement();
		button.setText( "disabled button" );
		button.setState( ButtonViewElement.State.DISABLED );

		renderAndExpect( button,
		                 "<button type='button' class='btn btn-default' disabled='disabled'>disabled button</button>" );

		button.setType( ButtonViewElement.Type.INPUT );
		renderAndExpect( button,
		                 "<input type='button' class='btn btn-default' disabled='disabled' value='disabled button' />" );

		button.setType( ButtonViewElement.Type.LINK );
		renderAndExpect( button, "<a class='btn btn-default disabled' href='#' role='button'>disabled button</a>" );
	}

	@Test
	public void url() {
		ButtonViewElement button = new ButtonViewElement();
		button.setText( "click me" );
		button.setUrl( "http://go-somewhere.com" );

		renderAndExpect(
				button,
				"<button type='button' data-url='http://go-somewhere.com' class='btn btn-default'>click me</button>"
		);

		button.setType( ButtonViewElement.Type.LINK );
		renderAndExpect( button,
		                 "<a class='btn btn-default' href='http://go-somewhere.com' role='button'>click me</a>" );
	}

	@Test
	public void icon() {
		ButtonViewElement button = new ButtonViewElement();
		button.setText( " icon button" );
		button.setIcon( new GlyphIcon( GlyphIcon.BARCODE ) );

		renderAndExpect( button,
		                 "<button type='button' class='btn btn-default'>" +
				                 "<span class='glyphicon glyphicon-barcode' aria-hidden='true'></span> icon button" +
				                 "</button>" );

		button.setTitle( "icon button" );
		button.setText( null );

		renderAndExpect( button,
		                 "<button type='button' class='btn btn-default' title='icon button'>" +
				                 "<span class='glyphicon glyphicon-barcode' aria-hidden='true'></span>" +
				                 "</button>" );
	}
}
