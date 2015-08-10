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

import com.foreach.across.modules.bootstrapui.elements.LabelFormElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderFactory;
import com.foreach.across.modules.web.ui.elements.builder.TextViewElementBuilder;
import com.foreach.across.test.support.AbstractViewElementBuilderTest;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestLabelFormElementBuilder extends AbstractViewElementBuilderTest<LabelFormElementBuilder, LabelFormElement>
{
	@Override
	protected LabelFormElementBuilder createBuilder( ViewElementBuilderFactory builderFactory ) {
		return new LabelFormElementBuilder();
	}

	@Test
	public void labelTextFromViewElementEndsUpAsText() {
		TextViewElementBuilder textBuilder = new TextViewElementBuilder().text( "Simple text" );
		builder.text( textBuilder );

		build();
		assertEquals( "Simple text", element.getText() );
		assertTrue( element.isEmpty() );

		textBuilder.escapeXml( false );

		build();
		assertNull( element.getText() );
		assertEquals( 1, element.size() );

		textBuilder.escapeXml( true ).customTemplate( "some template" );

		build();
		assertNull( element.getText() );
		assertEquals( 1, element.size() );
	}
}
