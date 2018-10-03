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

package com.foreach.across.modules.user.services.support;

import com.foreach.across.modules.user.business.Group;
import com.foreach.across.modules.user.business.User;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class TestExpressionBasedSecurityPrincipalResolver
{
	private ExpressionBasedSecurityPrincipalLabelResolver resolver;

	private User user;

	@Before
	public void before() {
		resolver = new ExpressionBasedSecurityPrincipalLabelResolver( User.class, "label" );

		user = new User();
		user.setUsername( "jdoe" );
		user.setFirstName( "John" );
		user.setLastName( "Doe" );
		user.setDisplayName( "johnny" );
		user.setEmail( "jdoe@gmail.com" );
	}

	@Test(expected = IllegalArgumentException.class)
	public void nullNotAllowed() {
		new ExpressionBasedSecurityPrincipalLabelResolver( null, "label" );
	}

	@Test(expected = IllegalArgumentException.class)
	public void nullNotAllowedForLabelExpression() {
		resolver.setLabelExpression( null );
	}

	@Test
	public void emptyReturnedIfPrincipalNotOfType() {
		assertEquals( Optional.empty(), resolver.resolvePrincipalLabel( null ) );
		assertEquals( Optional.empty(), resolver.resolvePrincipalLabel( new Group() ) );

		resolver = new ExpressionBasedSecurityPrincipalLabelResolver( Group.class, "name" );
		assertEquals( Optional.empty(), resolver.resolvePrincipalLabel( user ) );
	}

	@Test
	public void customExpression() {
		resolver.setLabelExpression( "username" );
		assertEquals( Optional.of( "jdoe" ), resolver.resolvePrincipalLabel( user ) );

		resolver.setLabelExpression( "displayName" );
		assertEquals( Optional.of( "johnny" ), resolver.resolvePrincipalLabel( user ) );
	}

	@Test
	public void defaultExpressionUsesDisplayNameIfPresent() {
		assertEquals( Optional.of( "johnny" ), resolver.resolvePrincipalLabel( user ) );
	}

	@Test
	public void defaultUsesLabel() {
		user.setDisplayName( "" );
		assertEquals( Optional.of( "John Doe" ), resolver.resolvePrincipalLabel( user ) );

		user.setDisplayName( null );
		assertEquals( Optional.of( "John Doe" ), resolver.resolvePrincipalLabel( user ) );
	}
}
