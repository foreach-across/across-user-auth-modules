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

package com.foreach.across.modules.spring.security.acl;

import com.foreach.across.modules.bootstrapui.elements.icons.IconSetRegistry;
import com.foreach.across.modules.bootstrapui.elements.icons.SimpleIconSet;
import com.foreach.across.modules.spring.security.acl.ui.icons.SpringSecurityAclModulePermissionIcons;

import static com.foreach.across.modules.bootstrapui.BootstrapUiModuleIcons.ICON_SET_FONT_AWESOME_SOLID;
import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;

public class SpringSecurityAclModuleIcons
{
	public static final String ICON_SET = SpringSecurityAclModule.NAME;

	public static final SpringSecurityAclModuleIcons springSecurityAclIcons = new SpringSecurityAclModuleIcons();

	public SpringSecurityAclModulePermissionIcons permission = new SpringSecurityAclModulePermissionIcons();

	public static void registerIconSet() {
		SimpleIconSet iconSet = new SimpleIconSet();

		iconSet.add( SpringSecurityAclModulePermissionIcons.ADD, ( name ) -> IconSetRegistry.getIconSet( ICON_SET_FONT_AWESOME_SOLID ).icon( "plus-circle" ) );
		iconSet.add( SpringSecurityAclModulePermissionIcons.REMOVE, ( name ) -> IconSetRegistry.getIconSet( ICON_SET_FONT_AWESOME_SOLID ).icon( "times" )
		                                                                                       .set( css.text.muted ) );

		iconSet.add( SpringSecurityAclModulePermissionIcons.MENU_ITEM, ( name ) -> IconSetRegistry.getIconSet( ICON_SET_FONT_AWESOME_SOLID ).icon( "lock" ) );
		iconSet.add( SpringSecurityAclModulePermissionIcons.TOOLTIP,
		             ( name ) -> IconSetRegistry.getIconSet( ICON_SET_FONT_AWESOME_SOLID ).icon( "question-circle" ) );

		IconSetRegistry.addIconSet( ICON_SET, iconSet );
	}
}
