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

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * @author Arne Vandamme
 */
public class TestNumericFormElement extends AbstractBootstrapViewElementTest
{
	private static final String DATA_ATTRIBUTE = "data-bootstrapui-numeric='{\"mDec\":2,\"vMin\":-9223372036854775808}'";

	private NumericFormElement numeric;

	@Before
	public void before() {
		numeric = new NumericFormElement();
	}

	@Test
	public void emptyNumeric() {
		renderAndExpect(
				numeric,
				"<input class='form-control numeric' type='text' />"
		);
	}

	@Test
	public void simpleNumericWithControlNameAndValue() {
		numeric.setControlName( "number" );
		numeric.setValue( 123L );

		renderAndExpect(
				numeric,
				"<input id='number' name='number' class='form-control numeric' type='text' value='123' />"
		);
	}

	@Test
	public void withControlNameAndValue() {
		BigDecimal number = new BigDecimal( "123.9541" );
		numeric.setConfiguration( new NumericFormElementConfiguration() );
		numeric.setControlName( "number" );
		numeric.setValue( number );

		renderAndExpect(
				numeric,
				"<input id='number' name='_number' class='form-control numeric' " +
						"type='text' " + DATA_ATTRIBUTE + " value='123.9541' />" +
						"<input type='hidden' name='number' value='123.9541' />"
		);
	}

	@Test
	public void numericFormElementInFormGroup() {
		FormGroupElement group = new FormGroupElement();

		numeric.setName( "number" );

		LabelFormElement label = new LabelFormElement();
		label.setTarget( numeric );
		label.setText( "title" );

		group.setLabel( label );
		group.setControl( numeric );

		renderAndExpect(
				group,
				"<div class='form-group'>" +
						"<label for='number' class='control-label'>title</label>" +
						"<input id='number' name='number' class='form-control numeric' type='text' />" +
						"</div>"
		);
	}
}
