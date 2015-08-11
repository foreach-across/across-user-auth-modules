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
package com.foreach.across.modules.entity.newviews.bootstrapui.elements.builder;

import com.foreach.across.modules.entity.config.entities.AuditableEntityUiConfiguration;
import com.foreach.across.modules.entity.newviews.util.EntityViewElementUtils;
import com.foreach.across.modules.hibernate.business.Auditable;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.ConversionService;

import java.util.Date;
import java.util.Objects;

/**
 * <p>Custom {@link ViewElementBuilder} for created and last modified properties of any
 * {@link com.foreach.across.modules.hibernate.business.Auditable} entity.  Will combine both
 * the timestamp and the principal (if available) into one field.</p>
 * <p>Usually set for {@link com.foreach.across.modules.entity.newviews.ViewElementMode#LIST_VALUE}.</p>
 *
 * @author Arne Vandamme
 * @see AuditableEntityUiConfiguration
 */
public class AuditablePropertyViewElementBuilder implements ViewElementBuilder
{
	private boolean forLastModifiedProperty;
	private ConversionService conversionService;

	/**
	 * Configure the builder for the last modified property instead of the creation property.
	 *
	 * @param forLastModifiedProperty true if the last modified property should be used
	 */
	public void setForLastModifiedProperty( boolean forLastModifiedProperty ) {
		this.forLastModifiedProperty = forLastModifiedProperty;
	}

	/**
	 * Set the {@link ConversionService} to be used for formatting dates.
	 * Can be {@code null} in case a {@link org.springframework.format.Printer} is provided.
	 *
	 * @param conversionService to use
	 */
	public void setConversionService( ConversionService conversionService ) {
		this.conversionService = conversionService;
	}

	@Override
	public ViewElement build( ViewElementBuilderContext builderContext ) {
		Auditable auditable = EntityViewElementUtils.currentEntity( builderContext, Auditable.class );

		if ( auditable != null ) {
			Object principal = auditable.getCreatedBy();
			Date date = auditable.getCreatedDate();

			if ( forLastModifiedProperty ) {
				principal = auditable.getLastModifiedBy();
				date = auditable.getLastModifiedDate();
			}

			String principalString = Objects.toString( principal, "" );

			return new TextViewElement(
					convertToString( date )
							+ ( !StringUtils.isBlank( principalString ) ? " by " + principalString : "" )
			);
		}

		return null;
	}

	private String convertToString( Date date ) {
		if ( conversionService != null ) {
			return conversionService.convert( date, String.class );
		}

		return "";
	}
}

