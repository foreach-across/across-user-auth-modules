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
import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Map;

/**
 * @author Sander Van Loock
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChangePasswordMailSender
{
	private final ChangePasswordTokenBuilder changePasswordTokenBuilder;
	private final JavaMailSender javaMailSender;
	private final ITemplateEngine templateEngine;

	private ChangePasswordControllerProperties configuration;

	/**
	 * Construct a MIME-message and send it to the email address of the given user.
	 * <p/>
	 * The {@code JavaMailSender} will be used for sending the emails.  Only when no exceptions occur when parsing the message and sending the email, the em
	 * ail will be delivered synchronously.
	 * <p/>
	 * The email will have HTML content, rendered via {@link ITemplateEngine}.  Both the link to change the password and the user will be passed
	 * to the engine as template variables.
	 *
	 * @param user
	 */
	public void sendChangePasswordMail( User user ) {
		//create token
		ChangePasswordToken changePasswordToken = changePasswordTokenBuilder.buildChangePasswordToken( user );
		String changePasswordLink = ServletUriComponentsBuilder.fromCurrentRequest()
		                                                       .pathSegment( "change" )
		                                                       .queryParam( "token", changePasswordToken.getToken() )
		                                                       .queryParam( "checksum", changePasswordToken.getChecksum() )
		                                                       .build()
		                                                       .toString();

		//construct mail with token and user as params
		final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper message;
		try {
			message = new MimeMessageHelper( mimeMessage, true, "UTF-8" );
			message.setValidateAddresses( true );
			message.setSubject( configuration.getChangePasswordMailSubject() );
			message.setFrom( configuration.getChangePasswordMailSender() );
			message.setTo( user.getEmail() );
			ImmutableMap<String, Object> variables = ImmutableMap.of( "changePasswordLink", changePasswordLink,
			                                                          "user", user );
			// Create the HTML body using Thymeleaf
			String htmlContent = buildChangeMailHtmlContent( configuration.getChangePasswordMailTemplate(), variables );
			message.setText( htmlContent, true ); // true = isHtml

			// Send mail
			javaMailSender.send( mimeMessage );
		}
		catch ( MessagingException me ) {
			LOG.error( "Unable to create MIME message for change password email" );
		}
		catch ( MailException e ) {
			LOG.error( "Unable to send the change password email", e );
		}
	}

	/**
	 * This method can be overriden to modify the behaviour of generating the HTML content of the email.  The given templateVariables will contain the following entries:
	 * <ul>
	 * <li>changePasswordLink: Link to the form for modifying the password, including a valid token and checksum.</li>
	 * <li>user: The {@code User} that requested the password change.</li>
	 * </ul>
	 */
	protected String buildChangeMailHtmlContent( String templateName, Map<String, Object> templateVariables ) {
		Context context = new Context();
		for ( Map.Entry<String, Object> variable : templateVariables.entrySet() ) {
			context.setVariable( variable.getKey(), variable.getValue() );
		}

		return templateEngine.process( templateName, context );
	}

	public void setConfiguration( ChangePasswordControllerProperties configuration ) {
		this.configuration = configuration;
	}
}

