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
import org.junit.Test;
import org.springframework.http.HttpMethod;

import static org.junit.Assert.*;

/**
 * @author Arne Vandamme
 */
public class TestFormViewElement extends AbstractBootstrapViewElementTest
{
	@Test
	public void method() {
		FormViewElement form = new FormViewElement();
		assertEquals( HttpMethod.POST, form.getMethod() );

		form.setMethod( HttpMethod.GET );
		assertEquals( HttpMethod.GET, form.getMethod() );
	}

	@Test
	public void formNameIsSameAsNameUnlessOtherwiseSpecified() {
		FormViewElement form = new FormViewElement();
		assertNull( form.getName() );
		assertNull( form.getFormName() );

		form.setName( "someName" );
		assertEquals( "someName", form.getName() );
		assertEquals( "someName", form.getFormName() );

		form.setFormName( "formName" );
		assertEquals( "someName", form.getName() );
		assertEquals( "formName", form.getFormName() );
	}

	@Test
	public void simpleForm() {
		FormViewElement form = new FormViewElement();
		form.setCommandAttribute( "${element}" );
		form.add( new TextViewElement( "some text" ) );

		renderAndExpect(
				form,
				"<form role='form' method='post'>some text</form>"
		);
	}

	@Test
	public void namedForms() {
		FormViewElement form = new FormViewElement();
		form.add( new TextViewElement( "some text" ) );
		form.setName( "defaultName" );

		renderAndExpect(
				form,
				"<form role='form' method='post' name='defaultName'>some text</form>"
		);

		form.setFormName( "formName" );

		renderAndExpect(
				form,
				"<form role='form' method='post' name='formName'>some text</form>"
		);
	}

	@Test
	public void customHttpMethodAndAction() {
		FormViewElement form = new FormViewElement();
		form.add( new TextViewElement( "some text" ) );
		form.setMethod( HttpMethod.GET );
		form.setAction( "actionUrl" );

		renderAndExpect(
				form,
				"<form role='form' method='get' action='actionUrl'>some text</form>"
		);
	}

	@Test
	public void encType() {
		FormViewElement form = new FormViewElement();
		form.setEncType( FormViewElement.ENCTYPE_MULTIPART );

		renderAndExpect(
				form,
				"<form role='form' method='post' enctype='multipart/form-data'></form>"
		);
	}

	@Test
	public void acceptCharSet() {
		FormViewElement form = new FormViewElement();
		form.setAcceptCharSet( "UTF-8" );

		renderAndExpect(
				form,
				"<form role='form' method='post' accept-charset='UTF-8'></form>"
		);
	}

	@Test
	public void noValidate() {
		FormViewElement form = new FormViewElement();
		assertFalse( form.isNoValidate() );

		form.setNoValidate( true );
		assertTrue( form.isNoValidate() );

		renderAndExpect(
				form,
				"<form role='form' method='post' novalidate='novalidate'></form>"
		);
	}

	@Test
	public void autoComplete() {
		FormViewElement form = new FormViewElement();
		assertTrue( form.isAutoComplete() );

		form.setAutoComplete( true );
		assertTrue( form.isAutoComplete() );

		renderAndExpect(
				form,
				"<form role='form' method='post' autocomplete='on'></form>"
		);

		form.setAutoComplete( false );
		assertFalse( form.isAutoComplete() );

		renderAndExpect(
				form,
				"<form role='form' method='post' autocomplete='off'></form>"
		);
	}
}
