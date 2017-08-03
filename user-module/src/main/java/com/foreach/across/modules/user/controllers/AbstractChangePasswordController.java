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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
	private AcrossEventPublisher acrossEventPublisher;

	private ChangePasswordControllerProperties configuration;
	private ChangePasswordMailSender changePasswordMailSender;
	private ChangePasswordSecurityUtilities changePasswordSecurityUtilities;
	private ChangePasswordNewPasswordValidator changePasswordNewPasswordValidator;

	@PostConstruct
	public void validateRequiredProperties() {
		//validate required properties properties of configuration

	}

	@PostMapping
	public String requestPasswordChange( @ModelAttribute("email") String usernameOrEmail, ModelMap model ) {
		if ( StringUtils.isBlank( usernameOrEmail ) ) {
			return renderChangePasswordFormWithFeedback( usernameOrEmail, "UserModule.web.changePassword.errorFeedback.invalidValue", model );
		}

		User user = retrieveUser( usernameOrEmail );
		if ( user == null ) {
			return renderChangePasswordFormWithFeedback( usernameOrEmail, "UserModule.web.changePassword.errorFeedback.userNotFound", model );
		}

		UserPasswordChangeAllowedEvent userPasswordChangeAllowedEvent = validateUserCanChangePassword( user );
		if ( !userPasswordChangeAllowedEvent.isPasswordChangeAllowed() ) {
			return renderChangePasswordFormWithFeedback( usernameOrEmail, userPasswordChangeAllowedEvent.getErrorFeedbackMessageCode(), model );
		}

		changePasswordMailSender.sendChangePasswordMail( configuration, user );

		return "redirect:" + ServletUriComponentsBuilder.fromCurrentRequest().build().getPath() + "/sent";
	}

	@GetMapping
	public String renderChangePasswordForm( @ModelAttribute("email") String usernameOrEmail, ModelMap model ) {
		model.addAttribute( "useEmailLookup", configuration.isUseEmailLookup() );
		return configuration.getChangePasswordForm();
	}

	protected String renderChangePasswordFormWithFeedback( @ModelAttribute("email") String usernameOrEmail, String errorFeedback, ModelMap model ) {
		model.addAttribute( ERROR_FEEDBACK_MODEL_ATTRIBUTE, errorFeedback );
		return renderChangePasswordForm( usernameOrEmail, model );
	}

	protected User retrieveUser( String usernameOrEmail ) {
		return configuration.isUseEmailLookup() ? userService.getUserByEmail( usernameOrEmail ) : userService.getUserByUsername( usernameOrEmail );
	}

	protected UserPasswordChangeAllowedEvent validateUserCanChangePassword( User user ) {
		UserPasswordChangeAllowedEvent result = new UserPasswordChangeAllowedEvent( configuration.getFlowId(), user, this );
		if ( StringUtils.isBlank( user.getEmail() ) ) {
			LOG.warn( "Attempt to change password for user {} with no email, refusing password change.", user );
			result.setErrorFeedbackMessageCode( "UserModule.web.changePassword.errorFeedback.userEmailIsMissing" );
			result.setPasswordChangeAllowed( false );
		}
		else {
			acrossEventPublisher.publish( result );
		}

		return result;
	}

	@GetMapping("/sent")
	public String mailSent( ModelMap model ) {
		return configuration.getMailSentTemplate();
	}

	@GetMapping(path = "/change")
	public String renderNewPasswordForm( ModelMap model, @RequestParam("checksum") String checksum ) {
		if ( !changePasswordSecurityUtilities.isValidLink( checksum, configuration ) ) {
			LOG.warn( "Attempt to change password via an invalid link." );
			//TODO should this redirect?
			return renderChangePasswordFormWithFeedback( null, "UserModule.web.changePassword.errorFeedback.invalidLink", model );
		}

		return configuration.getNewPasswordForm();
	}

	@PostMapping(path = "/change")
	public String requestNewPassword(
			ModelMap model,
			@RequestParam("checksum") String checksum,
			@ModelAttribute("model") @Valid PasswordResetDto password ) {
		if ( !changePasswordSecurityUtilities.isValidLink( checksum, configuration ) ) {
			LOG.warn( "Attempt to change password via an invalid link." );
			//TODO should this redirect?
			return renderChangePasswordFormWithFeedback( null, "UserModule.web.changePassword.errorFeedback.invalidLink", model );
		}

		User user = changePassword( checksum, password );
		doUserLogin( user );

		return "redirect:" + ServletUriComponentsBuilder.fromCurrentRequest().path( configuration.getRedirectDestinationAfterChangePassword() ).build()
		                                                .getPath();
	}

	protected void doUserLogin( User user ) {
		Authentication authentication = new UsernamePasswordAuthenticationToken( user, user.getPassword(), user.getAuthorities() );
		SecurityContextHolder.getContext().setAuthentication( authentication );
	}

	private User changePassword( String checksum, PasswordResetDto password ) {
		User user = changePasswordSecurityUtilities.getUser( checksum );
		user.toDto().setPassword( password.password );
		userService.save( user );
		return user;
	}

	@InitBinder("model")
	public void bindValidator( WebDataBinder binder ) {
		binder.addValidators( changePasswordNewPasswordValidator );
	}

	@Autowired
	public final void setJavaMailSender( JavaMailSender javaMailSender ) {
		this.javaMailSender = javaMailSender;
	}

	@Autowired
	public final void setUserService( UserService userService ) {
		this.userService = userService;
	}

	@Autowired
	public final void setAcrossEventPublisher( AcrossEventPublisher acrossEventPublisher ) {
		this.acrossEventPublisher = acrossEventPublisher;
	}

	@Autowired
	public final void setChangePasswordMailSender( ChangePasswordMailSender changePasswordMailSender ) {
		this.changePasswordMailSender = changePasswordMailSender;
	}

	@Autowired
	public final void setChangePasswordSecurityUtilities( ChangePasswordSecurityUtilities changePasswordSecurityUtilities ) {
		Assert.notNull( changePasswordSecurityUtilities );
		this.changePasswordSecurityUtilities = changePasswordSecurityUtilities;
	}

	@Autowired
	public void setChangePasswordNewPasswordValidator( ChangePasswordNewPasswordValidator changePasswordNewPasswordValidator ) {
		this.changePasswordNewPasswordValidator = changePasswordNewPasswordValidator;
	}

	public final ChangePasswordControllerProperties getConfiguration() {
		return configuration;
	}

	public final void setConfiguration( ChangePasswordControllerProperties configuration ) {
		Assert.notNull( configuration );
		this.configuration = configuration;
	}

}

