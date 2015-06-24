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

import org.junit.Test;

/**
 * @author Arne Vandamme
 */
public class TestFaIcon extends AbstractBootstrapViewElementTest
{
	@Test
	public void simpleIcon() {
		renderAndExpect(
				new FaIcon( FaIcon.WebApp.ADJUST ),
				"<i class='fa fa-adjust' aria-hidden='true'></i>"
		);
	}

	@Test
	public void customTagName() {
		IconViewElement icon = new FaIcon( FaIcon.WebApp.PENCIL );
		icon.setTagName( "div" );

		renderAndExpect(
				icon,
				"<div class='fa fa-pencil' aria-hidden='true'></div>"
		);
	}

	@Test
	public void emptyGlyphGeneratesElement() {
		renderAndExpect(
				new FaIcon( "" ),
				"<i class='fa ' aria-hidden='true'></i>"
		);
	}
}
