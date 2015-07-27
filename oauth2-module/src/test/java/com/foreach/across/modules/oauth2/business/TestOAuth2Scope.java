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
package com.foreach.across.modules.oauth2.business;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * @author Arne Vandamme
 */
public class TestOAuth2Scope
{
	@Test
	public void nullValuesAreSameAsEmpty() {
		OAuth2Scope scope = new OAuth2Scope();
		scope.setOAuth2ClientScopes( Collections.singleton( new OAuth2ClientScope() ) );

		assertFalse( scope.getOAuth2ClientScopes().isEmpty() );

		scope.setOAuth2ClientScopes( null );
		assertTrue( scope.getOAuth2ClientScopes().isEmpty() );
	}

	@Test
	public void oauth2ScopeDto() {
		OAuth2Scope scope = new OAuth2Scope();
		scope.setId( 123L );
		scope.setName( "scope" );

		OAuth2ClientScope clientScopeOne = new OAuth2ClientScope();
		clientScopeOne.setPk( new OAuth2ClientScopeId() );

		OAuth2ClientScope clientScopeTwo = new OAuth2ClientScope();
		clientScopeTwo.setPk( new OAuth2ClientScopeId() );

		scope.setOAuth2ClientScopes( Arrays.asList( clientScopeOne, clientScopeTwo ) );

		OAuth2Scope dto = scope.toDto();
		assertEquals( scope, dto );
		assertEquals( scope.getName(), dto.getName() );
		assertEquals( scope.getOAuth2ClientScopes(), dto.getOAuth2ClientScopes() );
		assertNotSame( scope.getOAuth2ClientScopes(), dto.getOAuth2ClientScopes() );
	}
}