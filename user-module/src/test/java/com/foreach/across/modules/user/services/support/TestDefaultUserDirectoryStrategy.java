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

import com.foreach.across.modules.user.business.InternalUserDirectory;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.business.UserDirectory;
import com.foreach.across.modules.user.services.UserDirectoryService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 1.2.0
 */
public class TestDefaultUserDirectoryStrategy
{
	private UserDirectoryService userDirectoryService;
	private DefaultUserDirectoryStrategy userDirectoryStrategy;

	@Before
	public void reset() {
		userDirectoryService = mock( UserDirectoryService.class );
		userDirectoryStrategy = new DefaultUserDirectoryStrategyImpl( userDirectoryService );
	}

	@Test
	public void defaultIsReturned() {
		UserDirectory dir = new InternalUserDirectory();

		when( userDirectoryService.getDefaultUserDirectory() ).thenReturn( dir );
		assertSame( dir, userDirectoryStrategy.getDefaultUserDirectory() );
	}

	@Test
	public void aSetDirectoryIsNeverReplaced() {
		UserDirectory one = new InternalUserDirectory();

		User user = new User();
		user.setUserDirectory( one );

		userDirectoryStrategy.apply( user );

		assertSame( one, user.getUserDirectory() );
		verify( userDirectoryService, never() ).getDefaultUserDirectory();
	}

	@Test
	public void defaultInternalUserDirectoryIsSetIfRequired() {
		UserDirectory defaultDir = new InternalUserDirectory();
		when( userDirectoryService.getDefaultUserDirectory() ).thenReturn( defaultDir );

		User user = new User();
		userDirectoryStrategy.apply( user );

		assertSame( defaultDir, user.getUserDirectory() );
	}
}
