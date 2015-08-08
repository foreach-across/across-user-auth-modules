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

import com.foreach.across.modules.bootstrapui.elements.*;
import com.foreach.across.modules.web.ui.ViewElementBuilderFactory;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import com.foreach.across.test.support.AbstractViewElementBuilderTest;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestFormGroupElementBuilder extends AbstractViewElementBuilderTest<FormGroupElementBuilder, FormGroupElement>
{
	@Override
	protected FormGroupElementBuilder createBuilder( ViewElementBuilderFactory builderFactory ) {
		return new FormGroupElementBuilder();
	}

	@Test
	public void getControlAndLabel() {
		assertNull( builder.getControl() );
		assertNull( builder.getLabel() );

		TextboxFormElementBuilder textbox = new TextboxFormElementBuilder();
		LabelFormElementBuilder label = new LabelFormElementBuilder();

		builder.control( textbox ).label( label );
		assertNotNull( builder.getControl() );
		assertSame( textbox, builder.getControl().getSource() );
		assertSame( textbox, builder.getControl( TextboxFormElementBuilder.class ) );
		assertNull( builder.getControl( FormGroupElementBuilder.class ) );

		assertSame( label, builder.getLabel().getSource() );
		assertSame( label, builder.getLabel( LabelFormElementBuilder.class ) );
		assertNull( builder.getLabel( TextboxFormElementBuilder.class ) );

		TextareaFormElement textarea = new TextareaFormElement();
		builder.control( textarea );

		assertSame( textarea, builder.getControl( TextboxFormElement.class ) );
		assertSame( textarea, builder.getControl( TextareaFormElement.class ) );
		assertNull( builder.getControl( LabelFormElement.class ) );

		assertSame( label, builder.getLabel().getSource() );
		assertSame( label, builder.getLabel( LabelFormElementBuilder.class ) );
		assertNull( builder.getLabel( TextboxFormElementBuilder.class ) );
	}

	@Test
	public void labelTargetIsSet() {
		TextboxFormElementBuilder textbox = new TextboxFormElementBuilder();
		LabelFormElementBuilder label = new LabelFormElementBuilder();

		builder.control( textbox ).label( label );

		FormGroupElement group = builder.build( builderContext );
		LabelFormElement labelElement = group.getLabel( LabelFormElement.class );

		assertTrue( labelElement.hasTarget() );
		assertNotNull( labelElement.getTargetAsElement() );
		assertSame( labelElement.getTargetAsElement(), group.getControl() );
	}

	@Test
	public void labelTargetIsSetInCaseOfInputGroup() {
		TextboxFormElement textbox = new TextboxFormElement();
		InputGroupFormElementBuilderSupport inputGroup = new InputGroupFormElementBuilder().control( textbox );
		LabelFormElementBuilder label = new LabelFormElementBuilder();

		builder.control( inputGroup ).label( label );

		FormGroupElement group = builder.build( builderContext );
		LabelFormElement labelElement = group.getLabel( LabelFormElement.class );

		assertTrue( labelElement.hasTarget() );
		assertSame( textbox, labelElement.getTargetAsElement() );
		assertNotNull( group.getControl( InputGroupFormElement.class ) );
	}

	@Test
	public void shortcutLabelAndHelpText() {
		builder.label( "some label" ).helpBlock( "some help" );

		FormGroupElement group = builder.build( builderContext );

		LabelFormElement labelFormElement = group.getLabel( LabelFormElement.class );
		assertNotNull( labelFormElement );
		assertEquals( "some label", labelFormElement.getText() );

		NodeViewElement node = group.getHelpBlock( NodeViewElement.class );
		assertNotNull( node );
		assertEquals( "some help", ( (TextViewElement) node.iterator().next() ).getText() );
	}
}
