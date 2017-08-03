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

import com.foreach.across.modules.user.events.UserPasswordChangedEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Configuration class for a change password flow implemented by an implementation of
 * {@link AbstractChangePasswordController}.
 *
 * @author Sander Van Loock
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangePasswordControllerProperties
{
	public static final String DEFAULT_CHANGE_PASSWORD_TEMPLATE = "th/UserModule/change-password/renderChangePasswordForm";
	public static final String DEFAULT_MAIL_SENT_TEMPLATE = "th/UserModule/change-password/sent";
	public static final String DEFAULT_FLOW_ID = "UserModule";

	/**
	 * Id for this change password flow configuration.  Only relevant in an application where multiple
	 * configurations might exist.  Used as event name for the {@link UserPasswordChangedEvent}.
	 */
	@Builder.Default
	private String flowId = DEFAULT_FLOW_ID;
	/**
	 * The form template used for rendering the change password form
	 */
	@Builder.Default
	private String changePasswordForm = DEFAULT_CHANGE_PASSWORD_TEMPLATE;

	@Builder.Default
	private String mailSentTemplate = DEFAULT_MAIL_SENT_TEMPLATE;

	/**
	 * If {@code false}, the required input field will be the username instead of the email address.
	 * Only users having a valid email can modify their passwords.
	 */
	private boolean useEmailLookup = false;

	/**
	 * Should feedback be given to a user if no user with that email addres/username can be found?
	 * If {@code false} a user will not be able to distinguish between a user not found or email sent.
	 */
	private boolean showUserNotFoundFeedback = true;

	/**
	 * Number of seconds that the change password link should be valid.
	 */
	private int changePasswordLinkValidityPeriodInSeconds = 86400;

	/**
	 * Base URL for the change password link that will be added in the email.
	 * When set, the request mapping will be appended to the base URL.
	 * If left to {@code null}, the request path will be used to attempt determining an URL.
	 */
	private String changePasswordLinkBaseUrl = null;

}
