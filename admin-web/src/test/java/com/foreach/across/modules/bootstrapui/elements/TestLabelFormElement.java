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

import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import org.junit.Test;

/**
 * @author Arne Vandamme
 */
public class TestLabelFormElement extends AbstractBootstrapViewElementTest
{
	@Test
	public void simple() {
		LabelFormElement box = new LabelFormElement();
		box.setText( "Component name" );

		renderAndExpect(
				box,
				"<label class='control-label'>Component name</label>"
		);
	}

	@Test
	public void fixedTargetId() {
		LabelFormElement box = new LabelFormElement();
		box.setTarget( "fixedId" );
		box.setText( "Component name" );

		renderAndExpect(
				box,
				"<label for='fixedId' class='control-label'>Component name</label>"
		);
	}

	@Test
	public void targetFormElement() {
		TextboxFormElement textbox = new TextboxFormElement();
		textbox.setName( "name" );

		LabelFormElement label = new LabelFormElement();
		label.setTarget( textbox );
		label.setText( "Textbox title" );

		renderAndExpect(
				label,
				"<label for='name' class='control-label'>Textbox title</label>"
		);
	}

	@Test
	public void targetInputGroup() {
		TextboxFormElement textbox = new TextboxFormElement();
		textbox.setName( "name" );

		InputGroupFormElement inputGroup = new InputGroupFormElement();
		inputGroup.setControl( textbox );

		LabelFormElement label = new LabelFormElement();
		label.setTarget( inputGroup );
		label.setText( "InputGroup title" );

		renderAndExpect(
				label,
				"<label for='name' class='control-label'>InputGroup title</label>"
		);
	}

	@Test
	public void targetFormElementRenderedBefore() {
		TextboxFormElement textbox = new TextboxFormElement();
		textbox.setName( "name" );

		LabelFormElement label = new LabelFormElement();
		label.setTarget( textbox );
		label.setText( "Textbox title" );

		ContainerViewElement container = new ContainerViewElement();
		container.add( textbox );
		container.add( label );

		renderAndExpect(
				container,
				"<input class='form-control' type='text' name='name' id='name' />" +
						"<label for='name' class='control-label'>Textbox title</label>"
		);
	}

	@Test
	public void targetFormElementRenderedAfter() {
		TextboxFormElement textbox = new TextboxFormElement();
		textbox.setName( "name" );

		LabelFormElement label = new LabelFormElement();
		label.setTarget( textbox );
		label.setText( "Textbox title" );

		ContainerViewElement container = new ContainerViewElement();
		container.add( label );
		container.add( textbox );

		renderAndExpect(
				container,
				"<label for='name' class='control-label'>Textbox title</label>" +
						"<input class='form-control' type='text' name='name' id='name' />"
		);
	}

	@Test
	public void targetFormElementRenderedAsChild() {
		TextboxFormElement textbox = new TextboxFormElement();
		textbox.setName( "name" );

		LabelFormElement label = new LabelFormElement();
		label.setTarget( textbox );
		label.setText( "Textbox title" );
		label.add( textbox );

		renderAndExpect(
				label,
				"<label for='name' class='control-label'>" +
						"Textbox title" +
						"<input class='form-control' type='text' name='name' id='name' />" +
						"</label>"
		);
	}

	@Test
	public void simpleFormElementRenderedAsChild() {
		LabelFormElement label = new LabelFormElement();
		label.add( new TextboxFormElement() );

		renderAndExpect(
				label,
				"<label class='control-label'>" +
						"<input class='form-control' type='text' />" +
						"</label>"
		);
	}

	@Test
	public void customViewElementTarget() {
		StaticFormElement staticContent = new StaticFormElement();
		staticContent.setHtmlId( "static" );
		staticContent.setText( "static content" );

		LabelFormElement label = new LabelFormElement();
		label.setTarget( staticContent );
		label.setText( "title" );

		ContainerViewElement container = new ContainerViewElement();
		container.add( label );
		container.add( staticContent );

		renderAndExpect(
				container,
				"<label for='static' class='control-label'>title</label>" +
						"<p class='form-control-static' id='static'>static content</p>"
		);
	}
}
