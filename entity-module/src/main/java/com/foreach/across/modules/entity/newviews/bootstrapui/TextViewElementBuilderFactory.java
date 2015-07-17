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
import com.foreach.across.modules.entity.newviews.EntityViewElementBuilderFactorySupport;
import com.foreach.across.modules.entity.newviews.ViewElementMode;
import com.foreach.across.modules.entity.newviews.bootstrapui.processors.element.EntityPropertyValuePostProcessor;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import com.foreach.across.modules.web.ui.elements.builder.TextViewElementBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;

/**
 * Creates a text value element for a given property.
 *
 * @author Arne Vandamme
 */
public class TextViewElementBuilderFactory extends EntityViewElementBuilderFactorySupport<TextViewElementBuilder>
{
	@Autowired
	private ConversionService conversionService;

	@Autowired
	private BootstrapUiFactory bootstrapUi;

	@Override
	public boolean supports( String viewElementType ) {
		return BootstrapUiElements.TEXT.equals( viewElementType );
	}

	@Override
	protected TextViewElementBuilder createInitialBuilder( EntityPropertyDescriptor propertyDescriptor,
	                                                       EntityPropertyRegistry entityPropertyRegistry,
	                                                       EntityConfiguration entityConfiguration,
	                                                       ViewElementMode viewElementMode ) {
		return bootstrapUi.text()
		                  .postProcessor(
				                  new EntityPropertyValuePostProcessor<TextViewElement>( conversionService,
				                                                                         propertyDescriptor )
		                  );
	}
}
