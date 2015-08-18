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

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.web.ui.elements.ConfigurableTextViewElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

import java.util.Locale;

/**
 * Implementation of {@link AbstractValueTextPostProcessor} that uses a conversion service for
 * converting the value to {@link com.foreach.across.modules.web.ui.elements.ConfigurableTextViewElement#setText(String)}.
 *
 * @author Arne Vandamme
 */
public class ConversionServiceValueTextPostProcessor<T extends ConfigurableTextViewElement>
		extends AbstractValueTextPostProcessor<T>
{
	private static final Logger LOG = LoggerFactory.getLogger( ConversionServiceValueTextPostProcessor.class );
	private static final TypeDescriptor STRING_TYPE = TypeDescriptor.valueOf( String.class );

	private final ConversionService conversionService;

	public ConversionServiceValueTextPostProcessor( EntityPropertyDescriptor propertyDescriptor,
	                                                ConversionService conversionService ) {
		super( propertyDescriptor );
		this.conversionService = conversionService;
	}

	@Override
	protected String print( Object value, Locale locale ) {
		TypeDescriptor sourceType = getPropertyDescriptor().getPropertyTypeDescriptor();

		if ( sourceType == null ) {
			sourceType = TypeDescriptor.forObject( value );
		}

		try {
			return (String) conversionService.convert( value, sourceType, STRING_TYPE );
		}
		catch ( ConversionException ce ) {
			LOG.warn( "Unable to convert {} to string", sourceType, ce );
		}

		return "";
	}
}
