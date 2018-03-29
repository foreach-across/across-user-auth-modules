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
package com.foreach.across.modules.spring.security.acl.business;

import org.springframework.security.acls.domain.AbstractPermission;
import org.springframework.security.acls.domain.CumulativePermission;

import java.util.stream.Stream;

/**
 * Redefined instance of {@link org.springframework.security.acls.domain.BasePermission} to avoid
 * confusion with the Permission from UserModule.
 * <p/>
 * To avoid collisions, applications should create their custom permissions with a minimum shift of 10.
 * This still allows for 22 unique permissions, the range below 10 should be considered reserved for Across.
 * <p/>
 * The factory methods {@link #create(int)} allow creation of a custom {@link AclPermission} using any
 * bit position that is 10 or higher.
 *
 * @author Arne Vandamme
 */
public class AclPermission extends AbstractPermission
{
	public static final AclPermission READ = new AclPermission( 1, 'R' ); // 1
	public static final AclPermission WRITE = new AclPermission( 1 << 1, 'W' ); // 2
	public static final AclPermission CREATE = new AclPermission( 1 << 2, 'C' ); // 4
	public static final AclPermission DELETE = new AclPermission( 1 << 3, 'D' ); // 8
	public static final AclPermission ADMINISTRATION = new AclPermission( 1 << 4, 'A' ); // 16

	protected AclPermission( int mask ) {
		super( mask );
	}

	protected AclPermission( int mask, char code ) {
		super( mask, code );
	}

	/**
	 * Create a new dynamic permission using the specified bit position.
	 * Because all bit positions below 10 are considered privileged, this method will throw
	 * an {@code IllegalArgumentException} if you attempt to create one. The same will happen
	 * if you attempt to go outside the integer range (max shift = 31).
	 * <p/>
	 * Specifying a position of {@code 11} will result in a permission being created with
	 * a mask of {@code 1 << 11}.
	 *
	 * @param position bit position that should be used for this permission
	 * @return permission
	 */
	public static AclPermission create( int position ) {
		return create( position, '*' );
	}

	/**
	 * Create a new dynamic permission using the specified bit position.
	 * Because all bit positions below 10 are considered privileged, this method will throw
	 * an {@code IllegalArgumentException} if you attempt to create one. The same will happen
	 * if you attempt to go outside the integer range (max shift = 31).
	 * <p/>
	 * Specifying a position of {@code 11} will result in a permission being created with
	 * a mask of {@code 1 << 11}.
	 *
	 * @param position bit position that should be used for this permission
	 * @param code     to use on this bit position when building the pattern
	 * @return permission
	 */
	public static AclPermission create( int position, char code ) {
		if ( position < 10 ) {
			throw new IllegalArgumentException( "Permission bit positions < 10 are considered reseved" );
		}
		if ( position >= 32 ) {
			throw new IllegalArgumentException( "Bit position must be < 32" );
		}

		return new AclPermission( 1 << position, code );
	}

	/**
	 * Builds a new cumulative permission, that is a combination of several others.
	 *
	 * @param permissions to combine
	 * @return cumulative permission
	 */
	public static CumulativePermission combine( AclPermission... permissions ) {
		CumulativePermission permission = new CumulativePermission();
		Stream.of( permissions ).forEach( permission::set );
		return permission;
	}
}
