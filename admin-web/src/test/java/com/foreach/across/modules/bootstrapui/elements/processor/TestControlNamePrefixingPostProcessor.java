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
package com.foreach.across.modules.bootstrapui.elements.processor;

import com.foreach.across.modules.bootstrapui.elements.TextboxFormElement;
import com.foreach.across.modules.bootstrapui.elements.builder.TextboxFormElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContextImpl;
import com.foreach.across.modules.web.ui.elements.builder.TextViewElementBuilder;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Arne Vandamme
 */
public class TestControlNamePrefixingPostProcessor
{
	@Test
	public void simplePrefixing() {
		ControlNamePrefixingPostProcessor processor = new ControlNamePrefixingPostProcessor( "test." );

		TextboxFormElement textbox = new TextboxFormElementBuilder()
				.controlName( "textbox" )
				.postProcessor( processor )
				.build( new ViewElementBuilderContextImpl() );

		assertEquals( "test.textbox", textbox.getControlName() );
	}

	@Test
	public void noControlNameSet() {
		ControlNamePrefixingPostProcessor processor = new ControlNamePrefixingPostProcessor( "test." );

		TextboxFormElement textbox = new TextboxFormElementBuilder()
				.postProcessor( processor )
				.build( new ViewElementBuilderContextImpl() );

		assertNull( textbox.getControlName() );
	}

	@Test
	public void notAFormInputElement() {
		ControlNamePrefixingPostProcessor processor = new ControlNamePrefixingPostProcessor( "test." );

		new TextViewElementBuilder()
				.postProcessor( processor )
				.build( new ViewElementBuilderContextImpl() );
	}

	@Test
	public void alreadyPrefixed() {
		ControlNamePrefixingPostProcessor processor = new ControlNamePrefixingPostProcessor( "test." );

		TextboxFormElement textbox = new TextboxFormElementBuilder()
				.controlName( "test.textbox" )
				.postProcessor( processor )
				.build( new ViewElementBuilderContextImpl() );

		assertEquals( "test.textbox", textbox.getControlName() );
	}

	@Test
	public void alwaysPrefix() {
		ControlNamePrefixingPostProcessor processor = new ControlNamePrefixingPostProcessor( "test.", true );

		TextboxFormElement textbox = new TextboxFormElementBuilder()
				.controlName( "test.textbox" )
				.postProcessor( processor )
				.build( new ViewElementBuilderContextImpl() );

		assertEquals( "test.test.textbox", textbox.getControlName() );
	}
}
