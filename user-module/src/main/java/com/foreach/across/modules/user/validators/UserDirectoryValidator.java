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

package com.foreach.across.modules.user.validators;

import com.foreach.across.modules.entity.validators.EntityValidatorSupport;
import com.foreach.across.modules.user.business.UserDirectory;
import com.foreach.across.modules.user.services.UserDirectoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import java.util.Objects;
import java.util.Optional;

/**
 * @author Marc Vanbrabant
 * @since 1.2.0
 */
public class UserDirectoryValidator extends EntityValidatorSupport<UserDirectory>
{
	private final UserDirectoryService userDirectoryService;

	@Autowired
	public UserDirectoryValidator( UserDirectoryService userDirectoryService ) {
		this.userDirectoryService = userDirectoryService;
	}

	@Override
	public boolean supports( Class<?> clazz ) {
		return UserDirectory.class.isAssignableFrom( clazz );
	}

	@Override
	protected void postValidation( UserDirectory entity, Errors errors ) {
		if ( !errors.hasFieldErrors( "name" ) ) {
			Optional<UserDirectory> userDirectoryOptional = userDirectoryService.getUserDirectories().stream().filter(
					ud -> ud != null && Objects.equals( ud.getName(), entity.getName() ) ).findFirst();
			if ( userDirectoryOptional.isPresent() ) {
				errors.rejectValue( "name", null, "The user directory name must be unique" );
			}
		}
	}
}
