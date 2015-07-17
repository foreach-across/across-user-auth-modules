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
package com.foreach.across.modules.entity.newviews.bootstrapui.processors.element;

import com.foreach.across.modules.entity.newviews.util.EntityViewElementUtils;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import com.foreach.across.modules.web.ui.elements.ConfigurableTextViewElement;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

/**
 * Responsible for fetching the property value and setting it as text property on a {@link ConfigurableTextViewElement}.
 */
public class EntityPropertyValuePostProcessor<T extends ConfigurableTextViewElement>
		implements ViewElementPostProcessor<T>
{
	private static final TypeDescriptor STRING_TYPE = TypeDescriptor.valueOf( String.class );

	private final ConversionService conversionService;
	private final EntityPropertyDescriptor propertyDescriptor;

	public EntityPropertyValuePostProcessor( ConversionService conversionService,
	                                         EntityPropertyDescriptor propertyDescriptor ) {
		this.conversionService = conversionService;
		this.propertyDescriptor = propertyDescriptor;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void postProcess( ViewElementBuilderContext builderContext, T element ) {
		Object entity = EntityViewElementUtils.currentEntity( builderContext );
		ValueFetcher valueFetcher = propertyDescriptor.getValueFetcher();

		if ( entity != null && valueFetcher != null ) {
			Object propertyValue = valueFetcher.getValue( entity );
			TypeDescriptor sourceType = propertyDescriptor.getPropertyTypeDescriptor();

			if ( sourceType == null && propertyValue != null ) {
				sourceType = TypeDescriptor.forObject( propertyValue );
			}

			String text = (String) conversionService.convert( propertyValue, sourceType, STRING_TYPE );

			element.setText( text );
		}
	}
}
