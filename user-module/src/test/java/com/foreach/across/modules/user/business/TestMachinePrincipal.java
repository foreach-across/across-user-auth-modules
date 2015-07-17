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

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * @author Arne Vandamme
 */
public class TestMachinePrincipal
{
	@Test
	public void machinePrincipalDto() {
		MachinePrincipal machinePrincipal = new MachinePrincipal();
		machinePrincipal.setId( 123L );
		machinePrincipal.setName( "machine principal" );
		machinePrincipal.setGroups( Arrays.asList( new Group( "groupOne" ), new Group( "groupTwo" ) ) );
		machinePrincipal.setRoles( Arrays.asList( new Role( "one" ), new Role( "two" ) ) );

		MachinePrincipal dto = machinePrincipal.toDto();
		assertEquals( machinePrincipal, dto );
		assertEquals( machinePrincipal.getName(), dto.getName() );
		assertEquals( machinePrincipal.getRoles(), dto.getRoles() );
		assertNotSame( machinePrincipal.getRoles(), dto.getRoles() );
		assertEquals( machinePrincipal.getGroups(), dto.getGroups() );
		assertNotSame( machinePrincipal.getGroups(), dto.getGroups() );
	}
}
