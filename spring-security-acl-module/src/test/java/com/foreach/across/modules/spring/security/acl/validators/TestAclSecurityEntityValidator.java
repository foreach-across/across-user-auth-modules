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
package com.foreach.across.modules.spring.security.acl.validators;

import com.foreach.across.modules.spring.security.acl.business.AclSecurityEntity;
import com.foreach.across.modules.spring.security.acl.services.AclSecurityEntityService;
import org.junit.Test;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
public class TestAclSecurityEntityValidator
{
	@Test
	public void recursiveParentsResultsInError() {
		AclSecurityEntityService aclSecurityEntityService = mock( AclSecurityEntityService.class );
		Validator validator = new AclSecurityEntityValidator( aclSecurityEntityService );

		Errors errors = mock( Errors.class );

		AclSecurityEntity one = create( "one", null );
		when( aclSecurityEntityService.getSecurityEntityByName( "one" ) ).thenReturn( Optional.of( one ) );
		AclSecurityEntity two = create( "two", one );
		when( aclSecurityEntityService.getSecurityEntityByName( "two" ) ).thenReturn( Optional.of( two ) );
		AclSecurityEntity three = create( "three", two );
		when( aclSecurityEntityService.getSecurityEntityByName( "three" ) ).thenReturn( Optional.of( three ) );
		AclSecurityEntity four = create( "four", three );
		when( aclSecurityEntityService.getSecurityEntityByName( "four" ) ).thenReturn( Optional.of( four ) );

		one.setParent( four );

		validator.validate( one, errors );

		verify( errors ).hasFieldErrors( "name" );
		verify( errors ).hasFieldErrors( "parent" );
		verify( errors ).rejectValue( "parent", "recursiveParents" );
		verifyNoMoreInteractions( errors );
	}

	private AclSecurityEntity create( String name, AclSecurityEntity parent ) {
		AclSecurityEntity entity = new AclSecurityEntity();
		entity.setId( System.currentTimeMillis() );
		entity.setName( name );
		entity.setParent( parent );

		return entity;
	}
}
