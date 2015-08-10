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
public class TestSelectFormElement extends AbstractBootstrapViewElementTest
{
	@Test
	public void simple() {
		SelectFormElement box = new SelectFormElement();
		box.setHtmlId( null );
		box.setControlName( "boxName" );

		renderAndExpect(
				box,
				"<select name='boxName' class='form-control' />"
		);
	}

	@Test
	public void multiple() {
		SelectFormElement box = new SelectFormElement();
		box.setMultiple( true );

		renderAndExpect(
				box,
				"<select class='form-control' multiple='multiple' />"
		);
	}

	@Test
	public void disabledAndReadonly() {
		SelectFormElement box = new SelectFormElement();
		box.setDisabled( true );

		renderAndExpect(
				box,
				"<select class='form-control' disabled='disabled' />"
		);

		box.setDisabled( false );
		box.setReadonly( true );

		renderAndExpect(
				box,
				"<select class='form-control' readonly='readonly' />"
		);
	}

	@Test
	public void options() {
		SelectFormElement box = new SelectFormElement();

		SelectFormElement.Option one = new SelectFormElement.Option();
		one.setValue( "one" );
		one.setText( "Inner text" );

		SelectFormElement.Option two = new SelectFormElement.Option();
		two.setLabel( "Short two" );
		two.setText( "Some text" );
		two.setSelected( true );
		two.setDisabled( true );

		SelectFormElement.Option three = new SelectFormElement.Option();
		three.setValue( 123 );
		three.setLabel( "Label only" );

		box.add( one );
		box.add( two );
		box.add( three );

		renderAndExpect(
				box,
				"<select class='form-control'>" +
						"<option value='one'>Inner text</option>" +
						"<option label='Short two' selected='selected' disabled='disabled'>Some text</option>" +
						"<option value='123'>Label only</option>" +
						"</select>"
		);
	}

	@Test
	public void optionGroups() {
		SelectFormElement box = new SelectFormElement();
		box.setMultiple( true );
		box.setName( "internalName" );
		box.setControlName( "controlName" );
		box.setReadonly( true );

		SelectFormElement.OptionGroup group = new SelectFormElement.OptionGroup();
		SelectFormElement.Option one = new SelectFormElement.Option();
		one.setValue( "one" );
		one.setText( "Inner text" );

		SelectFormElement.Option two = new SelectFormElement.Option();
		two.setLabel( "Short two" );
		two.setText( "Some text" );
		two.setSelected( true );
		two.setDisabled( true );

		group.add( one );
		group.add( two );

		SelectFormElement.OptionGroup groupTwo = new SelectFormElement.OptionGroup();
		groupTwo.setDisabled( true );
		groupTwo.setLabel( "some label" );

		box.add( two );
		box.add( group );
		box.add( groupTwo );
		box.add( one );

		renderAndExpect(
				box,
				"<select id='controlName' class='form-control' name='controlName' multiple='multiple' readonly='readonly'>" +
						"<option label='Short two' selected='selected' disabled='disabled'>Some text</option>" +
						"<optgroup>" +
						"<option value='one'>Inner text</option>" +
						"<option label='Short two' selected='selected' disabled='disabled'>Some text</option>" +
						"</optgroup>" +
						"<optgroup disabled='disabled' label='some label'></optgroup>" +
						"<option value='one'>Inner text</option>" +
						"</select>"
		);
	}
}
