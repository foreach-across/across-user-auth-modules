package com.foreach.across.modules.user.dto;

import com.foreach.across.modules.user.business.UserRestriction;
import org.junit.Test;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
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

		UserDto dto3 = new UserDto();
		dto3.setRestrictions( new HashSet<UserRestriction>() );

		Set<UserRestriction> retrievedUserRestrictions3 = dto3.getRestrictions();
		assertNotNull( "restriction cannot be null", retrievedUserRestrictions3 );
		assertEquals( 0, retrievedUserRestrictions3.size() );
		assertEquals( Collections.<UserRestriction>emptySet(), retrievedUserRestrictions3 );
	}

	@Test
	public void createNewUserIsCalculatedOnIdIfNotSetExplicitly() {
		UserDto dto = new UserDto();
		assertTrue( dto.isNewEntity() );

		dto.setId( -1 );
		assertFalse( dto.isNewEntity() );

		dto.setId( 123 );
		assertFalse( dto.isNewEntity() );

		dto.setId( 0 );
		assertTrue( dto.isNewEntity() );
	}

	@Test
	public void createNewUserCanBeSetExplicitly() {
		UserDto dto = new UserDto();
		dto.setNewEntity( false );

		assertFalse( dto.isNewEntity() );
		assertEquals( 0, dto.getId() );

		dto.setId( 0 );
		assertFalse( dto.isNewEntity() );

		dto.setId( 132 );
		assertFalse( dto.isNewEntity() );

		dto.setNewEntity( true );
		assertTrue( dto.isNewEntity() );

		dto.setId( 333 );
		assertTrue( dto.isNewEntity() );
	}
}
