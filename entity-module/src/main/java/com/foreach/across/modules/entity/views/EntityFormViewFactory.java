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
package com.foreach.across.modules.entity.views;

import com.foreach.across.modules.bootstrapui.elements.Grid;
import com.foreach.across.modules.bootstrapui.elements.Style;
import com.foreach.across.modules.entity.controllers.EntityControllerAttributes;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.entity.web.EntityLinkBuilder;
import com.foreach.across.modules.web.ui.ViewElements;
import org.springframework.ui.ModelMap;

/**
 * @author Arne Vandamme
 */
public class EntityFormViewFactory<V extends ViewCreationContext>
		extends SingleEntityViewFactory<V, EntityFormView>
{
	public static final String FORM_NAME = "entityForm";
	public static final String FORM_LEFT = "entityForm-left";
	public static final String FORM_RIGHT = "entityForm-right";

	public EntityFormViewFactory() {
		setViewElementMode( ViewElementMode.FORM_WRITE );
	}

	@Override
	protected EntityFormView createEntityView( ModelMap model ) {
		return new EntityFormView( model );
	}

	@Override
	protected ViewElements buildViewElements( V viewCreationContext,
	                                          EntityViewElementBuilderContext<EntityFormView> viewElementBuilderContext,
	                                          EntityMessageCodeResolver messageCodeResolver ) {
		ViewElements elements
				= super.buildViewElements( viewCreationContext, viewElementBuilderContext, messageCodeResolver );

		EntityLinkBuilder linkBuilder = viewElementBuilderContext.getEntityView().getEntityLinkBuilder();
		EntityMessages messages = viewElementBuilderContext.getEntityView().getEntityMessages();

		return bootstrapUi.form()
		                  .name( FORM_NAME )
		                  .commandAttribute( EntityControllerAttributes.VIEW_REQUEST )
		                  .post()
		                  .noValidate()
		                  .action( buildActionUrl( viewElementBuilderContext ) )
		                  .add(
				                  bootstrapUi.row()
				                             .add(
						                             bootstrapUi.column( Grid.Device.MD.width( Grid.Width.HALF ) )
						                                        .name( FORM_LEFT )
						                                        .addAll( elements )
				                             )
				                             .add(
						                             bootstrapUi.column( Grid.Device.MD.width( Grid.Width.HALF ) )
						                                        .name( FORM_RIGHT )
				                             )
		                  )
		                  .add(
				                  bootstrapUi.container()
				                             .name( "buttons" )
				                             .add(
						                             bootstrapUi.button()
						                                        .name( "btn-save" )
						                                        .style( Style.PRIMARY )
						                                        .submit()
						                                        .text( messages.messageWithFallback( "actions.save" ) )
				                             )
				                             .add(
						                             bootstrapUi.button()
						                                        .name( "btn-cancel" )
						                                        .link( linkBuilder.overview() )
						                                        .text(
								                                        messages.messageWithFallback( "actions.cancel" )
						                                        )
				                             )
		                  )
		                  .build( viewElementBuilderContext );
	}

	private String buildActionUrl( EntityViewElementBuilderContext<EntityFormView> viewElementBuilderContext ) {
		EntityFormView formView = viewElementBuilderContext.getEntityView();

		if ( formView.isUpdate() ) {
			return formView.getEntityLinkBuilder().update( formView.getOriginalEntity() );
		}

		return formView.getEntityLinkBuilder().create();
	}
}
