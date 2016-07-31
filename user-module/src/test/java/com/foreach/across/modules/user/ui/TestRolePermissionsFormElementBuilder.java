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

import com.foreach.across.modules.user.business.Permission;
import com.foreach.across.modules.user.business.PermissionGroup;
import com.foreach.across.modules.user.services.PermissionService;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementBuilderContextImpl;
import com.foreach.across.test.support.AbstractViewElementTemplateTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.mockito.Mockito.*;

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
	public void attributeNotAddedIfAlreadyPresent() {
		ViewElementBuilderContext ctx = mock( ViewElementBuilderContext.class );
		when( ctx.hasAttribute( RolePermissionsFormElementBuilder.ATTRIBUTE ) ).thenReturn( true );
		formElementBuilder.build( ctx );

		verify( ctx, never() ).setAttribute( anyString(), anyObject() );
	}

	@Ignore
	@Test
	public void outputRendering() {
		PermissionGroup groupOne = new PermissionGroup();
		groupOne.setName( "group b" );

		Permission one = new Permission( "permission a", "description of permission 2" );
		one.setId( 2L );

		Permission two = new Permission( "permission b", "description of permission 1" );
		two.setId( 1L );

		PermissionGroup groupTwo = new PermissionGroup();
		groupTwo.setName( "group a" );

		ViewElementBuilderContext ctx = new ViewElementBuilderContextImpl();
		ctx.setAttribute( RolePermissionsFormElementBuilder.ATTRIBUTE, Collections.emptyMap() );

		ViewElement element = formElementBuilder.build( ctx );

		renderAndExpect(
				element,
				model -> model.addAllAttributes( ctx.attributeMap() ),
				""
		);
	}
}
