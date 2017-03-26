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

package com.foreach.across.modules.oauth2.validators;

import com.foreach.across.modules.oauth2.business.OAuth2Client;
import com.foreach.across.modules.oauth2.repositories.OAuth2ClientRepository;
import com.foreach.across.modules.user.business.BasicSecurityPrincipal;
import com.foreach.across.modules.user.business.InternalUserDirectory;
import com.foreach.across.modules.user.business.UserDirectory;
import com.foreach.across.modules.user.services.support.DefaultUserDirectoryStrategy;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class TestOAuth2ClientValidator
{
	private OAuth2ClientRepository repository;
	private DefaultUserDirectoryStrategy defaultUserDirectoryStrategy;
	private Validator validator;
	private Errors errors;

	private UserDirectory defaultDir;

	@Before
	public void before() {
		defaultDir = new InternalUserDirectory();
		defaultDir.setId( UserDirectory.DEFAULT_INTERNAL_DIRECTORY_ID );

		repository = mock( OAuth2ClientRepository.class );
		defaultUserDirectoryStrategy = mock( DefaultUserDirectoryStrategy.class );
		doAnswer(
				i -> {
					( (BasicSecurityPrincipal) i.getArguments()[0] ).setUserDirectory( defaultDir );
					return null;
				}
		).when( defaultUserDirectoryStrategy ).apply( any( BasicSecurityPrincipal.class ) );

		validator = new OAuth2ClientValidator( repository, defaultUserDirectoryStrategy );

		errors = mock( Errors.class );
	}

	@Test
	public void noExistingOAuth2Client() {
		OAuth2Client client = new OAuth2Client();
		client.setClientId( "PRINCIPAL NAME" );

		when( repository.findOneByPrincipalName( "principal name" ) ).thenReturn( null );

		validator.validate( client, errors );

		verify( defaultUserDirectoryStrategy ).apply( client );
		verify( errors ).hasFieldErrors( "clientId" );
		verifyNoMoreInteractions( errors );
	}

	@Test
	public void sameOAuth2ClientWithSameName() {
		OAuth2Client client = new OAuth2Client();
		client.setClientId( "PRINCIPAL NAME" );

		when( repository.findOneByPrincipalName( "principal name" ) ).thenReturn( client );

		validator.validate( client, errors );

		verify( defaultUserDirectoryStrategy ).apply( client );
		verify( errors ).hasFieldErrors( "clientId" );
		verifyNoMoreInteractions( errors );
	}

	@Test
	public void principalNameMustBeUnique() {
		OAuth2Client client = new OAuth2Client();
		client.setClientId( "PRINCIPAL NAME" );

		OAuth2Client existing = new OAuth2Client();
		existing.setId( 2L );
		existing.setClientId( "principal name" );

		when( repository.findOneByPrincipalName( "principal name" ) ).thenReturn( existing );

		validator.validate( client, errors );

		verify( defaultUserDirectoryStrategy ).apply( client );
		verify( errors ).hasFieldErrors( "clientId" );
		verify( errors ).rejectValue( "clientId", "alreadyExists" );
		verifyNoMoreInteractions( errors );
	}
}
