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

import com.foreach.across.modules.bootstrapui.elements.CheckboxFormElement;
import com.foreach.across.modules.entity.newviews.util.EntityViewElementUtils;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Set the checked status of a {@link CheckboxFormElement} if a boolean property is {@code true}.
 *
 * @author Arne Vandamme
 */
public class EntityPropertyValueCheckboxPostProcessor implements ViewElementPostProcessor<CheckboxFormElement>
{
	private final EntityPropertyDescriptor propertyDescriptor;

	public EntityPropertyValueCheckboxPostProcessor( EntityPropertyDescriptor propertyDescriptor ) {
		this.propertyDescriptor = propertyDescriptor;
	}

	@Override
	public void postProcess( ViewElementBuilderContext viewElementBuilderContext,
	                         CheckboxFormElement checkbox ) {
		Object entity = EntityViewElementUtils.currentEntity( viewElementBuilderContext );
		ValueFetcher valueFetcher = propertyDescriptor.getValueFetcher();

		if ( entity != null && valueFetcher != null ) {
			Object propertyValue = valueFetcher.getValue( entity );

			checkbox.setChecked( isChecked( propertyValue ) );
		}
	}

	private boolean isChecked( Object value ) {
		if ( value instanceof Boolean ) {
			return Boolean.TRUE.equals( value );
		}
		else if ( value instanceof AtomicBoolean ) {
			return Boolean.TRUE.equals( ( (AtomicBoolean) value ).get() );
		}

		return false;
	}
}
