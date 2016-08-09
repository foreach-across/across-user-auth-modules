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

import com.foreach.across.modules.user.business.QRole;
import com.foreach.across.modules.user.business.Role;
import com.foreach.across.modules.user.repositories.RoleRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class TestRoleValidator
{
	private RoleRepository repository;
	private Validator validator;
	private Errors errors;

	@Before
	public void before() {
		repository = mock( RoleRepository.class );

		validator = new RoleValidator( repository );

		errors = mock( Errors.class );
	}

	@Test
	public void noNameCheckIfAlreadyError() {
		when( errors.hasFieldErrors( "name" ) ).thenReturn( true );

		Role role = new Role( "my auth" );
		role.setName( "My role" );

		validator.validate( role, errors );

		verify( repository, never() ).findOne( QRole.role.name.equalsIgnoreCase( "My role" ) );
		verify( repository ).findOne( QRole.role.authority.equalsIgnoreCase( "ROLE_MY_AUTH" ) );
	}

	@Test
	public void noAuthorityCheckIfAlreadyError() {
		when( errors.hasFieldErrors( "authority" ) ).thenReturn( true );

		Role role = new Role( "my auth" );
		role.setName( "My role" );

		validator.validate( role, errors );

		verify( repository ).findOne( QRole.role.name.equalsIgnoreCase( "My role" ) );
		verify( repository, never() ).findOne( QRole.role.authority.equalsIgnoreCase( "ROLE_MY_AUTH" ) );
	}

	@Test
	public void noExistingRole() {
		Role role = new Role( "my auth" );
		role.setName( "My role" );

		QRole q = QRole.role;

		when( repository.findOne( q.name.equalsIgnoreCase( "My role" ) ) )
				.thenReturn( null );

		validator.validate( role, errors );

		verify( errors ).hasFieldErrors( "name" );
		verify( errors ).hasFieldErrors( "authority" );
		verifyNoMoreInteractions( errors );
	}

	@Test
	public void sameRoleWithSameNameAndAuthority() {
		Role role = new Role( "my auth" );
		role.setId( 123L );
		role.setName( "My role" );

		QRole q = QRole.role;

		when( repository.findOne( q.name.equalsIgnoreCase( "My role" ) ) )
				.thenReturn( role );

		validator.validate( role, errors );

		verify( errors ).hasFieldErrors( "name" );
		verify( errors ).hasFieldErrors( "authority" );
		verifyNoMoreInteractions( errors );
	}

	@Test
	public void nameMustBeUnique() {
		Role role = new Role( "my auth" );
		role.setName( "My role" );

		Role other = new Role( "my auth" );
		other.setId( 123L );
		other.setName( "My role" );

		QRole q = QRole.role;
		when( repository.findOne( q.name.equalsIgnoreCase( "My role" ) ) )
				.thenReturn( other );

		validator.validate( role, errors );

		verify( errors ).hasFieldErrors( "name" );
		verify( errors ).rejectValue( "name", "alreadyExists" );
		verify( errors ).hasFieldErrors( "authority" );
		verifyNoMoreInteractions( errors );
	}

	@Test
	public void authorityMustBeUnique() {
		Role role = new Role( "my auth" );
		role.setName( "My role" );

		Role other = new Role( "my auth" );
		other.setId( 123L );
		other.setName( "My role" );

		QRole q = QRole.role;
		when( repository.findOne( q.authority.equalsIgnoreCase( "ROLE_MY_AUTH" ) ) )
				.thenReturn( other );

		validator.validate( role, errors );

		verify( errors ).hasFieldErrors( "name" );
		verify( errors ).hasFieldErrors( "authority" );
		verify( errors ).rejectValue( "authority", "alreadyExists" );
		verifyNoMoreInteractions( errors );
	}
}
