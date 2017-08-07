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
import com.foreach.across.modules.user.services.UserService;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class ChangePasswordTokenBuilder
{
	private final ChangePasswordControllerProperties configuration;
	private final UserService userService;

	/**
	 * Generate a token for a particular user, using the life time set in the configuration.
	 *
	 * @param user to build the token for
	 * @return token
	 */
	public ChangePasswordToken buildChangePasswordToken( User user ) {
		return null;
	}

	/**
	 * Decode a token into a matching {@link ChangePasswordRequest}.  If a token could not be decoded at all
	 * (no user found), the value might be empty.  Else a token will always be created but it is up to the
	 * client to check the corresponding properties ({@link ChangePasswordRequest#isValidToken()} and
	 * {@link ChangePasswordRequest#isExpired()} to determine if the request is in fact allowed.
	 * <p/>
	 * A token will only be valid if the checksum matched the rest of the token.
	 *
	 * @param token to decode
	 * @return request if token could be decoded
	 */
	public Optional<ChangePasswordRequest> decodeChangePasswordToken( ChangePasswordToken token ) {
		return null;
	}

	/**
	 * Checks if the link is valid. Should return false if the checksum does not contain an identifier for a user.
	 *
	 * @param checksum
	 * @param configuration
	 * @return
	 */
	@Deprecated
	public boolean isValidLink( String checksum, ChangePasswordControllerProperties configuration ) {
		return true;
	}

	/**
	 * Retrieves the user embedded in the checksum. Should never return null. If the user does not exist, {@link #isValidLink(String, ChangePasswordControllerProperties)} should return false.
	 *
	 * @return
	 */
	@Deprecated
	public User getUser( String checksum ) {
		return null;
	}

}

