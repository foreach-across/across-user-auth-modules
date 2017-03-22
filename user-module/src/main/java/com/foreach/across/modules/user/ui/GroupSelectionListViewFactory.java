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

package com.foreach.across.modules.user.ui;

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiFactory;
import com.foreach.across.modules.bootstrapui.elements.TableViewElement;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.bootstrapui.util.SortableTableBuilder;
import com.foreach.across.modules.entity.views.processors.EntityViewProcessorAdapter;
import com.foreach.across.modules.entity.views.processors.SortableTableRenderingViewProcessor;
import com.foreach.across.modules.entity.views.processors.support.ViewElementBuilderMap;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.user.business.Group;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import com.foreach.across.modules.web.ui.elements.builder.ContainerViewElementBuilderSupport;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class is currently not used.
 *
 * @author Arne Vandamme
 */
@Deprecated
public class GroupSelectionListViewFactory extends EntityViewProcessorAdapter
{
	private final BootstrapUiFactory bootstrapUiFactory;

	@Autowired
	public GroupSelectionListViewFactory( BootstrapUiFactory bootstrapUiFactory ) {
		this.bootstrapUiFactory = bootstrapUiFactory;
	}

	@Override
	protected void render( EntityViewRequest entityViewRequest,
	                       EntityView entityView,
	                       ContainerViewElementBuilderSupport<?, ?> containerBuilder,
	                       ViewElementBuilderMap builderMap,
	                       ViewElementBuilderContext builderContext ) {
		builderMap.get( SortableTableRenderingViewProcessor.TABLE_BUILDER, SortableTableBuilder.class )
		          .hideResultNumber()
		          .headerRowProcessor( new SelectableItemHeaderPostProcessor() )
		          .valueRowProcessor( new SelectableItemValuePostProcessor() );
	}

	private class SelectableItemHeaderPostProcessor implements ViewElementPostProcessor<TableViewElement.Row>
	{
		@Override
		public void postProcess( ViewElementBuilderContext builderContext, TableViewElement.Row row ) {
			TableViewElement.Cell cell = new TableViewElement.Cell();
			cell.setHeading( true );
			cell.addChild(
					bootstrapUiFactory.checkbox()
					                  .unwrapped()
					                  .htmlId( "select-all-items" )
					                  .build( builderContext )
			);
			row.addFirstChild( cell );
		}
	}

	private class SelectableItemValuePostProcessor implements ViewElementPostProcessor<TableViewElement.Row>
	{
		@Override
		public void postProcess( ViewElementBuilderContext builderContext, TableViewElement.Row row ) {
			Group projectItem = EntityViewElementUtils.currentEntity( builderContext, Group.class );

			TableViewElement.Cell cell = new TableViewElement.Cell();
			cell.addChild( bootstrapUiFactory.checkbox()
			                                 .unwrapped()
			                                 .controlName( "extensions[groupSelector].groups" )
			                                 .value( projectItem.getId() )
			                                 //.selected( projectItemSelectionDto
			                                 //                   .hasProjectItem( projectItem ) )
			                                 .build( builderContext ) );
			row.addFirstChild( cell );
		}
	}
}
