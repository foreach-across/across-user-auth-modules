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

package com.foreach.across.modules.user.controllers;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ModelMap;

import static org.junit.Assert.assertEquals;

/**
 * @author Sander Van Loock
 */
public class TestAbstractChangePasswordController
{
	private AbstractChangePasswordController controller = new ChangePasswordController(
			ChangePasswordControllerConfiguration.builder()
			                                     .build() );

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void defaultChangePasswordTemplateShouldBeReturned() throws Exception {
		String actualTemplate = controller.changePassword( "test@sander.be", new ModelMap() );

		assertEquals( ChangePasswordControllerConfiguration.DEFAULT_CHANGE_PASSWORD_TEMPLATE, actualTemplate );
	}

	@Test
	public void customChangePasswordTemplateShouldBeReturned() throws Exception {
		String expectedForm = "my/custom/form";
		controller = new ChangePasswordController( ChangePasswordControllerConfiguration.builder()
		                                                                                .changePasswordForm(
				                                                                                expectedForm )
		                                                                                .build() );
		String actual = controller.changePassword( "test@sander.be", new ModelMap() );

		assertEquals( expectedForm, actual );
	}

	private class ChangePasswordController extends AbstractChangePasswordController
	{

		public ChangePasswordController( ChangePasswordControllerConfiguration configuration ) {
			super( configuration );
		}
	}
}