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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author Marc Vanbrabant
 */
public class TestAclSecurityEntity
{
	@Test
	public void testEquals() throws Exception {
		assertNotEquals( new AclSecurityEntity(), new AclSecurityEntity() );
		{
			AclSecurityEntity entity1 = new AclSecurityEntity();
			entity1.setId( -1l );
			AclSecurityEntity entity2 = new AclSecurityEntity();
			entity2.setId( 1l );
			assertNotEquals( entity1, entity2 );
		}
		{
			AclSecurityEntity entity1 = new AclSecurityEntity();
			entity1.setId( 1l );
			AclSecurityEntity entity2 = new AclSecurityEntity();
			entity2.setId( 1l );
			assertEquals( entity1, entity2 );
		}
	}
}
