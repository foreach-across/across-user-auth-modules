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
