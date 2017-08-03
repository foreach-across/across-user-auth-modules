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
import org.springframework.stereotype.Service;

@Service
public class ChangePasswordSecurityUtilities
{

	/**
	 * Checks if the link is valid. Should return false if the checksum does not contain an identifier for a user.
	 *
	 * @param checksum
	 * @param configuration
	 * @return
	 */
	public boolean isValidLink( String checksum, ChangePasswordControllerProperties configuration ) {
		return true;
	}

	/**
	 * Retrieves the user embedded in the checksum. Should never return null. If the user does not exist, {@link #isValidLink(String, ChangePasswordControllerProperties)} should return false.
	 *
	 * @return
	 */
	public User getUser( String checksum ) {
		return null;
	}
}

