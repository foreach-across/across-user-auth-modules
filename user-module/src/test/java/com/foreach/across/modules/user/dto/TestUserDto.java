package com.foreach.across.modules.user.dto;

import com.foreach.across.modules.user.business.UserRestriction;
import org.junit.Test;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import static org.junit.Assert.*;

public class TestUserDto
{
	@Test
	public void testUserDtoAcceptsNullRestrictionsAndCollections() {
		UserDto dto = new UserDto();
		dto.setRestrictions( null );
		assertEquals( Collections.<UserRestriction>emptySet(), dto.getRestrictions() );

		UserDto dto2 = new UserDto();
		Set<UserRestriction> restrictions = EnumSet.of( UserRestriction.REQUIRES_CONFIRMATION, UserRestriction.LOCKED, UserRestriction.DISABLED );
		dto2.setRestrictions( restrictions );

		Set<UserRestriction> retrievedUserRestrictions = dto2.getRestrictions();
		assertNotNull( "restriction cannot be null", retrievedUserRestrictions );
		assertEquals( 3, restrictions.size() );
		assertEquals( restrictions, dto2.getRestrictions() );

		assertFalse( dto.hasRestriction( null ) );
		assertTrue( dto2.hasRestriction( UserRestriction.LOCKED ) );
	}

	@Test
	public void createNewUserIsCalculatedOnIdIfNotSetExplicitly() {
		UserDto dto = new UserDto();
		assertTrue( dto.isNewUser() );

		dto.setId( -1 );
		assertFalse( dto.isNewUser() );

		dto.setId( 123 );
		assertFalse( dto.isNewUser() );

		dto.setId( 0 );
		assertTrue( dto.isNewUser() );
	}

	@Test
	public void createNewUserCanBeSetExplicitly() {
		UserDto dto = new UserDto();
		dto.setNewUser( false );

		assertFalse( dto.isNewUser() );
		assertEquals( 0, dto.getId() );

		dto.setId( 0 );
		assertFalse( dto.isNewUser() );

		dto.setId( 132 );
		assertFalse( dto.isNewUser() );

		dto.setNewUser( true );
		assertTrue( dto.isNewUser() );

		dto.setId( 333 );
		assertTrue( dto.isNewUser() );
	}
}
