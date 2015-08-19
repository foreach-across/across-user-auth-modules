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

import com.foreach.across.modules.bootstrapui.elements.DateTimeFormElementConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.web.ui.elements.ConfigurableTextViewElement;

import java.util.Locale;

/**
 * Uses a localized {@link DateTimeFormElementConfiguration} and
 * the {@link java.text.DateFormat} it exposes to convert a property value to text.
 *
 * @author Arne Vandamme
 */
public class DateTimeValueTextPostProcessor<T extends ConfigurableTextViewElement> extends AbstractValueTextPostProcessor<T>
{
	private final DateTimeFormElementConfiguration configuration;

	public DateTimeValueTextPostProcessor( EntityPropertyDescriptor propertyDescriptor,
	                                       DateTimeFormElementConfiguration configuration ) {
		super( propertyDescriptor );
		this.configuration = configuration;
	}

	@Override
	protected String print( Object value, Locale locale ) {
		return configuration.localize( locale ).createDateFormat().format( value );
	}
}
