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

import com.foreach.across.modules.bootstrapui.elements.ConfigurablePlaceholderText;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

/**
 * Resolves a message code and sets it as the value of the placeholder property.
 *
 * @author Arne Vandamme
 */
public class PlaceholderTextPostProcessor<T extends ConfigurablePlaceholderText & ViewElement>
		implements ViewElementPostProcessor<T>
{
	private final String messageCode;

	public PlaceholderTextPostProcessor( EntityPropertyDescriptor descriptor ) {
		this( "properties." + descriptor.getName() + "[placeholder]" );
	}

	public PlaceholderTextPostProcessor( String messageCode ) {
		Assert.notNull( messageCode );
		this.messageCode = messageCode;
	}

	@Override
	public void postProcess( ViewElementBuilderContext builderContext, T element ) {
		EntityMessageCodeResolver codeResolver = builderContext.getAttribute( EntityMessageCodeResolver.class );

		if ( codeResolver != null ) {
			element.setPlaceholder(
					codeResolver.getMessageWithFallback(
							messageCode, StringUtils.defaultString( element.getPlaceholder() )
					)
			);
		}
	}
}
