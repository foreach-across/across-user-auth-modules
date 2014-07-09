package com.foreach.across.modules.user.services;

import com.foreach.across.modules.user.TestDatabaseConfig;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.business.UserStatus;
import com.foreach.across.modules.user.dto.UserDto;
import com.foreach.across.modules.user.repositories.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.EnumSet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestUserService.Config.class)
@DirtiesContext
public class TestUserService {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserService userService;

	@Test
	public void createUserDto() throws Exception {
		User user = new User();

		user.setId( 1561 );
		user.setFirstName( "first name" );
		user.setLastName( "last name" );
		user.setUsername( "username" );
		user.setDisplayName( "my display name" );
		user.setEmail( "testcase@foreach.com" );
		String password = passwordEncoder.encode( "fuzih" );
		user.setPassword( password );

		user.setDeleted( true );
		user.setEmailConfirmed( true );
		user.setStatus( EnumSet.of( UserStatus.CREDENTIALS_NON_EXPIRED ) );

		UserDto userDto = userService.createUserDto( user );

		assertEquals( 1561, user.getId() );
		assertEquals( "first name", userDto.getFirstName() );
		assertEquals( "last name", userDto.getLastName() );
		assertEquals( "username", userDto.getUsername() );
		assertEquals( "my display name", userDto.getDisplayName() );
		assertEquals( "testcase@foreach.com", userDto.getEmail() );
		assertEquals( null, userDto.getPassword() );

		assertEquals( true, userDto.getDeleted() );
		assertEquals( true, userDto.getEmailConfirmed() );
		assertEquals( EnumSet.of( UserStatus.CREDENTIALS_NON_EXPIRED ), userDto.getStatus() );
	}

	@Configuration
	static class Config {
		@Bean
		public UserService userService() {
			return new UserServiceImpl( passwordEncoder() );
		}

		@Bean
		public PasswordEncoder passwordEncoder() {
			return new BCryptPasswordEncoder();
		}

		@Bean
		public UserRepository userRepository() {
			return mock( UserRepository.class );
		}
	}
}
