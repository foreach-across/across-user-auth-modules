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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Sander Van Loock
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangePasswordControllerConfiguration
{
	public static final String DEFAULT_CHANGE_PASSWORD_TEMPLATE = "th/UserModule/changePassword";

	/**
	 * The form template used for rendering the change password form
	 */
	@Builder.Default
	private String changePasswordForm = DEFAULT_CHANGE_PASSWORD_TEMPLATE;

}
