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
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a security token for a change password request.
 * Token consists of 2 parts:
 * <ul>
 *     <li>the main token</li>
 *     <li>the checksum (private) part that is required to validate the token</li>
 * </ul>
 * Both parts are usually used if sent by email in a secure link.
 * When using form-based validation, usually the checksum is sent through a different channel (eg. SMS)
 * and the token itself is put as hidden form var (or kept in the session).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public final class ChangePasswordToken
{
	private String token;
	private String checksum;
}
