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

import com.foreach.across.modules.user.business.InternalUserDirectory;
import com.foreach.across.modules.user.business.UserDirectory;
import com.foreach.across.modules.user.services.UserDirectoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.Errors;

import java.util.Collections;

import static org.mockito.Mockito.*;

/**
 * @author Marc Vanbrabant
 * @since 2.0.0
 */
public class TestUserDirectoryValidator
{
	@Mock
	private UserDirectoryService userDirectoryService;

	private UserDirectoryValidator validator;
	private Errors errors;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks( this );
		validator = new UserDirectoryValidator( userDirectoryService );
		errors = mock( Errors.class );
	}

	@Test
	public void testThatUserDirectoryHasNoDuplicateName() {
		UserDirectory userDirectory = new InternalUserDirectory();
		UserDirectory otherDirectory = new InternalUserDirectory();
		otherDirectory.setName( "directory 2" );

		when( userDirectoryService.getUserDirectories() ).thenReturn( Collections.singletonList( otherDirectory ) );

		validator.validate( userDirectory, errors );
		verify( errors ).hasFieldErrors( "name" );
		verifyNoMoreInteractions( errors );
	}

	@Test
	public void testThatUserDirectoryWithDuplicateNameFails() {
		UserDirectory userDirectory = new InternalUserDirectory();
		userDirectory.setName( "directory 1" );
		UserDirectory otherDirectory = new InternalUserDirectory();
		otherDirectory.setName( "directory 1" );

		when( userDirectoryService.getUserDirectories() ).thenReturn( Collections.singletonList( otherDirectory ) );

		validator.validate( userDirectory, errors );
		verify( errors ).hasFieldErrors( "name" );
		verify( errors ).rejectValue( "name", null, "The user directory name must be unique" );
		verifyNoMoreInteractions( errors );
	}
}
