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

import com.foreach.across.modules.user.business.MachinePrincipal;
import com.foreach.across.modules.user.business.QMachinePrincipal;
import com.foreach.across.modules.user.repositories.MachinePrincipalRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
public class TestMachinePrincipalValidator
{
	private MachinePrincipalRepository repository;
	private Validator validator;
	private Errors errors;

	@Before
	public void before() {
		repository = mock( MachinePrincipalRepository.class );
		validator = new MachinePrincipalValidator( repository );

		errors = mock( Errors.class );
	}

	@Test
	public void noExistingMachinePrincipal() {
		MachinePrincipal machinePrincipal = new MachinePrincipal();
		machinePrincipal.setName( "PRINCIPAL NAME" );

		when( repository.findOne( QMachinePrincipal.machinePrincipal.name.equalsIgnoreCase( "PRINCIPAL NAME" ) ) )
				.thenReturn( null );

		validator.validate( machinePrincipal, errors );

		verify( errors ).hasFieldErrors( "name" );
		verifyNoMoreInteractions( errors );
	}

	@Test
	public void sameMachinePrincipalWithSameName() {
		MachinePrincipal machinePrincipal = new MachinePrincipal();
		machinePrincipal.setName( "PRINCIPAL NAME" );

		when( repository.findOne( QMachinePrincipal.machinePrincipal.name.equalsIgnoreCase( "PRINCIPAL NAME" ) ) )
				.thenReturn( machinePrincipal );

		validator.validate( machinePrincipal, errors );

		verify( errors ).hasFieldErrors( "name" );
		verifyNoMoreInteractions( errors );
	}

	@Test
	public void groupNameMustBeUnique() {
		MachinePrincipal machinePrincipal = new MachinePrincipal();
		machinePrincipal.setName( "PRINCIPAL NAME" );

		MachinePrincipal existing = new MachinePrincipal();
		existing.setName( "principal name" );

		when( repository.findOne( QMachinePrincipal.machinePrincipal.name.equalsIgnoreCase( "PRINCIPAL NAME" ) ) )
				.thenReturn( existing );

		validator.validate( machinePrincipal, errors );

		verify( errors ).hasFieldErrors( "name" );
		verify( errors ).rejectValue( "name", "alreadyExists" );
		verifyNoMoreInteractions( errors );
	}
}
