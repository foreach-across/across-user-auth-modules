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

package com.foreach.across.modules.ldap.tasks;

import com.foreach.across.modules.user.business.UserDirectory;
import org.springframework.core.NamedThreadLocal;

import java.util.Optional;

/**
 * @author Marc Vanbrabant
 */
public abstract class UserDirectorySyncHolder
{

	private static final ThreadLocal<UserDirectory>
			userDirectorySyncHolder = new NamedThreadLocal<>( "UserDirectorySyncHolder" );

	/**
	 * Associate the given user directory with the current thread.  Will replace any previously configured user directory.
	 * If the user directory parameter is {@code null}, the attached context will be removed.
	 *
	 * @param userDirectory instance
	 * @return the context that has been removed or set
	 */
	public static Optional<UserDirectory> setUserDirectory( UserDirectory userDirectory ) {
		return setUserDirectory( Optional.ofNullable( userDirectory ) );
	}

	/**
	 * Associate the given context with the current thread.  Will replace any previously configured context.
	 * If the context parameter is {@code empty}, the attached context will be removed.
	 * <p/>
	 * Convenience method for easy resetting of previously retrieved context.
	 *
	 * @param userDirectory instance
	 * @return the context that has been removed or set
	 */
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public static Optional<UserDirectory> setUserDirectory( Optional<UserDirectory> userDirectory ) {
		Optional<UserDirectory> current = getUserDirectory();
		if ( userDirectory.isPresent() ) {
			userDirectorySyncHolder.set( userDirectory.get() );
		}
		else {
			clearUserDirectory();
		}

		return current;
	}

	/**
	 * @return the context attached to the current thread
	 */
	public static Optional<UserDirectory> getUserDirectory() {
		return Optional.ofNullable( userDirectorySyncHolder.get() );
	}

	/**
	 * Removes the (optional) context attached to the current thread.
	 *
	 * @return the context that has been removed
	 */
	public static Optional<UserDirectory> clearUserDirectory() {
		Optional<UserDirectory> current = getUserDirectory();
		userDirectorySyncHolder.remove();
		return current;
	}
}