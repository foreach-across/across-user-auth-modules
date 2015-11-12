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
public class TestMachinePrincipal
{
	@Test
	public void principalNameAndNameAreAlwaysLowerCased() throws Exception {
		MachinePrincipal machinePrincipal = new MachinePrincipal();
		assertNull( machinePrincipal.getPrincipalName() );
		assertNull( machinePrincipal.getName() );

		machinePrincipal.setName( "Some Principal" );

		assertEquals( "some principal", machinePrincipal.getName() );
		assertEquals( "some principal", machinePrincipal.getPrincipalName() );

		Field name = ReflectionUtils.findField( MachinePrincipal.class, "name" );
		name.setAccessible( true );
		name.set( machinePrincipal, "OTHER Name" );

		Field principalName = ReflectionUtils.findField( MachinePrincipal.class, "principalName" );
		principalName.setAccessible( true );
		principalName.set( machinePrincipal, "PRINCIPAL_NAME" );

		assertEquals( "other name", machinePrincipal.getName() );
		assertEquals( "principal_name", machinePrincipal.getPrincipalName() );
	}
}
