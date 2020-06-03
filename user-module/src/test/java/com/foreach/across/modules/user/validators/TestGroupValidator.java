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

import com.foreach.across.modules.user.business.*;
import com.foreach.across.modules.user.services.GroupService;
import com.foreach.across.modules.user.services.support.DefaultUserDirectoryStrategy;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
public class TestGroupValidator
{
	private GroupService groupService;
	private Validator validator;
	private DefaultUserDirectoryStrategy defaultUserDirectoryStrategy;
	private Errors errors;

	private UserDirectory defaultDir;

	@Before
	public void before() {
		defaultDir = new InternalUserDirectory();
		defaultDir.setId( UserDirectory.DEFAULT_INTERNAL_DIRECTORY_ID );

		groupService = mock( GroupService.class );
		defaultUserDirectoryStrategy = mock( DefaultUserDirectoryStrategy.class );
		doAnswer(
				i -> {
					( (BasicSecurityPrincipal) i.getArguments()[0] ).setUserDirectory( defaultDir );
					return null;
				}
		).when( defaultUserDirectoryStrategy ).apply( any( BasicSecurityPrincipal.class ) );

		validator = new GroupValidator( groupService, defaultUserDirectoryStrategy );

		errors = mock( Errors.class );
	}

	@Test
	public void noExistingGroup() {
		Group group = new Group();
		group.setName( "GROUP NAME" );

		QGroup q = QGroup.group;
		when( groupService.findOne( q.name.equalsIgnoreCase( "GROUP NAME" ).and( q.userDirectory.eq( defaultDir ) ) ) ).thenReturn( Optional.empty() );

		validator.validate( group, errors );

		verify( defaultUserDirectoryStrategy ).apply( group );
		verify( errors ).hasFieldErrors( "name" );
		verifyNoMoreInteractions( errors );
	}

	@Test
	public void sameGroupWithSameName() {
		Group group = new Group();
		group.setName( "GROUP NAME" );

		QGroup q = QGroup.group;
		when( groupService.findOne(
				q.name.equalsIgnoreCase( "GROUP NAME" ).and( q.userDirectory.eq( defaultDir ) )
		) ).thenReturn( Optional.of( group ) );

		validator.validate( group, errors );

		verify( defaultUserDirectoryStrategy ).apply( group );
		verify( errors ).hasFieldErrors( "name" );
		verifyNoMoreInteractions( errors );
	}

	@Test
	public void groupNameMustBeUniqueInSameDirectory() {
		Group group = new Group();
		group.setName( "GROUP NAME" );

		Group existing = new Group();
		existing.setName( "group name" );

		QGroup q = QGroup.group;
		when( groupService.findOne(
				q.name.equalsIgnoreCase( "GROUP NAME" ).and( q.userDirectory.eq( defaultDir ) )
		) ).thenReturn( Optional.of( existing ) );

		validator.validate( group, errors );

		verify( defaultUserDirectoryStrategy ).apply( group );
		verify( errors ).hasFieldErrors( "name" );
		verify( errors ).rejectValue( "name", "alreadyExists" );
		verifyNoMoreInteractions( errors );
	}
}
