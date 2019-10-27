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

package com.foreach.across.modules.user.business;

import com.foreach.across.modules.spring.security.authority.AuthorityMatcher;
import org.junit.Test;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.security.core.GrantedAuthority;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestPermission
{
	private GenericConversionService conversionService = new DefaultConversionService();
	@Test
	public void authorityEqualsPermissionName() {
		Permission p = new Permission( "my Permission" );
		assertEquals( "my Permission", p.getAuthority() );
		assertEquals( "my Permission", Permission.authorityString( "my Permission" ) );
	}

	@Test
	public void toStringReturnsAuthority() {
		Permission p = new Permission( "some Permission" );
		assertEquals( "some Permission", p.toString() );
	}

	@Test
	public void authorityMatching() {
		Permission p = new Permission( "some permission" );

		Set<Permission> actuals = new HashSet<>();
		actuals.add( p );

		AuthorityMatcher matcher = AuthorityMatcher.allOf( "some permission" );
		Set<GrantedAuthority> actualsConverted = (Set<GrantedAuthority>) conversionService.convert( actuals, TypeDescriptor
				.collection( Set.class, TypeDescriptor.valueOf( GrantedAuthority.class ) ) );

		assertTrue( matcher.matches( actualsConverted ) );

		matcher = AuthorityMatcher.allOf( conversionService.convert( new Permission( "some permission" ), GrantedAuthority.class ) );
		assertTrue( matcher.matches( actualsConverted ) );

		matcher = AuthorityMatcher.allOf( p.toGrantedAuthority() );
		assertTrue( matcher.matches( actualsConverted ) );
	}
}
