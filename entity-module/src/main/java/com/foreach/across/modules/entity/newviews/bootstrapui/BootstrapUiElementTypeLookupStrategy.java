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
package com.foreach.across.modules.entity.newviews.bootstrapui;

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiElements;
import com.foreach.across.modules.entity.newviews.ViewElementMode;
import com.foreach.across.modules.entity.newviews.ViewElementTypeLookupStrategy;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.meta.PropertyPersistenceMetadata;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Arne Vandamme
 */
public class BootstrapUiElementTypeLookupStrategy implements ViewElementTypeLookupStrategy
{
	@Autowired
	private EntityRegistry entityRegistry;

	@Override
	public String findElementType( EntityConfiguration entityConfiguration,
	                               EntityPropertyDescriptor descriptor,
	                               ViewElementMode viewElementMode ) {
		boolean isForWriting
				= ViewElementMode.FORM_WRITE.equals( viewElementMode ) || ViewElementMode.isControl( viewElementMode );

		if ( isForWriting && !descriptor.isWritable() && descriptor.isReadable() ) {
			if ( ViewElementMode.FORM_WRITE.equals( viewElementMode ) ) {
				viewElementMode = ViewElementMode.FORM_READ;
			}
			else {
				viewElementMode = ViewElementMode.VALUE;
			}
		}

		boolean isForReading
				= ViewElementMode.FORM_READ.equals( viewElementMode ) || ViewElementMode.isValue( viewElementMode );

		if ( ( !descriptor.isWritable() && !descriptor.isReadable() ) && isForWriting ) {
			return null;
		}

		if ( !descriptor.isReadable() && isForReading ) {
			return null;
		}

		boolean isEmbedded = PropertyPersistenceMetadata.isEmbeddedProperty( descriptor );

		if ( ViewElementMode.FORM_WRITE.equals( viewElementMode )
				|| ViewElementMode.FORM_READ.equals( viewElementMode ) ) {
			if ( isEmbedded ) {
				return BootstrapUiElements.FIELDSET;
			}

			return BootstrapUiElements.FORM_GROUP;
		}

		if ( isEmbedded ) {
			return null;
		}

		if ( ViewElementMode.isValue( viewElementMode ) ) {
			return BootstrapUiElements.TEXT;
		}

		if ( ViewElementMode.isLabel( viewElementMode ) ) {
			return BootstrapUiElements.LABEL;
		}

		if ( descriptor.isWritable() ) {
			Class propertyType = descriptor.getPropertyType();

			if ( propertyType != null ) {
				if ( propertyType.isArray() || Collection.class.isAssignableFrom( propertyType ) ) {
					return BootstrapUiElements.MULTI_CHECKBOX;
				}

				if ( propertyType.isEnum() ) {
					return BootstrapUiElements.SELECT;
				}

				if ( !ClassUtils.isPrimitiveOrWrapper( propertyType ) ) {
					EntityConfiguration member = entityRegistry.getEntityConfiguration( propertyType );

					if ( member != null ) {
						return BootstrapUiElements.SELECT;
					}
				}

				if ( ClassUtils.isAssignable( Boolean.class, propertyType )
						|| ClassUtils.isAssignable( AtomicBoolean.class, propertyType ) ) {
					return BootstrapUiElements.CHECKBOX;
				}

				if ( ClassUtils.isAssignable( Date.class, propertyType ) ) {
					return BootstrapUiElements.DATETIME;
				}

				return BootstrapUiElements.TEXTBOX;
			}
		}

		return null;
	}
}
