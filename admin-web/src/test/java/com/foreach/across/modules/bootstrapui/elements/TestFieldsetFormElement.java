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
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Arne Vandamme
 */
public class TestFieldsetFormElement extends AbstractBootstrapViewElementTest
{
	private FieldsetFormElement fieldset;

	@Before
	public void before() {
		fieldset = new FieldsetFormElement();
	}

	@Test
	public void emptyFieldset() {
		renderAndExpect(
				fieldset,
				"<fieldset />"
		);
	}

	@Test
	public void childrenButNoLegend() {
		fieldset.add( new TextViewElement( "line 1" ) );
		fieldset.add( new TextboxFormElement() );

		renderAndExpect(
				fieldset,
				"<fieldset>" +
						"line 1" +
						"<input type='text' class='form-control' />" +
						"</fieldset>"
		);
	}

	@Test
	public void html5Attributes() {
		assertFalse( fieldset.isDisabled() );
		assertNull( fieldset.getFormId() );
		assertNull( fieldset.getFieldsetName() );

		fieldset.setDisabled( true );
		renderAndExpect(
				fieldset,
				"<fieldset disabled='disabled' />"
		);

		assertTrue( fieldset.isDisabled() );
		assertNull( fieldset.getFormId() );
		assertNull( fieldset.getFieldsetName() );

		fieldset.setFormId( "myform" );
		renderAndExpect(
				fieldset,
				"<fieldset disabled='disabled' form='myform' />"
		);

		assertTrue( fieldset.isDisabled() );
		assertEquals( "myform", fieldset.getFormId() );
		assertNull( fieldset.getFieldsetName() );

		fieldset.setDisabled( false );
		fieldset.setFormId( null );
		fieldset.setName( "timeTracking" );
		renderAndExpect(
				fieldset,
				"<fieldset name='timeTracking' />"
		);

		assertFalse( fieldset.isDisabled() );
		assertNull( fieldset.getFormId() );
		assertEquals( "timeTracking", fieldset.getName() );
		assertEquals( "timeTracking", fieldset.getFieldsetName() );

		fieldset.setFieldsetName( "123" );

		renderAndExpect(
				fieldset,
				"<fieldset name='123' />"
		);

		assertFalse( fieldset.isDisabled() );
		assertNull( fieldset.getFormId() );
		assertEquals( "timeTracking", fieldset.getName() );
		assertEquals( "123", fieldset.getFieldsetName() );

		fieldset.setFieldsetName( null );

		renderAndExpect(
				fieldset,
				"<fieldset />"
		);

		assertFalse( fieldset.isDisabled() );
		assertNull( fieldset.getFormId() );
		assertNull( fieldset.getFieldsetName() );
		assertEquals( "timeTracking", fieldset.getName() );
	}

	@Test
	public void legendText() {
		fieldset.getLegend().setText( "Legendary legend text." );

		renderAndExpect(
				fieldset,
				"<fieldset>" +
						"<legend>Legendary legend text.</legend>" +
						"</fieldset>"
		);
	}

	@Test
	public void legendWithCustomButtonAndChildren() {
		fieldset.getLegend().setText( "legend text" );
		fieldset.getLegend().add( new ButtonViewElement() );

		fieldset.add( new TextViewElement( "line 1" ) );
		fieldset.add( new TextboxFormElement() );

		renderAndExpect(
				fieldset,
				"<fieldset>" +
						"<legend>legend text<button type='button' class='btn btn-default' /></legend>" +
						"line 1" +
						"<input type='text' class='form-control' />" +
						"</fieldset>"
		);
	}
}
