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

import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Arne Vandamme
 */
public class TestGroup
{
	@Test
	public void principalNameIsAlwaysLowerCased() throws Exception {
		Group group = new Group();
		assertNull( group.getPrincipalName() );
		assertNull( group.getName() );

		group.setName( "Some Group" );

		assertEquals( "Some Group", group.getName() );
		assertEquals( "group:some group", group.getPrincipalName() );

		Field principalName = ReflectionUtils.findField( Group.class, "principalName" );
		principalName.setAccessible( true );
		principalName.set( group, "GROUP:PRINCIPAL_NAME" );

		assertEquals( "group:principal_name", group.getPrincipalName() );
	}
}
