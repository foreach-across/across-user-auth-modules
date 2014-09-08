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
package com.foreach.across.modules.user.business;

import javax.persistence.Column;
import javax.persistence.Id;

//@Entity
public class UserApplicationRole
{
	@Id
	@Column(name = "user_id", nullable = false)
	private User user;

	@Id
	@Column(name = "user_application_id", nullable = false)
	private UserApplication userApplication;

	@Id
	@Column(name = "role_id", nullable = false)
	private Role role;

	public User getUser() {
		return user;
	}

	public void setUser( User user ) {
		this.user = user;
	}

	public UserApplication getUserApplication() {
		return userApplication;
	}

	public void setUserApplication( UserApplication userApplication ) {
		this.userApplication = userApplication;
	}

	public Role getRole() {
		return role;
	}

	public void setRole( Role role ) {
		this.role = role;
	}
}
