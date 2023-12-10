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

package com.foreach.across.modules.user.services;

import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalService;
import com.foreach.across.modules.user.UserModuleSettings;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.repositories.UserRepository;
import com.foreach.across.modules.user.services.support.DefaultUserDirectoryStrategy;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestUserServiceWithEmailAsUserName.Config.class)
@DirtiesContext
public class TestUserServiceWithEmailAsUserName
{
	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	public void resetMocks() {
		reset( userRepository );

		when( userRepository.save( any( User.class ) ) ).thenAnswer(
				invocationOnMock -> invocationOnMock.getArguments()[0]
		);
	}

	@Test
	public void creatingUserFailsWhenEmailAlreadyExists() throws Exception {
		long firstUserId = 498L;
		User userDto = new User();
		userDto.setId( firstUserId );
		userDto.setEmail( "498@email.com" );

		User user = new User();
		user.setId( firstUserId );

		User otherUser = new User();
		otherUser.setId( 123L );

		when( userRepository.findById( firstUserId ) ).thenReturn( Optional.of( user ) );
		when( userRepository.findByEmail( "498@email.com" ) ).thenReturn( Optional.of( otherUser ) );

		try {
			userService.save( userDto );
		}
		catch ( UserValidationException uve ) {
			List<ObjectError> errors = uve.getErrors();
			assertEquals( 1, errors.size() );
			ObjectError fieldError = errors.get( 0 );
			assertEquals( "user", fieldError.getObjectName() );
			assertEquals( "email already exists", fieldError.getDefaultMessage() );
		}

	}

	@Test
	public void creatingUserFailsWhenEmailIsNotSpecified() throws Exception {
		User userDto = new User();
		userDto.setPassword( "password" );
		try {
			userService.save( userDto );
		}
		catch ( UserValidationException uve ) {
			List<ObjectError> errors = uve.getErrors();
			assertEquals( 1, errors.size() );
			FieldError fieldError = (FieldError) errors.get( 0 );
			assertEquals( "user", fieldError.getObjectName() );
			assertEquals( "email", fieldError.getField() );
			assertEquals( "email cannot be empty", fieldError.getDefaultMessage() );
		}
	}

	@Test
	public void creatingUserWithUsernameIsStillAllowed() throws Exception {
		User userDto = new User();
		userDto.setUsername( "someusername" );
		userDto.setPassword( "password" );
		userDto.setEmail( "test@email.com" );

		userService.save( userDto );
	}

	@Test
	public void creatingOrUpdatingSetsEmailAsUsername() throws Exception {
		User userDto = new User();
		userDto.setPassword( "password" );
		userDto.setEmail( "test@there.com" );

		userService.save( userDto );
		assertEquals( "test@there.com", userDto.getUsername() );
	}

//	@Test
//	public void creatingExistingUserDoesntErrorWhenThereIsAnErrorWithEmail() {
//		String email = "fooexample.com";
//		User dummy = new User();
//		dummy.setId( 5 );
//		dummy.setEmail( email );
//		dummy.setUsername( email );
//
//		UserDto dto = new UserDto();
//		dto.setEmail( email );
//		dto.setPassword( "foo" );
//
//		when( userRepository.getByEmail( email ) ).thenReturn( dummy );
//
//		try {
//			userService.save( dto );
//		}
//		catch ( UserValidationException uve ) {
//			List<ObjectError> errors = uve.getErrors();
//			assertEquals( 2, errors.size() );
//
//			ObjectError emailExists = errors.get( 0 );
//			assertEquals( "email already exists", emailExists.getDefaultMessage() );
//
//			FieldError invalidEmail = (FieldError) errors.get( 1 );
//			assertEquals( "email", invalidEmail.getField() );
//			assertEquals( "invalid email", invalidEmail.getDefaultMessage() );
//		}
//	}

	@Configuration
	static class Config
	{
		@Bean
		public UserRepository userRepository() {
			return mock( UserRepository.class );
		}

		@Bean
		public SecurityPrincipalService securityPrincipalService() {
			return mock( SecurityPrincipalService.class );
		}

		@Bean
		public DefaultUserDirectoryStrategy defaultUserDirectoryStrategy() {
			return mock( DefaultUserDirectoryStrategy.class );
		}

		@Bean
		public UserPropertiesService userPropertiesService() {
			return mock( UserPropertiesService.class );
		}

		@Bean
		public UserModifiedNotifier userModifiedNotifier() {
			return mock( UserModifiedNotifier.class );
		}

		@Bean
		public UserService userService() {
			return spy( new UserServiceImpl() );
		}

		@Bean
		public PasswordEncoder userPasswordEncoder() {
			return new BCryptPasswordEncoder();
		}

		@Bean
		public UserModuleSettings userModuleSettings() {
			UserModuleSettings settings = new UserModuleSettings();
			settings.setUseEmailAsUsername( true );
			return settings;
		}

		@Bean
		public UserValidator userValidator() {
			return new UserValidator();
		}

		@Bean
		public EmailValidator emailValidator() {
			return new EmailValidator();
		}
	}
}
