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

package com.foreach.across.modules.user.services;

import com.foreach.across.modules.user.business.UserDirectory;

import java.util.Collection;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public interface UserDirectoryService
{
	/**
	 * Get the default directory that should be used for all principals that do not have a specific
	 * directory set.  Usually the default internal directory is returned.  This is the initial
	 * user directory created with id {@link UserDirectory#DEFAULT_INTERNAL_DIRECTORY_ID}. It should never be
	 * removed.
	 *
	 * @return instance
	 */
	UserDirectory getDefaultUserDirectory();

	/**
	 * @return all user directories
	 */
	Collection<UserDirectory> getUserDirectories();

	/**
	 * Get the list of user directories that should be used for authentication, in authentication precedence order.
	 *
	 * @return active user directories sorted according to their order property
	 */
	Collection<UserDirectory> getActiveUserDirectories();

	/**
	 * Create or update a directory instance.
	 *
	 * @param userDirectory dto
	 * @return persisted entity
	 */
	UserDirectory save( UserDirectory userDirectory );
}
