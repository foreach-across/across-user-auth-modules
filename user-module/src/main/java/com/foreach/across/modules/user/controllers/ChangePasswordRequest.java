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
import lombok.Data;

import java.util.Date;

/**
 * Represents a change password request as decoded from a {@link ChangePasswordToken}.
 * Note that the methods {@link #isValid()}, {@link #isValidToken()} and {@link #isExpired()} should be used
 * to determine if changing the password is in fact allowed.
 */
@Data
public class ChangePasswordRequest
{
	private final User user;
	private final Date expireTime;
	private final boolean validToken;
	private final boolean expired;

	/**
	 * @return true if token used to build this request was valid, and the request has not yet expired
	 */
	public boolean isValid() {
		return isValidToken() && !isExpired();
	}
}
