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
package com.foreach.across.modules.entity.newviews.bootstrapui.processors.element;

import com.foreach.across.modules.bootstrapui.elements.TableViewElement;
import com.foreach.across.modules.entity.newviews.bootstrapui.util.SortableTableBuilder;
import com.foreach.across.modules.entity.newviews.util.EntityViewElementUtils;
import com.foreach.across.modules.entity.views.ViewCreationContext;
import com.foreach.across.modules.entity.web.EntityLinkBuilder;
import com.foreach.across.modules.entity.web.WebViewCreationContext;
import com.foreach.across.modules.web.resource.WebResource;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Registers an expandable detail view for an entity.
 *
 * @author Arne Vandamme
 */
public class EntitySummaryViewActionProcessor implements ViewElementPostProcessor<TableViewElement.Row>
{
	private final String viewName;

	public EntitySummaryViewActionProcessor( String viewName ) {
		this.viewName = viewName;
	}

	@Override
	public void postProcess( ViewElementBuilderContext builderContext, TableViewElement.Row element ) {
		Object entity = EntityViewElementUtils.currentEntity( builderContext );
		EntityLinkBuilder linkBuilder = builderContext.getAttribute( EntityLinkBuilder.class );

		element.setAttribute( "data-summary-url",
		                      ServletUriComponentsBuilder
				                      .fromCurrentContextPath()
				                      .path( linkBuilder.view( entity ) )
				                      .queryParam( "view", viewName )
				                      .queryParam( "_partial", "content" )
				                      .toUriString()
		);
	}

	/**
	 * Automatically registers the post processor to a {@link SortableTableBuilder} if the view with the given
	 * name is present in the configuration being rendered.
	 *
	 * @param viewCreationContext global context of the view being created
	 * @param tableBuilder        where the summary view should be added to the value rows
	 * @param viewName            name of the expanding view
	 */
	public static void autoRegister( ViewCreationContext viewCreationContext,
	                                 SortableTableBuilder tableBuilder,
	                                 String viewName ) {
		boolean hasSummaryView = viewCreationContext.isForAssociation()
				? viewCreationContext.getEntityAssociation().hasView( viewName )
				: viewCreationContext.getEntityConfiguration().hasView( viewName );

		if ( hasSummaryView ) {
			tableBuilder.addValueRowProcessor( new EntitySummaryViewActionProcessor( viewName ) );

			if ( viewCreationContext instanceof WebViewCreationContext ) {
				( (WebViewCreationContext) viewCreationContext )
						.getWebResourceRegistry()
						.add( WebResource.JAVASCRIPT_PAGE_END, "/js/entity/expandable.js", WebResource.VIEWS );
			}
		}
	}
}
