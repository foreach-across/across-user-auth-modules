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

package com.foreach.across.modules.user.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Errors;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * @author Raf Ceuls
 */
@RunWith(MockitoJUnitRunner.class)
public class TestChangePasswordNewPasswordValidator
{
	@Mock
	public Errors errors;

	@Test
	public void testValidationNoErrorsValidPasswords() {
		ChangePasswordNewPasswordValidator validator = new ChangePasswordNewPasswordValidator();
		PasswordResetDto resetDto = PasswordResetDto.builder().confirmedPassword( "xyz" ).password( "xyz" ).build();
		validator.validate( resetDto, errors );
		verify( errors, times( 1 ) ).hasFieldErrors( "password" );
		verify( errors, times( 1 ) ).hasFieldErrors( "confirmedPassword" );
		verify( errors, times( 0 ) ).rejectValue( anyString(), anyString() );
	}

	@Test
	public void testValidationErrorsOnMismatch() {
		ChangePasswordNewPasswordValidator validator = new ChangePasswordNewPasswordValidator();
		PasswordResetDto resetDto = PasswordResetDto.builder().confirmedPassword( "xyz" ).password( "zyx" ).build();
		validator.validate( resetDto, errors );
		verify( errors, times( 1 ) ).hasFieldErrors( "password" );
		verify( errors, times( 1 ) ).hasFieldErrors( "confirmedPassword" );
		verify( errors, times( 1 ) ).rejectValue( "confirmPassword", "UserModule.web.changePassword.errorFeedback.passwordsNotEqual" );
	}

	@Test
	public void testValidationErrorsOnPWValidationSkipped() {
		ChangePasswordNewPasswordValidator validator = new ChangePasswordNewPasswordValidator();
		PasswordResetDto resetDto = PasswordResetDto.builder().confirmedPassword( "xyz" ).password( "xyz" ).build();
		when(errors.hasFieldErrors( "password" )).thenReturn( true );
		validator.validate( resetDto, errors );
		verify( errors, times( 1 ) ).hasFieldErrors( "password" );
		verify( errors, times( 0 ) ).hasFieldErrors( "confirmedPassword" );
		verify( errors, times( 0 ) ).rejectValue( anyString(), anyString() );
	}


	@Test
	public void testValidationErrorsOnConfirmationValidationSkipped() {
		ChangePasswordNewPasswordValidator validator = new ChangePasswordNewPasswordValidator();
		PasswordResetDto resetDto = PasswordResetDto.builder().confirmedPassword( "xyz" ).password( "xyz" ).build();
		when(errors.hasFieldErrors( "confirmedPassword" )).thenReturn( true );
		validator.validate( resetDto, errors );
		verify( errors, times( 1 ) ).hasFieldErrors( "password" );
		verify( errors, times( 1 ) ).hasFieldErrors( "confirmedPassword" );
		verify( errors, times( 0 ) ).rejectValue( anyString(), anyString() );
	}
}