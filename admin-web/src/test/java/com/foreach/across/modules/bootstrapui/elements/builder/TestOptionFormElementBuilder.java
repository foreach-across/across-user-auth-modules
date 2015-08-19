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

import com.foreach.across.modules.bootstrapui.elements.CheckboxFormElement;
import com.foreach.across.modules.bootstrapui.elements.FormControlElementSupport;
import com.foreach.across.modules.bootstrapui.elements.RadioFormElement;
import com.foreach.across.modules.bootstrapui.elements.SelectFormElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderFactory;
import com.foreach.across.test.support.AbstractViewElementBuilderTest;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Arne Vandamme
 */
public class TestOptionFormElementBuilder
		extends AbstractViewElementBuilderTest<OptionFormElementBuilder<FormControlElementSupport>, FormControlElementSupport>
{
	@Override
	protected OptionFormElementBuilder createBuilder( ViewElementBuilderFactory builderFactory ) {
		return new OptionFormElementBuilder();
	}

	@Test
	public void selectOption() {
		builder.controlName( "boe" ).value( "test" ).selected( true ).label( "my label" );

		build();

		assertTrue( element instanceof SelectFormElement.Option );

		SelectFormElement.Option option = (SelectFormElement.Option) element;
		assertEquals( "boe", option.getControlName() );
		assertEquals( "my label", option.getLabel() );
		assertTrue( option.isSelected() );
		assertEquals( "test", option.getValue() );
	}

	@Test
	public void checkbox() {
		builder.checkbox().controlName( "boe" ).value( "test" ).selected( true ).label( "my label" );

		build();

		assertTrue( element instanceof CheckboxFormElement );

		CheckboxFormElement option = (CheckboxFormElement) element;
		assertEquals( "boe", option.getControlName() );
		assertEquals( "my label", option.getText() );
		assertTrue( option.isChecked() );
		assertEquals( "test", option.getValue() );
	}

	@Test
	public void radio() {
		builder.radio().controlName( "boe" ).value( "test" ).selected( true ).label( "my label" );

		build();

		assertTrue( element instanceof RadioFormElement );

		RadioFormElement option = (RadioFormElement) element;
		assertEquals( "boe", option.getControlName() );
		assertEquals( "my label", option.getText() );
		assertTrue( option.isChecked() );
		assertEquals( "test", option.getValue() );
	}

	@Test
	public void sortOptionsOnTextOnly() {
		List<OptionFormElementBuilder> options = Arrays.asList(
				new OptionFormElementBuilder().text( "bbb" ), new OptionFormElementBuilder().text( "aaa" )
		);
		Collections.sort( options );

		assertEquals( "aaa", options.get( 0 ).getText() );
		assertEquals( "bbb", options.get( 1 ).getText() );
	}

	@Test
	public void sortOptionsOnLabelOnly() {
		List<OptionFormElementBuilder> options = Arrays.asList(
				new OptionFormElementBuilder().label( "bbb" ), new OptionFormElementBuilder().label( "aaa" )
		);
		Collections.sort( options );

		assertEquals( "aaa", options.get( 0 ).getLabel() );
		assertEquals( "bbb", options.get( 1 ).getLabel() );
	}

	@Test
	public void sortOptionsOnLabelBeforeText() {
		List<OptionFormElementBuilder> options = Arrays.asList(
				new OptionFormElementBuilder().label( "aaa" ).text( "bbb" ),
				new OptionFormElementBuilder().label( "bbb" ).text( "aaa" )
		);
		Collections.sort( options );

		assertEquals( "aaa", options.get( 0 ).getLabel() );
		assertEquals( "bbb", options.get( 1 ).getLabel() );
	}
}
