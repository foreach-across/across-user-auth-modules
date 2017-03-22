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

package com.foreach.across.modules.ldap.config;

import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.processors.EntityViewProcessorAdapter;
import com.foreach.across.modules.entity.views.processors.SingleEntityFormViewProcessor;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.ldap.business.LdapConnector;
import com.foreach.across.modules.web.resource.WebResource;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.TemplateViewElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import static com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils.find;

/**
 * @author Marc Vanbrabant
 */
@Configuration
public class LdapEntitiesConfiguration implements EntityConfigurer
{
	@Autowired
	private SynchronizationInfoFormViewAdapter viewAdapter;

	@Override
	public void configure( EntitiesConfigurationBuilder configuration ) {
		configuration.withType( LdapConnector.class )
		             .createOrUpdateFormView( fvb -> fvb.viewProcessor( viewAdapter ) );
	}

	@Component
	public static class SynchronizationInfoFormViewAdapter extends EntityViewProcessorAdapter
	{

		@Override
		protected void registerWebResources( EntityViewRequest entityViewRequest,
		                                     EntityView entityView,
		                                     WebResourceRegistry webResourceRegistry ) {
			webResourceRegistry.addWithKey( WebResource.JAVASCRIPT_PAGE_END,
			                                "testLdapConnector-js",
			                                "/js/ldapmodule/testLdapConnector.js",
			                                WebResource.VIEWS );
		}

		@Override
		protected void postRender( EntityViewRequest entityViewRequest,
		                           EntityView entityView,
		                           ContainerViewElement container,
		                           ViewElementBuilderContext builderContext ) {
			find( container, SingleEntityFormViewProcessor.RIGHT_COLUMN, ContainerViewElement.class )
					.ifPresent(
							c -> c.addChild( new TemplateViewElement( "th/ldapmodule/includes/testLdapConnector" ) )
					);
		}
	}

}
