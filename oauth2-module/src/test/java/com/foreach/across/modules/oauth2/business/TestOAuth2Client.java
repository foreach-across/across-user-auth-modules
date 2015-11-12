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

import com.foreach.across.modules.user.business.Group;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Arne Vandamme
 */
public class TestOAuth2Client
{
	@Test
	public void principalNameIsAlwaysLowerCased() throws Exception {
		OAuth2Client client = new OAuth2Client();
		assertNull( client.getPrincipalName() );
		assertNull( client.getClientId() );

		client.setClientId( "My Client Id" );

		assertEquals( "My Client Id", client.getClientId() );
		assertEquals( "my client id", client.getPrincipalName() );

		Field principalName = ReflectionUtils.findField( Group.class, "principalName" );
		principalName.setAccessible( true );
		principalName.set( client, "CLIENT PRINCIPAL_NAME" );

		assertEquals( "client principal_name", client.getPrincipalName() );
	}
}
