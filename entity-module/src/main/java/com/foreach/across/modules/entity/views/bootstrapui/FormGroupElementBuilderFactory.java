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

import com.foreach.across.modules.bootstrapui.elements.*;
import com.foreach.across.modules.bootstrapui.elements.builder.FormGroupElementBuilder;
import com.foreach.across.modules.bootstrapui.elements.builder.LabelFormElementBuilder;
import com.foreach.across.modules.bootstrapui.elements.builder.OptionFormElementBuilder;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactorySupport;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderService;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Arne Vandamme
 */
public class FormGroupElementBuilderFactory extends EntityViewElementBuilderFactorySupport<FormGroupElementBuilder>
{
	@Autowired
	private EntityViewElementBuilderService entityViewElementBuilderService;

	@Autowired
	private BootstrapUiFactory bootstrapUi;

	private boolean renderHelpBlockBeforeControl = true;

	public FormGroupElementBuilderFactory() {
		//addProcessor( new FormGroupRequiredBuilderProcessor() );
	}

	public void setRenderHelpBlockBeforeControl( boolean renderHelpBlockBeforeControl ) {
		this.renderHelpBlockBeforeControl = renderHelpBlockBeforeControl;
	}

	@Override
	public boolean supports( String viewElementType ) {
		return BootstrapUiElements.FORM_GROUP.equals( viewElementType );
	}

	@Override
	protected FormGroupElementBuilder createInitialBuilder( EntityPropertyDescriptor propertyDescriptor,
	                                                        EntityPropertyRegistry entityPropertyRegistry,
	                                                        EntityConfiguration entityConfiguration,
	                                                        ViewElementMode viewElementMode ) {
		ViewElementMode controlMode = ViewElementMode.CONTROL;

		if ( !propertyDescriptor.isWritable() || ViewElementMode.FORM_READ.equals( viewElementMode ) ) {
			controlMode = ViewElementMode.VALUE;
		}

		ViewElementBuilder controlBuilder = entityViewElementBuilderService.getElementBuilder(
				entityConfiguration, propertyDescriptor, controlMode
		);

		DescriptionTextPostProcessor descriptionTextPostProcessor
				= new DescriptionTextPostProcessor( bootstrapUi, propertyDescriptor );

		FormGroupElementBuilder formGroup
				= bootstrapUi.formGroup()
				             .control( controlBuilder )
				             .postProcessor( descriptionTextPostProcessor )
				             .postProcessor( ( builderContext, element ) -> {
					             FormControlElement control = element.getControl( FormControlElement.class );

					             if ( control != null && control.isRequired() ) {
						             element.setRequired( true );
					             }
				             } );

		// todo: clean this up, work with separate control (?) allow list value to be without link, but other to be with
		if ( controlMode.equals( ViewElementMode.VALUE ) ) {
			formGroup.postProcessor( new ViewElementPostProcessor<FormGroupElement>()
			{
				@Override
				public void postProcess( ViewElementBuilderContext builderContext, FormGroupElement element ) {
					StaticFormElement staticFormElement = new StaticFormElement();
					staticFormElement.add( element.getControl() );
					element.setControl( staticFormElement );
				}
			} );
		}

		if ( controlBuilder instanceof OptionFormElementBuilder ) {
			// Form groups for checkbox and radio buttons are different
			formGroup.helpBlockRenderedBeforeControl( false );
		}
		else {
			ViewElementBuilder labelText = entityViewElementBuilderService.getElementBuilder(
					entityConfiguration, propertyDescriptor, ViewElementMode.LABEL
			);

			LabelFormElementBuilder labelBuilder = bootstrapUi.label().text( labelText );

			formGroup.label( labelBuilder ).helpBlockRenderedBeforeControl( renderHelpBlockBeforeControl );
		}

		return formGroup;
	}

	/**
	 * Attempts to resolve a property description (help block).
	 */
	public static class DescriptionTextPostProcessor implements ViewElementPostProcessor<FormGroupElement>
	{
		private final BootstrapUiFactory bootstrapUi;
		private final EntityPropertyDescriptor propertyDescriptor;
		private EntityMessageCodeResolver defaultMessageCodeResolver;

		public DescriptionTextPostProcessor( BootstrapUiFactory bootstrapUi,
		                                     EntityPropertyDescriptor propertyDescriptor ) {
			this.bootstrapUi = bootstrapUi;
			this.propertyDescriptor = propertyDescriptor;
		}

		public void setDefaultMessageCodeResolver( EntityMessageCodeResolver defaultMessageCodeResolver ) {
			this.defaultMessageCodeResolver = defaultMessageCodeResolver;
		}

		@Override
		public void postProcess( ViewElementBuilderContext builderContext, FormGroupElement element ) {
			EntityMessageCodeResolver codeResolver = builderContext.getAttribute( EntityMessageCodeResolver.class );

			if ( codeResolver == null ) {
				codeResolver = defaultMessageCodeResolver;
			}

			if ( codeResolver != null ) {
				String description = codeResolver.getMessageWithFallback(
						"properties." + propertyDescriptor.getName() + "[description]", ""
				);

				if ( !StringUtils.isBlank( description ) ) {
					element.setHelpBlock( bootstrapUi.helpBlock( description ).build( builderContext ) );
				}
			}
		}
	}
}
