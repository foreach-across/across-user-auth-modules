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

package com.foreach.across.modules.spring.security.acl.ui;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.modules.bootstrapui.BootstrapUiModule;
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders;
import com.foreach.across.modules.bootstrapui.elements.icons.IconSetRegistry;
import com.foreach.across.modules.bootstrapui.elements.icons.SimpleIconSet;
import com.foreach.across.modules.spring.security.acl.SpringSecurityAclModule;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.test.support.AbstractViewElementTemplateTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;

import static com.foreach.across.modules.spring.security.acl.config.icons.SpringSecurityAclModulePermissionIcons.ADD;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
@ContextConfiguration(classes = TestAclPermissionsFormItemSelectorControl.Config.class)

public class TestAclPermissionsFormItemSelectorControl extends AbstractViewElementTemplateTest
{
	private ViewElementBuilderContext builderContext = new DefaultViewElementBuilderContext();

	@Before
	public void setUp() {
		SimpleIconSet iconSet = new SimpleIconSet();
		iconSet.setDefaultIconResolver( ( name ) -> Collections.singletonList( ADD ).contains( name ) ? html.i().addCssClass( "add" ) : null );
		IconSetRegistry.addIconSet( SpringSecurityAclModule.NAME, iconSet );
	}

	@After
	public void cleanUp() {
		IconSetRegistry.removeIconSet( SpringSecurityAclModule.NAME );
	}

	@Test
	public void defaultRow() {
		renderAndExpect(
				AclPermissionsForm.selectorControl()
				                  .control( BootstrapUiBuilders.textbox() )
				                  .attribute( "test", "value" )
				                  .build( builderContext ),
				"<div class='acl-permissions-form-selector' test='value'>" +
						"<input data-bootstrapui-adapter-type=\"basic\" type='text' class='form-control' />" +
						"<button type=\"button\" class=\"acl-permissions-form-selector-button btn btn-default\">" +
						"<i class=\"add\"></i></button>" +
						"</div>"
		);
	}

	@Configuration
	protected static class Config implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			context.addModule( new BootstrapUiModule() );
		}
	}
}
