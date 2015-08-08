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

import com.foreach.across.modules.bootstrapui.elements.ButtonViewElement;
import com.foreach.across.modules.bootstrapui.elements.FieldsetFormElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderFactory;
import com.foreach.across.test.support.AbstractViewElementBuilderTest;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Arne Vandamme
 */
public class TestFieldsetFormElementBuilder extends AbstractViewElementBuilderTest<FieldsetFormElementBuilder, FieldsetFormElement>
{
	@Override
	protected FieldsetFormElementBuilder createBuilder( ViewElementBuilderFactory builderFactory ) {
		return new FieldsetFormElementBuilder();
	}

	@Test
	public void attributesAndSettings() {
		build();

		assertNull( element.getFormId() );
		assertNull( element.getFieldsetName() );
		assertFalse( element.isDisabled() );
		assertNull( element.getLegend().getText() );

		builder.formId( "myform" )
		       .fieldsetName( "name" )
		       .disabled()
		       .legend( "legend text" );
		build();

		assertEquals( "myform", element.getFormId() );
		assertEquals( "name", element.getFieldsetName() );
		assertTrue( element.isDisabled() );
		assertEquals( "legend text", element.getLegend().getText() );

		builder.legend()
		       .text( "other text" )
		       .add( new ButtonViewElement() );
		build();

		assertEquals( "other text", element.getLegend().getText() );
		assertEquals( 1, element.getLegend().size() );
	}
}
