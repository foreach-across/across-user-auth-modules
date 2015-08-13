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
package com.foreach.across.modules.entity.views.bootstrapui.processors.element;

import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import com.foreach.across.modules.web.ui.elements.ConfigurableTextViewElement;
import org.springframework.util.Assert;

/**
 * Implementation of {@link ViewElementPostProcessor} for a {@link ConfigurableTextViewElement}
 * that will set the text property of the element based on a messagecode that is being resolved.
 * The currently set text will be used as the fallback in case the code cannot be resolved.
 *
 * @author Arne Vandamme
 */
public class TextCodeResolverPostProcessor<T extends ConfigurableTextViewElement>
		implements ViewElementPostProcessor<T>
{
	private final String messageCode;

	public TextCodeResolverPostProcessor( String messageCode ) {
		Assert.notNull( messageCode );
		this.messageCode = messageCode;
	}

	@Override
	public void postProcess( ViewElementBuilderContext builderContext, T text ) {
		EntityMessageCodeResolver codeResolver = builderContext.getAttribute( EntityMessageCodeResolver.class );

		if ( codeResolver != null ) {
			text.setText( codeResolver.getMessageWithFallback( messageCode, text.getText() ) );
		}
	}
}
