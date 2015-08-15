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
package com.foreach.across.modules.entity.views.bootstrapui;

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiElements;
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiFactory;
import com.foreach.across.modules.bootstrapui.elements.HiddenFormElement;
import com.foreach.across.modules.bootstrapui.elements.builder.HiddenFormElementBuilder;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactorySupport;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

import java.io.Serializable;

/**
 * Renders a hidden input for a property.
 *
 * @author Arne Vandamme
 */
public class HiddenFormElementBuilderFactory extends EntityViewElementBuilderFactorySupport<HiddenFormElementBuilder>
{
	@Autowired
	private ConversionService conversionService;

	@Autowired
	private EntityRegistry entityRegistry;

	@Autowired
	private BootstrapUiFactory bootstrapUi;

	@Override
	public boolean supports( String viewElementType ) {
		return BootstrapUiElements.HIDDEN.equals( viewElementType );
	}

	@Override
	protected HiddenFormElementBuilder createInitialBuilder( EntityPropertyDescriptor propertyDescriptor,
	                                                         ViewElementMode viewElementMode ) {
		return bootstrapUi.hidden()
		                  .name( propertyDescriptor.getName() )
		                  .controlName( propertyDescriptor.getName() )
		                  .postProcessor(
				                  new EntityPropertyValueHiddenPostProcessor(
						                  entityRegistry, conversionService, propertyDescriptor
				                  )
		                  );
	}

	/**
	 * Writes out the id in case of a registered entity.
	 */
	public static class EntityPropertyValueHiddenPostProcessor implements ViewElementPostProcessor<HiddenFormElement>
	{
		private static final Logger LOG = LoggerFactory.getLogger( EntityPropertyValueHiddenPostProcessor.class );
		private static final TypeDescriptor STRING_TYPE = TypeDescriptor.valueOf( String.class );

		private final EntityRegistry entityRegistry;
		private final ConversionService conversionService;
		private final EntityPropertyDescriptor propertyDescriptor;

		public EntityPropertyValueHiddenPostProcessor( EntityRegistry entityRegistry,
		                                               ConversionService conversionService,
		                                               EntityPropertyDescriptor propertyDescriptor ) {
			this.entityRegistry = entityRegistry;
			this.conversionService = conversionService;
			this.propertyDescriptor = propertyDescriptor;
		}

		@Override
		@SuppressWarnings("unchecked")
		public void postProcess( ViewElementBuilderContext builderContext, HiddenFormElement element ) {
			Object entity = EntityViewElementUtils.currentEntity( builderContext );
			ValueFetcher valueFetcher = propertyDescriptor.getValueFetcher();

			if ( entity != null && valueFetcher != null ) {
				Object propertyValue = valueFetcher.getValue( entity );
				TypeDescriptor sourceType = propertyDescriptor.getPropertyTypeDescriptor();

				if ( propertyValue != null ) {
					EntityConfiguration entityConfiguration = entityRegistry.getEntityConfiguration( propertyValue );

					if ( entityConfiguration != null ) {
						Serializable entityId = entityConfiguration.getId( propertyValue );
						element.setValue( conversionService.convert( entityId, String.class ) );
					}
					else {
						if ( sourceType == null ) {
							sourceType = TypeDescriptor.forObject( propertyValue );
						}

						try {
							element.setValue( conversionService.convert( propertyValue, sourceType, STRING_TYPE ) );
						}
						catch ( ConversionException ce ) {
							LOG.warn( "Unable to convert {} to string", sourceType, ce );
						}
					}
				}
			}
		}
	}
}
