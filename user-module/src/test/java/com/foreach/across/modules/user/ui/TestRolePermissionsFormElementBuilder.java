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

import com.foreach.across.modules.entity.web.EntityViewModel;
import com.foreach.across.modules.user.business.Permission;
import com.foreach.across.modules.user.business.PermissionGroup;
import com.foreach.across.modules.user.business.Role;
import com.foreach.across.modules.user.services.PermissionService;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.TemplateViewElement;
import com.foreach.across.test.support.AbstractViewElementTemplateTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class TestRolePermissionsFormElementBuilder extends AbstractViewElementTemplateTest
{
	@Mock
	private PermissionService permissionService;

	private RolePermissionsFormElementBuilder formElementBuilder;

	@Before
	public void before() {
		MockitoAnnotations.initMocks( this );

		formElementBuilder = new RolePermissionsFormElementBuilder();
		formElementBuilder.setPermissionService( permissionService );
	}

	@Test
	public void attributeAndTemplate() {
		ViewElementBuilderContext ctx = mock( ViewElementBuilderContext.class );
		TemplateViewElement template = formElementBuilder.build( ctx );

		assertEquals( RolePermissionsFormElementBuilder.TEMPLATE, template.getCustomTemplate() );
		assertEquals( Collections.emptyMap(), template.getAttribute( RolePermissionsFormElementBuilder.ATTRIBUTE ) );
	}

	@Test
	public void outputRenderingInAlphabeticOrder() {
		PermissionGroup groupOne = new PermissionGroup();
		groupOne.setName( "group b" );

		Permission one = new Permission( "permission a", "description of permission 2" );
		one.setId( 2L );
		one.setGroup( groupOne );

		Permission two = new Permission( "permission b", "description of permission 1" );
		two.setId( 1L );
		two.setGroup( groupOne );

		PermissionGroup groupTwo = new PermissionGroup();
		groupTwo.setName( "group a" );

		Permission three = new Permission( "permission c", "description of permission 3" );
		three.setId( 3L );
		three.setGroup( groupTwo );

		Permission four = new Permission( "permission d", "description of permission 4" );
		four.setId( 4L );
		four.setGroup( groupTwo );

		when( permissionService.getPermissions() )
				.thenReturn( Arrays.asList( two, four, one, three ) );

		ViewElementBuilderContext ctx = new DefaultViewElementBuilderContext();
		ctx.setAttribute( RolePermissionsFormElementBuilder.ATTRIBUTE, Collections.emptyMap() );

		Role role = mock( Role.class );
		when( role.hasPermission( three ) ).thenReturn( true );
		ctx.setAttribute( EntityViewModel.ENTITY, role );

		ViewElement element = formElementBuilder.build( ctx );

		renderAndExpect(
				element,
				model -> model.addAllAttributes( ctx.attributeMap() ),
				"<div class='panel-group' id='permissions-accordion' role='tablist' aria-multiselectable='true'>" +
						"<div class='panel panel-default'>\n" +
						"\t\t<div class='panel-heading' role='tab' id='pg-heading1'>\n" +
						"\t\t\t<h4 class='panel-title'>\n" +
						"\t\t\t\t<a role='button' data-toggle='collapse' data-parent='#permissions-accordion' aria-expanded='true' aria-controls='pg-body1' href='#pg-body1'>\n" +
						"\t\t\t\t\tgroup a\n" +
						"\t\t\t\t</a>\n" +
						"\t\t\t</h4>\n" +
						"\t\t</div>\n" +
						"\t\t<div class='panel-collapse collapse in' role='tabpanel' aria-labelledby='pg-heading1' id='pg-body1'>\n" +
						"\t\t\t<div class='panel-body'>\n" +
						"\t\t\t\t\n" +
						"\t\t\t\t<div class='checkbox'>\n" +
						"\t\t\t\t\t<label>\n" +
						"\t\t\t\t\t\t<input type='checkbox' name='entity.permissions' checked='checked' value='3' />\n" +
						"\t\t\t\t\t\tpermission c\n" +
						"\t\t\t\t\t\t<div class='small text-muted'>description of permission 3</div>\n" +
						"\t\t\t\t\t</label>\n" +
						"\t\t\t\t\t<input type='hidden' name='_entity.restrictions' value='on' />\n" +
						"\n" +
						"\t\t\t\t</div>\n" +
						"\t\t\t\t<div class='checkbox'>\n" +
						"\t\t\t\t\t<label>\n" +
						"\t\t\t\t\t\t<input type='checkbox' name='entity.permissions' value='4' />\n" +
						"\t\t\t\t\t\tpermission d\n" +
						"\t\t\t\t\t\t<div class='small text-muted'>description of permission 4</div>\n" +
						"\t\t\t\t\t</label>\n" +
						"\t\t\t\t\t<input type='hidden' name='_entity.restrictions' value='on' />\n" +
						"\n" +
						"\t\t\t\t</div>\n" +
						"\t\t\t</div>\n" +
						"\t\t</div>\n" +
						"\t</div>\n" +
						"\t<div class='panel panel-default'>\n" +
						"\t\t<div class='panel-heading' role='tab' id='pg-heading2'>\n" +
						"\t\t\t<h4 class='panel-title'>\n" +
						"\t\t\t\t<a role='button' data-toggle='collapse' data-parent='#permissions-accordion' aria-expanded='false' aria-controls='pg-body2' href='#pg-body2' class='collapsed'>\n" +
						"\t\t\t\t\tgroup b\n" +
						"\t\t\t\t</a>\n" +
						"\t\t\t</h4>\n" +
						"\t\t</div>\n" +
						"\t\t<div class='panel-collapse collapse' role='tabpanel' aria-labelledby='pg-heading2' id='pg-body2'>\n" +
						"\t\t\t<div class='panel-body'>\n" +
						"\t\t\t\t\n" +
						"\t\t\t\t<div class='checkbox'>\n" +
						"\t\t\t\t\t<label>\n" +
						"\t\t\t\t\t\t<input type='checkbox' name='entity.permissions' value='2' />\n" +
						"\t\t\t\t\t\tpermission a\n" +
						"\t\t\t\t\t\t<div class='small text-muted'>description of permission 2</div>\n" +
						"\t\t\t\t\t</label>\n" +
						"\t\t\t\t\t<input type='hidden' name='_entity.restrictions' value='on' />\n" +
						"\n" +
						"\t\t\t\t</div>\n" +
						"\t\t\t\t<div class='checkbox'>\n" +
						"\t\t\t\t\t<label>\n" +
						"\t\t\t\t\t\t<input type='checkbox' name='entity.permissions' value='1' />\n" +
						"\t\t\t\t\t\tpermission b\n" +
						"\t\t\t\t\t\t<div class='small text-muted'>description of permission 1</div>\n" +
						"\t\t\t\t\t</label>\n" +
						"\t\t\t\t\t<input type='hidden' name='_entity.restrictions' value='on' />\n" +
						"\n" +
						"\t\t\t\t</div>\n" +
						"\t\t\t</div>\n" +
						"\t\t</div>" +
						"</div></div>"
		);
	}
}
