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
package com.foreach.across.modules.it.user;

import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalService;
import com.foreach.across.modules.user.business.Group;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.dto.GroupDto;
import com.foreach.across.modules.user.dto.UserDto;
import com.foreach.across.modules.user.services.GroupService;
import com.foreach.across.modules.user.services.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = { ITUserModule.Config.class })
public class ITSecurityPrincipalService
{
	@Autowired
	private UserService userService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private SecurityPrincipalService securityPrincipalService;

	@Test
	public void userPrincipals() {
		User expectedUserOne = createUser( UUID.randomUUID().toString() );
		User expectedUserTwo = createUser( UUID.randomUUID().toString() );

		assertEquals( expectedUserOne,
		              securityPrincipalService.getPrincipalByName( expectedUserOne.getPrincipalName() ) );
		assertEquals( expectedUserTwo,
		              securityPrincipalService.getPrincipalByName( expectedUserTwo.getPrincipalName() ) );
	}

	@Test
	public void groupPrincipals() {
		Group expectedGroupOne = createGroup( RandomStringUtils.randomAscii( 50 ) );
		Group expectedGroupTwo = createGroup( RandomStringUtils.randomAscii( 50 ) );

		assertEquals( expectedGroupOne,
		              securityPrincipalService.getPrincipalByName( expectedGroupOne.getPrincipalName() ) );
		assertEquals( expectedGroupTwo,
		              securityPrincipalService.getPrincipalByName( expectedGroupTwo.getPrincipalName() ) );
	}

	private Group createGroup( String name ) {
		GroupDto dto = new GroupDto();
		dto.setName( name );

		return groupService.save( dto );
	}

	private User createUser( String username ) {
		UserDto user = new UserDto();
		user.setUsername( username );
		user.setEmail( UUID.randomUUID() + "@test.com" );
		user.setPassword( "test" );
		user.setFirstName( RandomStringUtils.randomAscii( 25 ) );
		user.setLastName( RandomStringUtils.randomAscii( 25 ) );
		user.setDisplayName( RandomStringUtils.randomAscii( 50 ) );

		return userService.save( user );
	}
}
