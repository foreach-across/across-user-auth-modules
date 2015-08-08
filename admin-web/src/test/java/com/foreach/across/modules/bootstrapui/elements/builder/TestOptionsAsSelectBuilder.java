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
public class TestOptionsAsSelectBuilder extends AbstractBootstrapViewElementTest
{
	protected OptionsFormElementBuilder builder;

	protected ViewElementBuilderContext builderContext;

	@Before
	public void reset() {
		builderContext = new ViewElementBuilderContextImpl();

		builder = new OptionsFormElementBuilder();
	}

	@Test
	public void simple() {
		builder.htmlId( "mybox" ).controlName( "boxName" );

		expect(
				"<select name='boxName' id='mybox' class='form-control' />"
		);
	}

	@Test
	public void multiple() {
		builder.multiple();

		expect(
				"<select class='form-control' multiple='multiple' />"
		);
	}

	@Test
	public void disabledAndReadonly() {
		builder.disabled();

		expect(
				"<select class='form-control' disabled='disabled' />"
		);

		builder.disabled( false ).readonly();

		expect(
				"<select class='form-control' readonly='readonly' />"
		);
	}

	@Test
	public void options() {
		builder
				.add( new OptionFormElementBuilder().text( "Inner text" ).value( "one" ) )
				.add( new OptionFormElementBuilder().label( "Only label" ).value( 123 ) )
				.add( new OptionFormElementBuilder().label( "Short two" ).text( "Some text" ).selected().disabled() );

		expect(
				"<select class='form-control'>" +
						"<option value='one'>Inner text</option>" +
						"<option value='123'>Only label</option>" +
						"<option label='Short two' selected='selected' disabled='disabled'>Some text</option>" +
						"</select>"
		);
	}

	private void expect( String output ) {
		renderAndExpect( builder.build( builderContext ), output );
	}
}
