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

package com.foreach.across.modules.user;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Arne Vandamme
 * @since 1.2.0
 */
public class TestUserModuleSettings
{
	@Test
	public void emailMustBeUniqueIfItIsUsername() {
		UserModuleSettings settings = new UserModuleSettings();
		assertFalse( settings.isRequireEmailUnique() );
		assertFalse( settings.isUseEmailAsUsername() );

		settings.setUseEmailAsUsername( true );
		assertTrue( settings.isUseEmailAsUsername() );
		assertTrue( settings.isRequireEmailUnique() );

		settings.setUseEmailAsUsername( false );
		assertFalse( settings.isRequireEmailUnique() );
		assertFalse( settings.isUseEmailAsUsername() );

		settings.setRequireEmailUnique( true );
		assertTrue( settings.isRequireEmailUnique() );
		assertFalse( settings.isUseEmailAsUsername() );
	}
}
