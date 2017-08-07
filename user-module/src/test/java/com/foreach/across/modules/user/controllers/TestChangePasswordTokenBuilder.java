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

import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.services.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 * @since 2.1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestChangePasswordTokenBuilder
{
	private ChangePasswordControllerProperties configuration = new ChangePasswordControllerProperties();

	@Mock
	private UserService userService;

	private ChangePasswordTokenBuilder tokenBuilder;

	@Before
	public void setUp() {
		tokenBuilder = new ChangePasswordTokenBuilder( configuration, userService );
	}

	@Test
	public void validEncodeAndDecode() {
		encodeAndDecodeUserWithId( 123L );
		encodeAndDecodeUserWithId( Long.MAX_VALUE );
		encodeAndDecodeUserWithId( Long.MIN_VALUE + 1 );
	}

	private void encodeAndDecodeUserWithId( long userId ) {
		reset( userService );

		configuration.setHashToken( UUID.randomUUID().toString() );

		long current = new Date().getTime();

		User user = new User();
		user.setId( userId );
		user.setUsername( RandomStringUtils.random( 50 ) );
		user.setPassword( RandomStringUtils.random( 20 ) );

		when( userService.getUserById( userId ) ).thenReturn( user );

		ChangePasswordToken token = tokenBuilder.buildChangePasswordToken( user );
		assertNotNull( token );
		assertNotNull( token.getToken() );
		assertEquals( 3, token.getToken().split( "-" ).length );
		assertNotNull( token.getChecksum() );
		assertEquals( 6, token.getChecksum().length() );

		ChangePasswordRequest request = tokenBuilder.decodeChangePasswordToken( token ).orElse( null );
		assertNotNull( request );
		assertTrue( request.isValid() );

		assertNotNull( request.getExpireTime() );
		assertTrue( request.getExpireTime().getTime() >= ( current + ( configuration.getChangePasswordLinkValidityPeriodInSeconds() * 1000 ) ) );
		assertSame( user, request.getUser() );
	}

	@Test
	public void tokenNotValidIfChecksumIsWrong() {
		ChangePasswordToken token = tokenBuilder.buildChangePasswordToken( user( 10L ) );

		ChangePasswordToken other = new ChangePasswordToken( token.getToken(), "123" );

		ChangePasswordRequest request = tokenBuilder.decodeChangePasswordToken( other ).orElse( null );
		assertFalse( request.isValid() );

		request = tokenBuilder.decodeChangePasswordToken( token ).orElse( null );
		assertTrue( request.isValid() );
	}

	@Test
	public void tokenNotValidIfHashTokenHasChanged() {
		configuration.setHashToken( "my-hash-token" );
		ChangePasswordToken token = tokenBuilder.buildChangePasswordToken( user( 10L ) );

		configuration.setHashToken( "my-updated-hash-token" );
		ChangePasswordRequest request = tokenBuilder.decodeChangePasswordToken( token ).orElse( null );
		assertFalse( request.isValid() );
	}

	@Test
	public void tokenNotValidIfUsernameHasChanged() {
		User user = user( 10L );
		ChangePasswordToken token = tokenBuilder.buildChangePasswordToken( user );

		user.setUsername( user.getUsername() + "_1" );
		ChangePasswordRequest request = tokenBuilder.decodeChangePasswordToken( token ).orElse( null );
		assertFalse( request.isValid() );
	}

	@Test
	public void tokenNotValidIfPasswordChanged() {
		User user = user( 10L );
		ChangePasswordToken token = tokenBuilder.buildChangePasswordToken( user );

		user.setPassword( user.getPassword() + "-updated" );
		ChangePasswordRequest request = tokenBuilder.decodeChangePasswordToken( token ).orElse( null );
		assertFalse( request.isValid() );
	}

	@Test
	public void tokenNotValidIfWrongUserId() {
		User user = user( 10L );
		ChangePasswordToken token = tokenBuilder.buildChangePasswordToken( user );

		user.setId( 123L );
		ChangePasswordRequest request = tokenBuilder.decodeChangePasswordToken( token ).orElse( null );
		assertFalse( request.isValid() );
	}

	@Test
	public void illegalTokenValues() {
		ChangePasswordToken token = tokenBuilder.buildChangePasswordToken( user( 10L ) );
		ChangePasswordToken badToken = new ChangePasswordToken( "a" + token.getToken(), token.getChecksum() );

		assertEquals( Optional.empty(), tokenBuilder.decodeChangePasswordToken( badToken ) );
		assertEquals( Optional.empty(), tokenBuilder.decodeChangePasswordToken( new ChangePasswordToken( "", "123456" ) ) );
		assertEquals( Optional.empty(), tokenBuilder.decodeChangePasswordToken( new ChangePasswordToken( "kdsmqlkfjldksjfkqdsjlmkqds", "" ) ) );
		assertEquals( Optional.empty(), tokenBuilder.decodeChangePasswordToken( new ChangePasswordToken( "jkdsojdosij", "dsfdqsqsd" ) ) );
	}

	private User user( long userId ) {
		User user = new User();
		user.setId( userId );
		user.setUsername( RandomStringUtils.random( 50 ) );
		user.setPassword( RandomStringUtils.random( 20 ) );

		reset( userService );
		when( userService.getUserById( userId ) ).thenReturn( user );

		return user;
	}
}
