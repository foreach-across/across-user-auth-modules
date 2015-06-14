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
import com.foreach.across.modules.entity.newviews.EntityViewElementBuilderContext;
import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.elements.CommonViewElements;
import com.foreach.across.modules.entity.views.elements.ViewElementMode;
import com.foreach.across.modules.entity.views.elements.ViewElements;
import com.foreach.across.modules.entity.views.elements.button.ButtonViewElement;
import com.foreach.across.modules.entity.views.elements.container.ColumnsViewElement;
import com.foreach.across.modules.entity.views.elements.container.ContainerViewElement;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.entity.web.EntityLinkBuilder;
import org.springframework.ui.ModelMap;

/**
 * @author Arne Vandamme
 */
public class EntityFormViewFactory<V extends ViewCreationContext> extends ConfigurablePropertiesEntityViewFactorySupport<V, EntityFormView>
{
	public static final String FORM_GRID = "_formGrid";
	public static final String FORM_LEFT = "_formGrid_left";
	public static final String FORM_RIGHT = "_formGrid_right";

	@Override
	protected EntityFormView createEntityView( ModelMap model ) {
		return new EntityFormView( model );
	}

	@Override
	protected void preProcessEntityView( V creationContext, EntityFormView view ) {
		EntityModel entityModel = creationContext.getEntityConfiguration().getEntityModel();

		Object entity = retrieveOrCreateEntity( entityModel, view );
		view.setEntity( entity );

		Object original = view.getOriginalEntity();

		if ( original == null ) {
			original = entity;
		}

		boolean newEntity = entityModel.isNew( entity );
		view.addAttribute( "existing", !newEntity );
		view.setFormAction( newEntity
				                    ? view.getEntityLinkBuilder().create()
				                    : view.getEntityLinkBuilder().update( original )
		);

		super.preProcessEntityView( creationContext, view );
	}

	private Object retrieveOrCreateEntity( EntityModel entityModel, EntityFormView view ) {
		Object entity = view.getEntity();

		if ( entity == null ) {
			entity = entityModel.createNew();
		}

		return entity;
	}

	@Override
	protected com.foreach.across.modules.web.ui.ViewElements buildViewElements( V viewCreationContext,
	                                                                            EntityViewElementBuilderContext<EntityFormView> viewElementBuilderContext,
	                                                                            EntityMessageCodeResolver messageCodeResolver ) {
		com.foreach.across.modules.web.ui.ViewElements elements =
				super.buildViewElements( viewCreationContext, viewElementBuilderContext, messageCodeResolver );

		EntityLinkBuilder linkBuilder = viewElementBuilderContext.getEntityView().getEntityLinkBuilder();
		EntityMessages messages = viewElementBuilderContext.getEntityView().getEntityMessages();

		return bootstrapUi.form()
							//.url( linkBuilder.update( entity  ))
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

	@Override
	protected ViewElements customizeViewElements( ViewElements elements ) {
//		bootstrapUi.form()
//				.add( elements );

//		bootstrapUi.container()
//				.add(
//						bootstrapUi.button()
//						           .name("btn-save")
//						           .submit()
//						           .text( messages.messageWithFallb )
//				)

		ContainerViewElement left = new ContainerViewElement( FORM_LEFT );
		left.addAll( elements );

		ColumnsViewElement formGrid = new ColumnsViewElement( FORM_GRID );
		formGrid.add( left );
		formGrid.add( new ContainerViewElement( FORM_RIGHT ) );

		ContainerViewElement root = new ContainerViewElement( CONTAINER );
		root.add( formGrid );

		return root;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void extendViewModel( V viewCreationContext, EntityFormView view ) {
		EntityMessages messages = view.getEntityMessages();

		ContainerViewElement buttons = new ContainerViewElement();
		buttons.setName( "buttons" );

		ButtonViewElement save = new ButtonViewElement();
		save.setName( "btn-save" );
		save.setElementType( CommonViewElements.SUBMIT_BUTTON );
		save.setLabel( messages.messageWithFallback( "actions.save" ) );
		buttons.add( save );

		ButtonViewElement cancel = new ButtonViewElement();
		cancel.setName( "btn-cancel" );
		cancel.setElementType( CommonViewElements.LINK_BUTTON );
		cancel.setStyle( ButtonViewElement.Style.LINK );
		cancel.setLink( view.getEntityLinkBuilder().overview() );
		cancel.setLabel( messages.messageWithFallback( "actions.cancel" ) );
		buttons.add( cancel );

		view.getEntityProperties().add( buttons );
	}

	@Override
	protected ViewElementMode getMode() {
		return ViewElementMode.FOR_WRITING;
	}

}
