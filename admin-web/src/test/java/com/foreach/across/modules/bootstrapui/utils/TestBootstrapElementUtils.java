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
package com.foreach.across.modules.bootstrapui.utils;

import com.foreach.across.modules.bootstrapui.elements.*;
import org.junit.Test;

import static org.junit.Assert.assertSame;

/**
 * @author Arne Vandamme
 */
public class TestBootstrapElementUtils
{
	private final TextboxFormElement textbox = new TextboxFormElement();
	private final CheckboxFormElement child = new CheckboxFormElement();

	@Test
	public void directFormControl() {
		assertSame( textbox, BootstrapElementUtils.getFormControl( textbox ) );
	}

	@Test
	public void inputGroupShouldReturnControl() {
		InputGroupFormElement inputGroup = new InputGroupFormElement();
		inputGroup.setControl( textbox );
		inputGroup.add( child );

		assertSame( textbox, BootstrapElementUtils.getFormControl( inputGroup ) );
	}

	@Test
	public void formGroupWithTextboxShouldReturnTextbox() {
		FormGroupElement formGroup = new FormGroupElement();
		formGroup.setControl( textbox );
		formGroup.add( child );

		assertSame( textbox, BootstrapElementUtils.getFormControl( formGroup ) );
	}

	@Test
	public void datetimeFormElementShouldReturnTextbox() {
		DateTimeFormElement datetime = new DateTimeFormElement();
		datetime.setControl( textbox );
		datetime.add( child );

		assertSame( textbox, BootstrapElementUtils.getFormControl( datetime ) );
	}

	@Test
	public void formGroupWithInputGroupShouldReturnInputGroupControl() {
		InputGroupFormElement inputGroup = new InputGroupFormElement();
		inputGroup.setControl( textbox );
		inputGroup.add( child );

		FormGroupElement formGroup = new FormGroupElement();
		formGroup.setControl( inputGroup );

		assertSame( textbox, BootstrapElementUtils.getFormControl( formGroup ) );
	}

	@Test
	public void formGroupWithDateTimeShouldReturnDateTimeControl() {
		DateTimeFormElement datetime = new DateTimeFormElement();
		datetime.setControl( textbox );
		datetime.add( child );

		FormGroupElement formGroup = new FormGroupElement();
		formGroup.setControl( datetime );

		assertSame( textbox, BootstrapElementUtils.getFormControl( formGroup ) );
	}

	@Test
	public void formGroupWithInputGroupWithoutControlShouldReturnInputGroupChild() {
		InputGroupFormElement inputGroup = new InputGroupFormElement();
		inputGroup.setControl( null );
		inputGroup.add( child );

		FormGroupElement formGroup = new FormGroupElement();
		formGroup.setControl( inputGroup );

		assertSame( child, BootstrapElementUtils.getFormControl( formGroup ) );
	}

	@Test
	public void formGroupWithoutControlShouldReturnChild() {
		FormGroupElement formGroup = new FormGroupElement();
		formGroup.setControl( null );
		formGroup.add( child );

		assertSame( child, BootstrapElementUtils.getFormControl( formGroup ) );
	}
}
