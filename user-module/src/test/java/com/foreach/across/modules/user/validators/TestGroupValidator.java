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

import com.foreach.across.modules.user.business.Group;
import com.foreach.across.modules.user.business.QGroup;
import com.foreach.across.modules.user.services.GroupService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
public class TestGroupValidator
{
	private GroupService groupService;
	private Validator validator;
	private Errors errors;

	@Before
	public void before() {
		groupService = mock( GroupService.class );
		validator = new GroupValidator( groupService );

		errors = mock( Errors.class );
	}

	@Test
	public void noExistingGroup() {
		Group group = new Group();
		group.setName( "GROUP NAME" );

		when( groupService.findGroup( QGroup.group.name.equalsIgnoreCase( "GROUP NAME" ) ) ).thenReturn( null );

		validator.validate( group, errors );

		verify( errors ).hasFieldErrors( "name" );
		verifyNoMoreInteractions( errors );
	}

	@Test
	public void sameGroupWithSameName() {
		Group group = new Group();
		group.setName( "GROUP NAME" );

		when( groupService.findGroup( QGroup.group.name.equalsIgnoreCase( "GROUP NAME" ) ) ).thenReturn( group );

		validator.validate( group, errors );

		verify( errors ).hasFieldErrors( "name" );
		verifyNoMoreInteractions( errors );
	}

	@Test
	public void groupNameMustBeUnique() {
		Group group = new Group();
		group.setName( "GROUP NAME" );

		Group existing = new Group();
		existing.setName( "group name" );

		when( groupService.findGroup( QGroup.group.name.equalsIgnoreCase( "GROUP NAME" ) ) ).thenReturn( existing );

		validator.validate( group, errors );

		verify( errors ).hasFieldErrors( "name" );
		verify( errors ).rejectValue( "name", "alreadyExists" );
		verifyNoMoreInteractions( errors );
	}
}
