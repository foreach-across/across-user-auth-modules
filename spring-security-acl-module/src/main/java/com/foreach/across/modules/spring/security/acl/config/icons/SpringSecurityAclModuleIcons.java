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

package com.foreach.across.modules.spring.security.acl.config.icons;

import com.foreach.across.modules.bootstrapui.elements.icons.IconSetRegistry;
import com.foreach.across.modules.bootstrapui.elements.icons.SimpleIconSet;
import com.foreach.across.modules.spring.security.acl.SpringSecurityAclModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import static com.foreach.across.modules.bootstrapui.config.FontAwesomeIconSetConfiguration.FONT_AWESOME_SOLID_ICON_SET;
import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;

@Configuration
public class SpringSecurityAclModuleIcons
{
	public final static SpringSecurityAclModuleIcons springSecurityAclIcons = new SpringSecurityAclModuleIcons();

	public SpringSecurityAclModulePermissionIcons permission = new SpringSecurityAclModulePermissionIcons();

	@Autowired
	public void createIconSet() {
		SimpleIconSet iconSet = new SimpleIconSet();

		iconSet.add( SpringSecurityAclModulePermissionIcons.ADD, ( name ) -> IconSetRegistry.getIconSet( FONT_AWESOME_SOLID_ICON_SET ).icon( "plus-circle" ) );
		iconSet.add( SpringSecurityAclModulePermissionIcons.REMOVE, ( name ) -> IconSetRegistry.getIconSet( FONT_AWESOME_SOLID_ICON_SET ).icon( "times" )
		                                                                                       .set( css.text.muted ) );

		iconSet.add( SpringSecurityAclModulePermissionIcons.MENU_ITEM, ( name ) -> IconSetRegistry.getIconSet( FONT_AWESOME_SOLID_ICON_SET ).icon( "lock" ) );
		iconSet.add( SpringSecurityAclModulePermissionIcons.TOOLTIP,
		             ( name ) -> IconSetRegistry.getIconSet( FONT_AWESOME_SOLID_ICON_SET ).icon( "question-circle" ) );

		IconSetRegistry.addIconSet( SpringSecurityAclModule.NAME, iconSet );
	}
}
