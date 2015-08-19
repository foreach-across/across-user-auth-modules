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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Arne Vandamme
 */
public class TestInputGroupFormElement extends AbstractBootstrapViewElementTest
{
	private InputGroupFormElement inputGroup;

	@Before
	public void before() {
		inputGroup = new InputGroupFormElement();
	}

	@Test
	public void emptyInputGroup() {
		renderAndExpect(
				inputGroup,
				"<div class='input-group'>"
						+ "<input type='text' class='form-control' />"
						+ "</div>"
		);
	}

	@Test
	public void inputGroupWithOnlyControl() {
		inputGroup.setControl( new SelectFormElement() );

		renderAndExpect(
				inputGroup,
				"<div class='input-group'>"
						+ "<select class='form-control' />"
						+ "</div>"
		);
	}

	@Test
	public void simpleAddonBefore() {
		inputGroup.setAddonBefore( new GlyphIcon( GlyphIcon.CALENDAR ) );

		assertNull( inputGroup.getAddonAfter() );
		assertNotNull( inputGroup.getAddonBefore() );
		assertNotNull( inputGroup.getAddonBefore( GlyphIcon.class ) );
		assertNull( inputGroup.getAddonBefore( FaIcon.class ) );

		renderAndExpect(
				inputGroup,
				"<div class='input-group'>"
						+ "<span class='input-group-addon'>"
						+ "<span aria-hidden='true' class='glyphicon glyphicon-calendar'></span>"
						+ "</span>"
						+ "<input type='text' class='form-control' />"
						+ "</div>"
		);
	}

	@Test
	public void simpleAddonAfter() {
		inputGroup.setAddonAfter( new GlyphIcon( GlyphIcon.CALENDAR ) );

		assertNull( inputGroup.getAddonBefore() );
		assertNotNull( inputGroup.getAddonAfter() );
		assertNotNull( inputGroup.getAddonAfter( GlyphIcon.class ) );
		assertNull( inputGroup.getAddonAfter( FaIcon.class ) );

		renderAndExpect(
				inputGroup,
				"<div class='input-group'>"
						+ "<input type='text' class='form-control' />"
						+ "<span class='input-group-addon'>"
						+ "<span aria-hidden='true' class='glyphicon glyphicon-calendar'></span>"
						+ "</span>"
						+ "</div>"
		);
	}

	@Test
	public void buttonBefore() {
		inputGroup.setAddonBefore( new ButtonViewElement() );
		inputGroup.setAddonAfter( new GlyphIcon( GlyphIcon.CALENDAR ) );
		inputGroup.setControl( new TextboxFormElement() );

		renderAndExpect(
				inputGroup,
				"<div class='input-group'>"
						+ "<span class='input-group-btn'>"
						+ "<button type='button' class='btn btn-default' />"
						+ "</span>"
						+ "<input type='text' class='form-control' />"
						+ "<span class='input-group-addon'>"
						+ "<span aria-hidden='true' class='glyphicon glyphicon-calendar'></span>"
						+ "</span>"
						+ "</div>"
		);
	}

	@Test
	public void buttonAfter() {
		inputGroup.setAddonAfter( new ButtonViewElement() );
		inputGroup.setAddonBefore( new GlyphIcon( GlyphIcon.CALENDAR ) );
		inputGroup.setControl( new TextboxFormElement() );

		renderAndExpect(
				inputGroup,
				"<div class='input-group'>"
						+ "<span class='input-group-addon'>"
						+ "<span aria-hidden='true' class='glyphicon glyphicon-calendar'></span>"
						+ "</span>"
						+ "<input type='text' class='form-control' />"
						+ "<span class='input-group-btn'>"
						+ "<button type='button' class='btn btn-default' />"
						+ "</span>"
						+ "</div>"
		);
	}
}
