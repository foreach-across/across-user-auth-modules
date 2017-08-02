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

import com.foreach.across.modules.user.services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * @author Sander Van Loock
 */
@RunWith(MockitoJUnitRunner.class)
public class TestAbstractChangePasswordController
{
	private AbstractChangePasswordController controller;
	@Mock
	private UserService userService;

	@Mock
	private JavaMailSender javaMailSender;
	private ModelMap model;

	@Before
	public void setUp() throws Exception {
		controller = new ChangePasswordController();
		controller.setJavaMailSender( javaMailSender );
		controller.setUserService( userService );
		model = new ModelMap();

		controller.setConfiguration( ChangePasswordControllerConfiguration.builder()
		                                                                  .build() );
	}

	@Test(expected = IllegalArgumentException.class)
	public void validateRequiredProperties() throws Exception {
		controller.setConfiguration( null );
		controller.validateRequiredProperties();
	}

	@Test
	public void defaultChangePasswordTemplateShouldBeReturned() throws Exception {
		String actualTemplate = controller.changePassword( "test@sander.be", model );

		assertEquals( false, model.get( "useEmailLookup" ) );
		assertEquals( ChangePasswordControllerConfiguration.DEFAULT_CHANGE_PASSWORD_TEMPLATE, actualTemplate );
	}

	@Test
	public void customChangePasswordTemplateShouldBeReturned() throws Exception {
		String expectedForm = "my/custom/form";
		ChangePasswordControllerConfiguration customConfig =
				ChangePasswordControllerConfiguration.builder()
				                                     .changePasswordForm( expectedForm )
				                                     .useEmailLookup( true )
				                                     .build();
		controller.setConfiguration( customConfig );
		String actual = controller.changePassword( "test@sander.be", model );

		assertEquals( true, model.get( "useEmailLookup" ) );
		assertEquals( expectedForm, actual );
	}

	@Test
	public void nullEmailShouldNotTriggerChange() throws Exception {
		BindingResult bindingResult = mock( BindingResult.class );
		controller.changePassword( null, bindingResult );

		ArgumentCaptor<String> fieldCaptor = ArgumentCaptor.forClass( String.class );
		verify( bindingResult, times( 1 ) ).rejectValue( fieldCaptor.capture(), anyString() );
		assertEquals( "email", fieldCaptor.getValue() );
		verifyNoChangeHappend();
	}

	@Test
	public void emptyEmailShouldNotTriggerChange() throws Exception {
		BindingResult bindingResult = mock( BindingResult.class );
		controller.changePassword( "", bindingResult );

		ArgumentCaptor<String> fieldCaptor = ArgumentCaptor.forClass( String.class );
		verify( bindingResult, times( 1 ) ).rejectValue( fieldCaptor.capture(), anyString() );
		assertEquals( "email", fieldCaptor.getValue() );
		verifyNoChangeHappend();
	}

	@Test
	public void unknownUserShouldNotTriggerChange() throws Exception {
		BindingResult bindingResult = mock( BindingResult.class );
		String email = "email";
		when( userService.getUserByUsername( email ) ).thenReturn( null );
		controller.changePassword( email, bindingResult );

		verify( userService, times( 1 ) ).getUserByUsername( "email" );

		verifyNoChangeHappend();

	}

	private void verifyNoChangeHappend() {
		verifyNoMoreInteractions( userService, javaMailSender );
	}

	private class ChangePasswordController extends AbstractChangePasswordController
	{

	}
}