package com.foreach.across.modules.user.dto;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestUserDto
{
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
