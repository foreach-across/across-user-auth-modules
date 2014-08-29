package com.foreach.across.modules.user.services;

import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.dto.UserDto;
import com.foreach.across.modules.user.repositories.UserRepository;
import com.foreach.common.test.MockedLoader;
import org.hibernate.validator.internal.constraintvalidators.EmailValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockedLoader.class, classes = TestUserServiceWithEmailAsUserName.Config.class)
@DirtiesContext
public class TestUserServiceWithEmailAsUserName
{
	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Before
	public void resetMocks() {
		reset( userRepository );
	}

	@Test
	public void creatingUserFailsWhenEmailAlreadyExists() throws Exception {

		int firstUserId = 498;
		UserDto userDto = new UserDto();
		userDto.setId( firstUserId );
		userDto.setEmail( "498@email.com" );

		User user = new User();
		user.setId( firstUserId );

		User otherUser = new User();

		when( userRepository.getById( firstUserId ) ).thenReturn( user );
		when( userRepository.getByEmail( "498@email.com" ) ).thenReturn( otherUser );

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
		UserDto userDto = new UserDto();
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
		UserDto userDto = new UserDto();
		userDto.setUsername( "someusername" );
		userDto.setPassword( "password" );
		userDto.setEmail( "test@email.com" );

		userService.save( userDto );
	}

	@Test
	public void creatingOrUpdatingSetsEmailAsUsername() throws Exception {
		UserDto userDto = new UserDto();
		userDto.setPassword( "password" );
		userDto.setEmail( "test@there.com" );

		userService.save( userDto );
		assertEquals( "test@there.com", userDto.getUsername() );
	}

	@Configuration
	static class Config
	{
		@Bean
		public UserService userService() {
			return spy( new UserServiceImpl( passwordEncoder(), true, true ) );
		}

		@Bean
		public PasswordEncoder passwordEncoder() {
			return new BCryptPasswordEncoder();
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