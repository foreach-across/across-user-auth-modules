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
 * Event published primarily by a {@link com.foreach.across.modules.user.controllers.AbstractChangePasswordController} to verify
 * if a {@link com.foreach.across.modules.user.business.User} is allowed to change its password.
 * <p/>
 * The final value of {@link #isPasswordChangeAllowed()} after all event handlers have executed will be used to determine
 * if password change should be allowed.
 * <p/>
 * By default changing the password of a {@link User} is allowed.
 *
 * @author Arne Vandamme
 * @since 2.1.0
 */
@Data
@RequiredArgsConstructor
public final class UserPasswordChangeAllowedEvent implements NamedAcrossEvent
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

	/**
	 * Should password change be allowed or not.
	 * By default changing the password is allowed unless an event handler vetoes.
	 */
	private boolean passwordChangeAllowed = true;

	@Override
	public String getEventName() {
		return flowId;
	}
}
