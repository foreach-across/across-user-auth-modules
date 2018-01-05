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

package com.foreach.across.modules.spring.security.acl.services;

import lombok.Data;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.acls.domain.*;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;

import java.util.Optional;

import static com.foreach.across.modules.spring.security.acl.business.AclPermission.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestAclOperations
{
	@Mock
	private Sid john;

	@Mock
	private Sid joe;

	private MutableAcl acl;
	private AclOperations operations;

	@Before
	public void before() {
		acl = new AclImpl( new ObjectIdentityImpl( "object", 1 ), 1, mock( AclAuthorizationStrategy.class ), mock( AuditLogger.class ) );
		operations = new AclOperations( acl, new DefaultPermissionFactory() );
	}

	@Test
	public void allowPermissions() {
		operations.allow( john, READ, WRITE );
		operations.allow( joe, ADMINISTRATION );
		operations.allow( john, DELETE, WRITE );
		assertAcl(
				granted( john, READ ),
				granted( john, WRITE ),
				granted( joe, ADMINISTRATION ),
				granted( john, DELETE )
		);
	}

	@Test
	public void allowPermissionsByName() {
		operations.allow( john, "READ", "WRITE" );
		operations.allow( joe, "ADMINISTRATION" );
		operations.allow( john, "DELETE", "WRITE" );
		assertAcl(
				granted( john, READ ),
				granted( john, WRITE ),
				granted( joe, ADMINISTRATION ),
				granted( john, DELETE )
		);
	}

	@Test
	public void allowPermissionsByMask() {
		operations.allow( john, READ.getMask(), WRITE.getMask() );
		operations.allow( joe, ADMINISTRATION.getMask() );
		operations.allow( john, DELETE.getMask() | WRITE.getMask() );

		CumulativePermission combined = new CumulativePermission();
		combined.set( DELETE );
		combined.set( WRITE );
		assertAcl(
				granted( john, READ ),
				granted( john, WRITE ),
				granted( joe, ADMINISTRATION ),
				granted( john, combined )
		);
	}

	@Test
	public void denyPermissions() {
		operations.deny( john, READ, WRITE );
		operations.deny( joe, ADMINISTRATION );
		operations.deny( john, DELETE, WRITE );
		assertAcl(
				denied( john, READ ),
				denied( john, WRITE ),
				denied( joe, ADMINISTRATION ),
				denied( john, DELETE )
		);
	}

	@Test
	public void denyPermissionsByName() {
		operations.deny( john, "READ", "WRITE" );
		operations.deny( joe, "ADMINISTRATION" );
		operations.deny( john, "DELETE", "WRITE" );
		assertAcl(
				denied( john, READ ),
				denied( john, WRITE ),
				denied( joe, ADMINISTRATION ),
				denied( john, DELETE )
		);
	}

	@Test
	public void deniedPermissionsByMask() {
		operations.deny( john, READ.getMask(), WRITE.getMask() );
		operations.deny( joe, ADMINISTRATION.getMask() );
		operations.deny( john, DELETE.getMask() | WRITE.getMask() );

		CumulativePermission combined = new CumulativePermission();
		combined.set( DELETE );
		combined.set( WRITE );
		assertAcl(
				denied( john, READ ),
				denied( john, WRITE ),
				denied( joe, ADMINISTRATION ),
				denied( john, combined )
		);
	}

	@Test
	public void revokePermissions() {
		operations.allow( john, READ, WRITE );
		operations.deny( joe, READ, WRITE );
		operations.revoke( john, WRITE );
		operations.revoke( joe, READ );

		assertAcl(
				granted( john, READ ),
				denied( joe, WRITE )
		);
	}

	@Test
	public void revokePermissionsByName() {
		operations.allow( john, "READ", "WRITE" );
		operations.deny( joe, "READ", "WRITE" );
		operations.revoke( john, "WRITE" );
		operations.revoke( joe, "READ" );

		assertAcl(
				granted( john, READ ),
				denied( joe, WRITE )
		);
	}

	@Test
	public void revokePermissionsByMask() {
		operations.allow( john, READ.getMask(), WRITE.getMask() );
		operations.deny( joe, READ.getMask(), WRITE.getMask() );
		operations.revoke( john, WRITE.getMask() );
		operations.revoke( joe, READ.getMask() );

		assertAcl(
				granted( john, READ ),
				denied( joe, WRITE )
		);
	}

	@Test
	public void complexOperation() {
		operations.allow( john, READ );
		operations.deny( joe, ADMINISTRATION );
		operations.allow( john, ADMINISTRATION.getMask(), DELETE.getMask() );
		operations.revoke( john, "ADMINISTRATION" );
		operations.allow( joe, ADMINISTRATION, READ );
		operations.deny( john, "DELETE" );

		assertAcl(
				granted( john, READ ),
				granted( joe, ADMINISTRATION ),
				denied( john, DELETE ),
				granted( joe, READ )
		);
	}

	@Test
	public void complexOperationUsingApply() {
		operations.allow( john, READ, WRITE );
		operations.deny( joe, ADMINISTRATION );
		operations.apply( john, new Permission[] { ADMINISTRATION, DELETE, READ, CREATE }, new int[] { ADMINISTRATION.getMask(), -DELETE.getMask() } );

		assertAcl(
				granted( john, WRITE ),
				denied( joe, ADMINISTRATION ),
				granted( john, ADMINISTRATION ),
				denied( john, DELETE )
		);
	}

	@Test
	@SuppressWarnings("all")
	public void getAce() {
		operations.allow( john, READ );
		operations.deny( joe, READ );

		granted( john, READ ).matches( operations.getAce( john, READ ).get() );
		denied( joe, READ ).matches( operations.getAce( joe, READ ).get() );

		assertEquals( Optional.empty(), operations.getAce( john, WRITE ) );
	}

	private void assertAcl( ExpectedAce... entries ) {
		val actualEntries = acl.getEntries();
		assertEquals( entries.length, actualEntries.size() );

		for ( int i = 0; i < entries.length; i++ ) {
			entries[i].matches( actualEntries.get( i ) );
		}
	}

	private ExpectedAce granted( Sid sid, Permission permission ) {
		return new ExpectedAce( sid, permission, true );
	}

	private ExpectedAce denied( Sid sid, Permission permission ) {
		return new ExpectedAce( sid, permission, false );
	}

	@Data
	static class ExpectedAce
	{
		private final Sid sid;
		private final Permission permission;
		private final boolean granted;

		void matches( AccessControlEntry entry ) {
			assertSame( sid, entry.getSid() );
			assertEquals( permission, entry.getPermission() );
			assertEquals( granted, entry.isGranting() );
		}
	}

}
