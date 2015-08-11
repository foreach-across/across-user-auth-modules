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

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiFactory;
import com.foreach.across.modules.bootstrapui.elements.GlyphIcon;
import com.foreach.across.modules.bootstrapui.elements.TableViewElement;
import com.foreach.across.modules.bootstrapui.elements.builder.TableViewElementBuilder;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.entity.web.EntityLinkBuilder;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
import com.foreach.across.modules.spring.security.actions.AllowableActions;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;

/**
 * Adds common actions (update, delete) for an {@link EntityConfiguration} entity to every result item.
 */
public class EntityListActionsProcessor implements ViewElementPostProcessor<TableViewElement.Row>
{
	protected final BootstrapUiFactory bootstrapUi;
	protected final EntityConfiguration<Object> entityConfiguration;
	protected final EntityLinkBuilder linkBuilder;
	protected final EntityMessages messages;

	@SuppressWarnings("unchecked")
	public EntityListActionsProcessor( BootstrapUiFactory bootstrapUi,
	                                   EntityConfiguration entityConfiguration,
	                                   EntityLinkBuilder linkBuilder, EntityMessages messages ) {
		this.bootstrapUi = bootstrapUi;
		this.entityConfiguration = entityConfiguration;
		this.linkBuilder = linkBuilder;
		this.messages = messages;
	}

	@Override
	public void postProcess( com.foreach.across.modules.web.ui.ViewElementBuilderContext builderContext,
	                         TableViewElement.Row row ) {
		TableViewElementBuilder.Cell cell = new TableViewElementBuilder.Cell();

		Object entity = EntityViewElementUtils.currentEntity( builderContext );

		if ( entity != null ) {
			addEntityActions( cell, entity );
		}
		else {
			cell.heading( true );
		}

		row.add( cell.build( builderContext ) );
	}

	protected void addEntityActions( TableViewElementBuilder.Cell cell, Object entity ) {
		AllowableActions allowableActions = entityConfiguration.getAllowableActions( entity );

		if ( allowableActions.contains( AllowableAction.UPDATE ) ) {
			cell.add(
					bootstrapUi.button()
					           .link( linkBuilder.update( entity ) )
					           .iconOnly( new GlyphIcon( GlyphIcon.EDIT ) )
					           .text( messages.updateAction() )
			);
	      /*.add(
		           bootstrapUi.button()
                              .link()
                              .iconOnly( new GlyphIcon( GlyphIcon.REMOVE ) )
                              .text( "Delete group" )
           )*/
		}
	}
}
