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

import com.foreach.across.modules.bootstrapui.elements.AbstractBootstrapViewElementTest;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementBuilderContextImpl;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Arne Vandamme
 */
public class TestOptionsAsCheckboxBuilder extends AbstractBootstrapViewElementTest
{
	protected OptionsFormElementBuilder builder;

	protected ViewElementBuilderContext builderContext;

	@Before
	public void reset() {
		builderContext = new ViewElementBuilderContextImpl();

		builder = new OptionsFormElementBuilder().checkbox();
	}

	@Test
	public void simple() {
		builder.htmlId( "mybox" ).controlName( "boxName" );

		expect(
				"<div id='mybox' />"
		);
	}

//	@Test
//	public void multiple() {
//		builder.multiple();
//
//		expect(
//				"<select class='form-control' multiple='multiple' />"
//		);
//	}
//
//	@Test
//	public void disabledAndReadonly() {
//		builder.disabled();
//
//		expect(
//				"<select class='form-control' disabled='disabled' />"
//		);
//
//		builder.disabled( false ).readonly();
//
//		expect(
//				"<select class='form-control' readonly='readonly' />"
//		);
//	}

	@Test
	public void options() {
		builder
				.controlName( "mybox" )
				.add(
						builder.option().text( "Inner text" ).value( "one" )
				)
				.add(
						builder.option().label( "Short two" ).text( "Some text" ).value( 2 ).selected().disabled()
				);

		expect(
				"<div id='mybox'>" +
						"<div class='checkbox'><label>" +
						"<input type='checkbox' value='one' id='mybox1' name='mybox' /> Inner text" +
						"</label><input type='hidden' name='_mybox' value='on' /></div>" +
						"<div class='checkbox disabled'><label>" +
						"<input type='checkbox' value='2' checked='checked' disabled='disabled' name='mybox' id='mybox2' /> Short two" +
						"</label><input type='hidden' name='_mybox' value='on' /></div>" +
						"</div>"
		);
	}

	private void expect( String output ) {
		renderAndExpect( builder.build( builderContext ), output );
	}
}
