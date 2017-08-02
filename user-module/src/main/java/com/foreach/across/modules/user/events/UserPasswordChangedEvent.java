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

package com.foreach.across.modules.user.events;

import com.foreach.across.core.events.NamedAcrossEvent;
import com.foreach.across.modules.user.business.User;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Event published by {@link com.foreach.across.modules.user.controllers.AbstractChangePasswordController}
 * whenever a user password has been changed.
 *
 * @author Arne Vandamme
 * @since 2.1.0
 */
@Data
@RequiredArgsConstructor
public final class UserPasswordChangedEvent implements NamedAcrossEvent
{
	/**
	 * Id of the flow through which the password change is requested.
	 * Also used as value for {@link #getEventName()}.
	 * See also {@link #getInitiator()} for the object that published this event.
	 */
	@NonNull
	private final String flowId;

	/**
	 * User for whom password change is requested.
	 */
	@NonNull
	private final User user;

	/**
	 * Initiator that published this event, can be {@code null}.
	 */
	private final Object initiator;

	@Override
	public String getEventName() {
		return flowId;
	}
}
