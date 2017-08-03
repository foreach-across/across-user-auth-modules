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

import com.foreach.across.core.events.AcrossEventPublisher;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.events.UserPasswordChangeAllowedEvent;
import com.foreach.across.modules.user.services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static com.foreach.across.modules.user.controllers.AbstractChangePasswordController.ERROR_FEEDBACK_MODEL_ATTRIBUTE;
import static com.foreach.across.modules.user.controllers.ChangePasswordControllerProperties.DEFAULT_FLOW_ID;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Sander Van Loock
 */
@RunWith(MockitoJUnitRunner.class)
public class TestAbstractChangePasswordController
{
	@Mock
	private UserService userService;

	@Mock
	private JavaMailSender javaMailSender;

	@Mock
	private AcrossEventPublisher acrossEventPublisher;

	@Mock
	private ChangePasswordMailSender changePasswordMailSender;

	@Mock
	private ChangePasswordSecurityUtilities changePasswordSecurityUtilities;

	@Mock
	private BindingResult bindingResult;

	private User user = new User();

	@InjectMocks
	private AbstractChangePasswordController controller = spy( AbstractChangePasswordController.class );

	private ModelMap model;

	@Before
	public void setUp() throws Exception {
		model = new ModelMap();

		controller.setConfiguration( new ChangePasswordControllerProperties() );
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI( "/change-password" );
		RequestContextHolder.setRequestAttributes( new ServletRequestAttributes( request ) );

		when( bindingResult.hasErrors() ).thenReturn( false );
	}

	@Test(expected = IllegalArgumentException.class)
	public void validateRequiredProperties() throws Exception {
		controller.setConfiguration( null );
		controller.validateRequiredProperties();
	}

	@Test
	public void defaultChangePasswordTemplateShouldBeReturned() throws Exception {
		String actualTemplate = controller.renderChangePasswordForm( "test@sander.be", model );

		assertEquals( false, model.get( "useEmailLookup" ) );
		assertEquals( ChangePasswordControllerProperties.DEFAULT_CHANGE_PASSWORD_TEMPLATE, actualTemplate );
	}

	@Test
	public void customChangePasswordTemplateShouldBeReturned() throws Exception {
		String expectedForm = "my/custom/form";

		ChangePasswordControllerProperties customConfig =
				ChangePasswordControllerProperties.builder()
				                                  .changePasswordForm( expectedForm )
				                                  .useEmailLookup( true )
				                                  .build();
		controller.setConfiguration( customConfig );
		String actual = controller.renderChangePasswordForm( "test@sander.be", model );

		assertEquals( true, model.get( "useEmailLookup" ) );
		assertEquals( expectedForm, actual );
	}

	@Test
	public void nullEmailShouldNotTriggerChange() throws Exception {
		String actual = controller.requestPasswordChange( null, model );

		assertEquals( "UserModule.web.changePassword.errorFeedback.invalidValue", model.get( ERROR_FEEDBACK_MODEL_ATTRIBUTE ) );
		assertEquals( false, model.get( "useEmailLookup" ) );
		assertEquals( ChangePasswordControllerProperties.DEFAULT_CHANGE_PASSWORD_TEMPLATE, actual );
		verifyNoChangeHappened();
	}

	@Test
	public void emptyEmailShouldNotTriggerChange() throws Exception {
		String actual = controller.requestPasswordChange( "", model );

		assertEquals( "UserModule.web.changePassword.errorFeedback.invalidValue", model.get( ERROR_FEEDBACK_MODEL_ATTRIBUTE ) );
		assertEquals( false, model.get( "useEmailLookup" ) );
		assertEquals( ChangePasswordControllerProperties.DEFAULT_CHANGE_PASSWORD_TEMPLATE, actual );
		verifyNoChangeHappened();
	}

	@Test
	public void unknownUserShouldNotTriggerChange() throws Exception {
		BindingResult bindingResult = mock( BindingResult.class );
		String email = "email";
		when( userService.getUserByUsername( email ) ).thenReturn( null );
		controller.requestPasswordChange( email, model );

		verify( userService, times( 1 ) ).getUserByUsername( "email" );

		verifyNoChangeHappened();

	}

	@Test
	public void invalidUserShouldNotTriggerChange() throws Exception {
		when( controller.retrieveUser( "sander@localhost" ) ).thenReturn( null );

		String actual = controller.requestPasswordChange( "sander@localhost", model );
		assertEquals( "UserModule.web.changePassword.errorFeedback.userNotFound", model.get( ERROR_FEEDBACK_MODEL_ATTRIBUTE ) );
		assertEquals( false, model.get( "useEmailLookup" ) );
		assertEquals( ChangePasswordControllerProperties.DEFAULT_CHANGE_PASSWORD_TEMPLATE, actual );
		verify( userService, times( 1 ) ).getUserByUsername( "sander@localhost" );
		verifyNoChangeHappened();
	}

	@Test
	public void retrieveUserByEmail() throws Exception {
		controller.setConfiguration( ChangePasswordControllerProperties.builder()
		                                                               .useEmailLookup( true )
		                                                               .build() );
		when( userService.getUserByEmail( "sander@localhost" ) ).thenReturn( new User() );
		User user = controller.retrieveUser( "sander@localhost" );

		verify( userService, times( 1 ) ).getUserByEmail( "sander@localhost" );

	}

	@Test
	public void retrieveUserByUsername() throws Exception {
		when( userService.getUserByUsername( "sander@localhost" ) ).thenReturn( new User() );
		User user = controller.retrieveUser( "sander@localhost" );

		verify( userService, times( 1 ) ).getUserByUsername( "sander@localhost" );

	}

	@Test
	public void userCannotChangePasswordShouldNotTriggerChange() throws Exception {
		User user = new User();
		when( controller.retrieveUser( "sander@localhost" ) ).thenReturn( user );
		UserPasswordChangeAllowedEvent allowedEvent = new UserPasswordChangeAllowedEvent( "qsdmlkfk", user, null );
		allowedEvent.setPasswordChangeAllowed( false );
		when( controller.validateUserCanChangePassword( user ) ).thenReturn( allowedEvent );

		String actual = controller.requestPasswordChange( "sander@localhost", model );
		assertEquals( "UserModule.web.changePassword.errorFeedback.userNotAllowedToChangePassword", model.get( ERROR_FEEDBACK_MODEL_ATTRIBUTE ) );
		assertEquals( false, model.get( "useEmailLookup" ) );
		assertEquals( ChangePasswordControllerProperties.DEFAULT_CHANGE_PASSWORD_TEMPLATE, actual );
		verify( userService, times( 1 ) ).getUserByUsername( "sander@localhost" );
		verifyNoChangeHappened();
	}

	@Test
	public void noEmailShouldSetEvent() throws Exception {
		User user = new User();
		UserPasswordChangeAllowedEvent actualEvent = controller.validateUserCanChangePassword( user );

		verifyCorrectChangeAllowedEvent( user, actualEvent );
	}

	@Test
	public void emptyEmailShouldSetEvent() throws Exception {
		User user = new User();
		user.setEmail( "" );
		UserPasswordChangeAllowedEvent actualEvent = controller.validateUserCanChangePassword( user );

		verifyCorrectChangeAllowedEvent( user, actualEvent );
	}

	@Test
	public void publishUserCanChangePasswordEvent() throws Exception {
		User user = new User();
		user.setEmail( "sander@hotmale.com" );
		UserPasswordChangeAllowedEvent actual = controller.validateUserCanChangePassword( user );

		assertTrue( actual.isPasswordChangeAllowed() );
		assertEquals( "UserModule.web.changePassword.errorFeedback.userNotAllowedToChangePassword", actual.getErrorFeedbackMessageCode() );
		assertEquals( user, actual.getUser() );
		assertSame( controller, actual.getInitiator() );
		assertEquals( DEFAULT_FLOW_ID, actual.getFlowId() );
		verify( acrossEventPublisher, times( 1 ) ).publish( actual );
		verifyNoMoreInteractions( acrossEventPublisher );
	}

	@Test
	public void changePasswordMailIsSent() throws Exception {
		User user = new User();
		when( controller.retrieveUser( "mail" ) ).thenReturn( user );
		UserPasswordChangeAllowedEvent allowedEvent = new UserPasswordChangeAllowedEvent( DEFAULT_FLOW_ID, user, controller );
		when( controller.validateUserCanChangePassword( user ) ).thenReturn( allowedEvent );
		controller.requestPasswordChange( "mail", model );

		verify( changePasswordMailSender, times( 1 ) ).sendChangePasswordMail( controller.getConfiguration(), user );

	}

	@Test
	public void redirectToCorrectPath() throws Exception {
		User user = new User();
		when( controller.retrieveUser( "mail" ) ).thenReturn( user );
		UserPasswordChangeAllowedEvent allowedEvent = new UserPasswordChangeAllowedEvent( DEFAULT_FLOW_ID, user, controller );
		when( controller.validateUserCanChangePassword( user ) ).thenReturn( allowedEvent );
		String actualPath = controller.requestPasswordChange( "mail", model );

		assertEquals( "redirect:/change-password/sent", actualPath );
	}

	@Test
	public void confirmPasswordCorrectPath() throws Exception {

		when( changePasswordSecurityUtilities.isValidLink( "xyz", controller.getConfiguration() ) ).thenReturn( true );
		String actualPath = controller.renderNewPasswordForm( model, "xyz", new PasswordResetDto() );

		assertEquals( "th/UserModule/change-password/newPasswordForm", actualPath );
	}

	@Test
	public void confirmPasswordInvalidPasswordRedirects() throws Exception {
		when( changePasswordSecurityUtilities.isValidLink( "xyz", controller.getConfiguration() ) ).thenReturn( false );
		controller.renderNewPasswordForm( model, "xyz", new PasswordResetDto() );

		verify( controller, times( 1 ) ).renderChangePasswordFormWithFeedback( null, "UserModule.web.changePassword.errorFeedback.invalidLink", model );
	}

	@Test
	public void requestNewPasswordCorrectPath() throws Exception {
		when( changePasswordSecurityUtilities.isValidLink( "xyz", controller.getConfiguration() ) ).thenReturn( true );
		when( changePasswordSecurityUtilities.getUser( "xyz" ) ).thenReturn( user );

		PasswordResetDto validDto = PasswordResetDto.builder().password( "zyx" ).confirmedPassword( "zyx" ).build();
		String actualPath = controller.requestNewPassword( model, "xyz", validDto, bindingResult );

		assertEquals( "redirect:/change-password/", actualPath );

		verify( userService, times( 1 ) ).save( user );
		verify( controller, times( 1 ) ).doUserLogin( user );
	}

	@Test
	public void requestNewPasswordInvalidPathRedirects() throws Exception {
		when( changePasswordSecurityUtilities.isValidLink( "xyz", controller.getConfiguration() ) ).thenReturn( false );
		controller.requestNewPassword( model, "xyz", PasswordResetDto.builder().build(), bindingResult );
		verify( controller, times( 1 ) ).renderChangePasswordFormWithFeedback( null, "UserModule.web.changePassword.errorFeedback.invalidLink", model );

		verify( userService, times( 0 ) ).save( user );
		verify( controller, times( 0 ) ).doUserLogin( user );
	}

	private void verifyCorrectChangeAllowedEvent( User user, UserPasswordChangeAllowedEvent actualEvent ) {
		assertFalse( actualEvent.isPasswordChangeAllowed() );
		assertSame( controller, actualEvent.getInitiator() );
		assertEquals( user, actualEvent.getUser() );
		assertEquals( DEFAULT_FLOW_ID, actualEvent.getFlowId() );
		assertEquals( "UserModule.web.changePassword.errorFeedback.userEmailIsMissing", actualEvent.getErrorFeedbackMessageCode() );
		verifyNoMoreInteractions( acrossEventPublisher );
	}

	private void verifyNoChangeHappened() {
		verifyNoMoreInteractions( userService, javaMailSender );
	}
}