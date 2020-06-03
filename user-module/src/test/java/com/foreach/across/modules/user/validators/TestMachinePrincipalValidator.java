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
import com.foreach.across.modules.user.repositories.MachinePrincipalRepository;
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
public class TestMachinePrincipalValidator
{
	private MachinePrincipalRepository repository;
	private DefaultUserDirectoryStrategy defaultUserDirectoryStrategy;
	private Validator validator;
	private Errors errors;

	private UserDirectory defaultDir;

	@Before
	public void before() {
		defaultDir = new InternalUserDirectory();
		defaultDir.setId( UserDirectory.DEFAULT_INTERNAL_DIRECTORY_ID );

		repository = mock( MachinePrincipalRepository.class );
		defaultUserDirectoryStrategy = mock( DefaultUserDirectoryStrategy.class );
		doAnswer(
				i -> {
					( (BasicSecurityPrincipal) i.getArguments()[0] ).setUserDirectory( defaultDir );
					return null;
				}
		).when( defaultUserDirectoryStrategy ).apply( any( BasicSecurityPrincipal.class ) );

		validator = new MachinePrincipalValidator( repository, defaultUserDirectoryStrategy );

		errors = mock( Errors.class );
	}

	@Test
	public void noExistingMachinePrincipal() {
		MachinePrincipal machinePrincipal = new MachinePrincipal();
		machinePrincipal.setName( "PRINCIPAL NAME" );

		QMachinePrincipal q = QMachinePrincipal.machinePrincipal;
		when( repository.findOne( q.name.equalsIgnoreCase( "principal name" ).and( q.userDirectory.eq( defaultDir ) ) ) ).thenReturn( Optional.empty() );

		validator.validate( machinePrincipal, errors );

		verify( defaultUserDirectoryStrategy ).apply( machinePrincipal );
		verify( errors ).hasFieldErrors( "name" );
		verify( errors ).hasFieldErrors( "groups" );
		verifyNoMoreInteractions( errors );
	}

	@Test
	public void sameMachinePrincipalWithSameName() {
		MachinePrincipal machinePrincipal = new MachinePrincipal();
		machinePrincipal.setName( "PRINCIPAL NAME" );

		QMachinePrincipal q = QMachinePrincipal.machinePrincipal;
		when( repository.findOne( q.name.equalsIgnoreCase( "principal name" ).and( q.userDirectory.eq( defaultDir ) ) ) )
				.thenReturn( Optional.of( machinePrincipal ) );

		validator.validate( machinePrincipal, errors );

		verify( defaultUserDirectoryStrategy ).apply( machinePrincipal );
		verify( errors ).hasFieldErrors( "name" );
		verify( errors ).hasFieldErrors( "groups" );
		verifyNoMoreInteractions( errors );
	}

	@Test
	public void principalNameMustBeUnique() {
		MachinePrincipal machinePrincipal = new MachinePrincipal();
		machinePrincipal.setName( "PRINCIPAL NAME" );

		MachinePrincipal existing = new MachinePrincipal();
		existing.setName( "principal name" );

		QMachinePrincipal q = QMachinePrincipal.machinePrincipal;
		when( repository
				      .findOne( q.name.equalsIgnoreCase( "principal name" ).and( q.userDirectory.eq( defaultDir ) ) ) )
				.thenReturn( Optional.of( existing ) );

		validator.validate( machinePrincipal, errors );

		verify( defaultUserDirectoryStrategy ).apply( machinePrincipal );
		verify( errors ).hasFieldErrors( "name" );
		verify( errors ).rejectValue( "name", "alreadyExists" );
		verify( errors ).hasFieldErrors( "groups" );
		verifyNoMoreInteractions( errors );
	}
}
