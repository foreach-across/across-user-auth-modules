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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Arne Vandamme
 */
public class TestOAuth2Scope
{
	@Test
	public void oauth2ScopeDto() {
		OAuth2Scope scope = new OAuth2Scope();
		scope.setId( 123L );
		scope.setName( "scope" );

		OAuth2ClientScope clientScopeOne = new OAuth2ClientScope();
		clientScopeOne.setId( new OAuth2ClientScopeId() );

		OAuth2ClientScope clientScopeTwo = new OAuth2ClientScope();
		clientScopeTwo.setId( new OAuth2ClientScopeId() );

		OAuth2Scope dto = scope.toDto();
		assertEquals( scope, dto );
		assertEquals( scope.getName(), dto.getName() );
	}
}
