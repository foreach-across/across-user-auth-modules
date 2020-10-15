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

package com.foreach.across.modules.user.ui.support;

import com.foreach.across.modules.entity.query.EQValue;
import com.foreach.across.modules.user.business.Role;
import com.foreach.across.modules.user.repositories.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
@ExtendWith(MockitoExtension.class)
public class TestEQValueToRoleConverter
{
	@Mock
	private RoleRepository roleRepository;

	private EQValueToRoleConverter converter;

	@BeforeEach
	public void reset() {
		converter = new EQValueToRoleConverter( roleRepository );
	}

	@Test
	public void numericIsLookupById() {
		EQValue id = new EQValue( "123" );
		when( roleRepository.findById( 123L ) ).thenReturn( Optional.of( mock( Role.class ) ) );

		assertNotNull( converter.convert( id ) );
		verify( roleRepository, never() ).findByAuthorityIgnoringCase( any( String.class ) );
	}

	@Test
	public void lookupByAuthority() {
		EQValue id = new EQValue( "admin" );
		when( roleRepository.findByAuthorityIgnoringCase( "ROLE_admin" ) ).thenReturn( Optional.of( mock( Role.class ) ) );

		assertNotNull( converter.convert( id ) );
		verify( roleRepository, never() ).findById( any( Long.class ) );
	}
}
