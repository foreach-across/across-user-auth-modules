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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Arne Vandamme
 */
public class TestHiddenFormElement extends AbstractBootstrapViewElementTest
{
	@Test
	public void simple() {
		HiddenFormElement hidden = new HiddenFormElement();

		renderAndExpect(
				hidden,
				"<input type='hidden' />"
		);

		assertNull( hidden.getValue() );
	}

	@Test
	public void withAttributes() {
		HiddenFormElement hidden = new HiddenFormElement();
		hidden.setControlName( "inputName" );
		hidden.setValue( 123 );
		hidden.setHtmlId( "_id" );

		renderAndExpect(
				hidden,
				"<input type='hidden' id='_id' name='inputName' value='123' />"
		);

		assertEquals( Integer.valueOf( 123 ), hidden.getValue( Integer.class ) );
	}
}
