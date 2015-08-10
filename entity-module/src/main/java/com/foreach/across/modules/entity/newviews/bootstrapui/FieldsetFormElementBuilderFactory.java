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
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiFactory;
import com.foreach.across.modules.bootstrapui.elements.FieldsetFormElement;
import com.foreach.across.modules.bootstrapui.elements.builder.FieldsetFormElementBuilder;
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.newviews.EntityViewElementBuilderFactorySupport;
import com.foreach.across.modules.entity.newviews.EntityViewElementBuilderService;
import com.foreach.across.modules.entity.newviews.ViewElementMode;
import com.foreach.across.modules.entity.newviews.bootstrapui.processors.element.TextCodeResolverPostProcessor;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.registry.properties.meta.PropertyPersistenceMetadata;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Builds a {@link com.foreach.across.modules.bootstrapui.elements.builder.FieldsetFormElementBuilder}
 * for a property.  The property can have a {@link EntityAttributes#FIELDSET_PROPERTY_SELECTOR} attribute
 * specifying the selector that should be used to fetch the members of the fieldset.
 * If none is available and the property is embedded, a default will be created for all properties of the embedded type.
 *
 * @author Arne Vandamme
 * @see com.foreach.across.modules.entity.EntityAttributes#FIELDSET_PROPERTY_SELECTOR
 */
public class FieldsetFormElementBuilderFactory extends EntityViewElementBuilderFactorySupport<FieldsetFormElementBuilder>
{
	@Autowired
	private BootstrapUiFactory bootstrapUi;

	@Autowired
	private EntityViewElementBuilderService entityViewElementBuilderService;

	@Override
	public boolean supports( String viewElementType ) {
		return BootstrapUiElements.FIELDSET.equals( viewElementType );
	}

	@Override
	protected FieldsetFormElementBuilder createInitialBuilder( EntityPropertyDescriptor propertyDescriptor,
	                                                           EntityPropertyRegistry entityPropertyRegistry,
	                                                           EntityConfiguration entityConfiguration,
	                                                           ViewElementMode viewElementMode ) {
		FieldsetFormElementBuilder fieldset
				= bootstrapUi.fieldset()
				             .name( propertyDescriptor.getName() )
				             .legend()
				             .text( propertyDescriptor.getDisplayName() )
				             .postProcessor(
						             new TextCodeResolverPostProcessor<FieldsetFormElement.Legend>(
								             "properties." + propertyDescriptor.getName(),
								             entityConfiguration.getEntityMessageCodeResolver()
						             )
				             )
				             .and();

		EntityPropertySelector selector = retrieveMembersSelector( propertyDescriptor );

		if ( selector != null ) {
			for ( EntityPropertyDescriptor member : entityPropertyRegistry.select( selector ) ) {
				ViewElementBuilder memberBuilder = entityViewElementBuilderService.getElementBuilder(
						entityConfiguration, member, viewElementMode
				);

				if ( memberBuilder != null ) {
					fieldset.add( memberBuilder );
				}
			}
		}

		return fieldset;
	}

	private EntityPropertySelector retrieveMembersSelector( EntityPropertyDescriptor descriptor ) {
		EntityPropertySelector selector
				= descriptor.getAttribute( EntityAttributes.FIELDSET_PROPERTY_SELECTOR, EntityPropertySelector.class );

		if ( selector == null && PropertyPersistenceMetadata.isEmbeddedProperty( descriptor ) ) {
			selector = new EntityPropertySelector( descriptor.getName() + ".*" );
		}

		return selector;
	}
}
