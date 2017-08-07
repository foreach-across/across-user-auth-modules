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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
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
	public void userTokenTakesExpireTimeIntoAccount() {
		User user = new User();
		user.setId( 123L );

		when( userService.getUserById( user.getId() ) ).thenReturn( user );

		ChangePasswordToken token = tokenBuilder.buildChangePasswordToken( user );
		assertNotNull( token );
		assertNotNull( token.getToken() );
		assertEquals( 3, token.getToken().split( "-" ).length );
		assertNotNull( token.getChecksum() );
		assertEquals( 6, token.getChecksum().length() );

		System.err.println( token );

		ChangePasswordRequest request = tokenBuilder.decodeChangePasswordToken( token ).orElse( null );
		assertNotNull( request );
		assertTrue( request.isValid() );

		assertNotNull( request.getExpireTime() );
		assertSame( user, request.getUser() );
	}
}
