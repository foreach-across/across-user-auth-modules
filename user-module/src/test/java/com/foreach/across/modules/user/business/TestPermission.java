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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestPermission
{
	@Test
	public void equals() {
		Permission left = new Permission( "some permission" );
		left.setDescription( "description left" );

		Permission right = new Permission( "some permission" );

		assertEquals( left, right );
		assertNotEquals( left, new Permission( "other permission" ) );
	}

	@Test
	public void caseIsIgnored() {
		Permission left = new Permission( "some permission" );
		left.setDescription( "description left" );

		Permission right = new Permission( "SOME permission" );

		assertEquals( left, right );
	}
}
