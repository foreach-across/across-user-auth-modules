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

import com.foreach.across.modules.bootstrapui.elements.TextboxFormElement;
import com.foreach.across.modules.bootstrapui.resource.BootstrapUiFormElementsWebResources;
import com.foreach.across.modules.web.resource.WebResource;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;

import static com.foreach.across.modules.web.resource.WebResource.CSS;
import static com.foreach.across.modules.web.resource.WebResource.JAVASCRIPT_PAGE_END;
import static com.foreach.across.modules.web.resource.WebResourceRule.add;
import static com.foreach.across.modules.web.resource.WebResourceRule.addPackage;

/**
 * Auto-suggest for selecting multiple groups.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class GroupsFormElementBuilder implements ViewElementBuilder<ViewElement>
{
	@Override
	public ViewElement build( ViewElementBuilderContext builderContext ) {
		TextboxFormElement textbox = new TextboxFormElement();
		textbox.setCustomTemplate( "th/UserModule/groups :: groups-autosuggest-control(${component})" );
		textbox.setControlName( "entity.groups" );

		builderContext.getAttribute( WebResourceRegistry.class ).apply(
				addPackage( BootstrapUiFormElementsWebResources.NAME ),
				add( WebResource.javascript( "@static:/UserModule/js/user-module.js" ) )
						.withKey( "user-module" )
						.toBucket( JAVASCRIPT_PAGE_END ),
				add( WebResource.javascript( "@static:/UserModule/css/user-module.css" ) )
						.withKey( "user-module" )
						.toBucket( CSS )

		);

		return textbox;
	}
}
