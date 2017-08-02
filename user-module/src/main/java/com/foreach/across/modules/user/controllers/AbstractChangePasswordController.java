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
import com.foreach.across.modules.user.events.UserPasswordChangeAllowedEvent;
import com.foreach.across.modules.user.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

/**
 * @author Sander Van Loock
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractChangePasswordController
{
	public static final String ERROR_FEEDBACK_MODEL_ATTRIBUTE = "errorFeedback";
	/**
	 * Encoding used for sending emails
	 */
	public final String ENCODING = "UTF-8";

	private UserService userService;
	private JavaMailSender javaMailSender;

	private ChangePasswordControllerConfiguration configuration;

	@PostConstruct
	public void validateRequiredProperties() {
		//validate required properties properties of configuration

	}

	@PostMapping
	public String requestPasswordChange( @ModelAttribute("email") String usernameOrEmail, ModelMap model ) {
		// eerst validatie van usernameOrEmail (not blank)
		if ( StringUtils.isBlank( usernameOrEmail ) ) {
			return renderChangePasswordFormWithFeedback( usernameOrEmail, "UserModule.web.changePassword.errorFeedback.invalidValue", model );
		}

		// user ophalen volgens config
		//// indien user niet bestaat => check property showUserNotFoundFeedback
		User user = retrieveUser( usernameOrEmail );
		if ( user == null ) {
			return renderChangePasswordFormWithFeedback( usernameOrEmail, "UserModule.web.changePassword.errorFeedback.userNotFound", model );
		}

		// check geldigheid gebruiker (geldig email, niet expired, ...)
		//// indien ongeldig zelfde afhandeling als hierboven
		UserPasswordChangeAllowedEvent userPasswordChangeAllowedEvent = validateUserCanChangePassword( user );
		if ( !userPasswordChangeAllowedEvent.isPasswordChangeAllowed() ) {
			return renderChangePasswordFormWithFeedback( usernameOrEmail, userPasswordChangeAllowedEvent.getErrorFeedbackMessageCode(), model );
		}

		// verstuur email

		//toon confirmation page

//		if ( StringUtils.isBlank( email ) ) {
//			bindingResult.reject( "org.hibernate.validator.constraints.NotBlank.message" );
//		}
//		User user = userService.getUserByEmail( email );

//		MimeMessageHelper message =
//				new MimeMessageHelper( mimeMessage, true, ENCODING ); // true = multipart
//		message.setSubject( subject );
//		message.setFrom( from );
//		message.setTo( to );
//		// Create the HTML body using Thymeleaf
//		final String htmlContent = build( templateName, templateVariables );
//		message.setText( htmlContent, true ); // true = isHtml
//
//		// Send mail
//		javaMailSender.send( mimeMessage );
		return "";
	}

	@GetMapping
	public String renderChangePasswordForm( @ModelAttribute("email") String usernameOrEmail, ModelMap model ) {
		model.addAttribute( "useEmailLookup", configuration.isUseEmailLookup() );
		return configuration.getChangePasswordForm();
	}

	private String renderChangePasswordFormWithFeedback( @ModelAttribute("email") String usernameOrEmail, String errorFeedback, ModelMap model ) {
		model.addAttribute( ERROR_FEEDBACK_MODEL_ATTRIBUTE, errorFeedback );
		return renderChangePasswordForm( usernameOrEmail, model );
	}

	protected User retrieveUser( String usernameOrEmail ) {
		return configuration.isUseEmailLookup() ? userService.getUserByEmail( usernameOrEmail ) : userService.getUserByUsername( usernameOrEmail );
	}

	protected UserPasswordChangeAllowedEvent validateUserCanChangePassword( User user ) {
		return null;
	}

	@GetMapping("mail-sent")
	public String mailSent( ModelMap model ) {
		return configuration.getMailSentTemplate();
	}

	@GetMapping(path = "/change")
	public String doChange( String code,
	                        PasswordResetDto dto ) {
		return "";
	}

	@PostMapping(path = "/change")
	public String doChange(
			ModelMap model,
			@RequestParam("code") String code,
			@Valid @ModelAttribute("dto") PasswordResetDto request ) {
//		User user = userService.getUserByEmail( email );
//		if ( user != null ) {
//			LOG.debug( "Changing password of user {}", user );
//			User userDto = user.toDto();
//			userDto.setPassword( request.getPassword() );
//			userService.save( userDto );
//		}
		return "";
	}

	@Autowired
	public void setJavaMailSender( JavaMailSender javaMailSender ) {
		this.javaMailSender = javaMailSender;
	}

	@Autowired
	public void setUserService( UserService userService ) {
		this.userService = userService;
	}

	public final ChangePasswordControllerConfiguration getConfiguration() {
		return configuration;
	}

	public final void setConfiguration( ChangePasswordControllerConfiguration configuration ) {
		Assert.notNull( configuration );
		this.configuration = configuration;
	}

	private class PasswordResetDto
	{
	}
}
