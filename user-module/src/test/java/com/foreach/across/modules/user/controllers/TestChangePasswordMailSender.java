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

import com.foreach.across.modules.user.business.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.IContext;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static com.foreach.across.modules.user.controllers.ChangePasswordControllerProperties.DEFAULT_CHANGE_PASSXORD_MAIL_SUBJECT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Sander Van Loock
 */
@RunWith(MockitoJUnitRunner.class)
public class TestChangePasswordMailSender
{
	@Mock
	private ChangePasswordTokenBuilder changePasswordTokenBuilder;
	@Mock
	private JavaMailSender javaMailSender;
	@Mock
	private ITemplateEngine templateEngine;
	@Mock
	private MimeMessage mimeMessage;

	private ChangePasswordMailSender changePasswordMailSender;
	private ChangePasswordControllerProperties properties;
	private String expectedMailSender;
	private User user;

	@Before
	public void setUp() throws Exception {
		changePasswordMailSender = new ChangePasswordMailSender( changePasswordTokenBuilder, javaMailSender, templateEngine );
		properties = new ChangePasswordControllerProperties();
		expectedMailSender = "noreply@test.be";
		properties.setChangePasswordMailSender( expectedMailSender );
		changePasswordMailSender.setConfiguration( properties );

		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes( new ServletRequestAttributes( request ) );

		user = new User();
		user.setEmail( "svl@foreach.be" );
		reset( changePasswordTokenBuilder, javaMailSender, templateEngine );
		when( changePasswordTokenBuilder.buildChangePasswordToken( user ) ).thenReturn( new ChangePasswordToken( "abc", "123" ) );
		when( javaMailSender.createMimeMessage() ).thenReturn( mimeMessage );
		when( templateEngine.process( anyString(), any( IContext.class ) ) ).thenReturn( "body of the mail" );
	}

	@Test
	public void sendMail() throws Exception {
		changePasswordMailSender.sendChangePasswordMail( user );

		verifyCorrectMimeMessage();
		ArgumentCaptor<IContext> contextArgumentCaptor = ArgumentCaptor.forClass( IContext.class );
		verify( templateEngine, times( 1 ) ).process( anyString(), contextArgumentCaptor.capture() );
		assertEquals( "http://localhost/change?token=abc&checksum=123", contextArgumentCaptor.getValue().getVariable( "changePasswordLink" ) );
		assertEquals( user, contextArgumentCaptor.getValue().getVariable( "user" ) );
		verify( javaMailSender, times( 1 ) ).send( mimeMessage );
	}

	@Test
	public void exceptionWhenSendingMail() throws Exception {
		doThrow( new MailSendException( "error" ) ).when( javaMailSender ).send( mimeMessage );

		changePasswordMailSender.sendChangePasswordMail( user );

		verifyCorrectMimeMessage();
		verify( javaMailSender, times( 1 ) ).send( mimeMessage );
	}

	@Test
	public void exceptionWhenCreatingMimeMessage() throws Exception {
		user.setEmail( "invalid-email" );

		changePasswordMailSender.sendChangePasswordMail( user );

		verify( templateEngine, never() ).process( anyString(), any( IContext.class ) );
		verify( javaMailSender, never() ).send( mimeMessage );
	}

	private void verifyCorrectMimeMessage() throws MessagingException {
		ArgumentCaptor<InternetAddress> senderCaptor = ArgumentCaptor.forClass( InternetAddress.class );
		verify( mimeMessage ).setFrom( senderCaptor.capture() );
		assertEquals( expectedMailSender, senderCaptor.getValue().getAddress() );
		ArgumentCaptor<InternetAddress> toCaptor = ArgumentCaptor.forClass( InternetAddress.class );
		verify( mimeMessage ).setRecipient( any( MimeMessage.RecipientType.class ), toCaptor.capture() );
		assertEquals( user.getEmail(), toCaptor.getValue().getAddress() );
		verify( mimeMessage ).setSubject( DEFAULT_CHANGE_PASSXORD_MAIL_SUBJECT, "UTF-8" );
	}
}