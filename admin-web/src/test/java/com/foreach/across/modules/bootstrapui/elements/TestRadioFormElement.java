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
public class TestRadioFormElement extends AbstractBootstrapViewElementTest
{
	@Test
	public void simple() {
		RadioFormElement box = new RadioFormElement();
		box.setControlName( "boxName" );
		box.setText( "label text" );
		box.setValue( 123 );

		renderAndExpect(
				box,
				"<div class='radio'><label for='boxName'>" +
				"<input type='radio' id='boxName' name='boxName' value='123' />label text" +
						"</label></div>"
		);
	}

	@Test
	public void checked() {
		RadioFormElement box = new RadioFormElement();
		box.setValue( true );
		box.setChecked( true );

		renderAndExpect(
				box,
				"<div class='radio'><label>" +
						"<input type='radio' value='true' checked='checked' />" +
						"</label></div>"
		);
	}

	@Test
	public void disabled() {
		RadioFormElement box = new RadioFormElement();
		box.setValue( "on" );
		box.setDisabled( true );

		renderAndExpect(
				box,
				"<div class='radio disabled'><label>" +
						"<input type='radio' value='on' disabled='disabled' />" +
						"</label></div>"
		);

		box.setDisabled( false );
		box.setReadonly( true );

		renderAndExpect(
				box,
				"<div class='radio'><label>" +
						"<input type='radio' value='on' readonly='readonly' />" +
						"</label></div>"
		);
	}
}
