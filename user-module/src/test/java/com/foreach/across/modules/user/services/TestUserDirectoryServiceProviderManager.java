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

package com.foreach.across.modules.user.services;

import com.foreach.across.modules.user.business.InternalUserDirectory;
import com.foreach.across.modules.user.business.UserDirectory;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class TestUserDirectoryServiceProviderManager
{
	@Test
	public void firstProviderIsReturned() {
		UserDirectoryServiceProvider one = mock( UserDirectoryServiceProvider.class );
		UserDirectoryServiceProvider two = mock( UserDirectoryServiceProvider.class );

		when( one.supports( InternalUserDirectory.class ) ).thenReturn( true );
		when( two.supports( UserDirectory.class ) ).thenReturn( true );

		UserDirectoryServiceProviderManager manager
				= new UserDirectoryServiceProviderManager( Arrays.asList( one, two ) );
		assertSame( one, manager.getServiceProvider( InternalUserDirectory.class ) );
		assertSame( one, manager.getServiceProvider( new InternalUserDirectory() ) );

		assertSame( two, manager.getServiceProvider( UserDirectory.class ) );
	}

	@Test
	public void nullIsReturnedIfNoProviderCanBeFound() {
		UserDirectoryServiceProviderManager manager
				= new UserDirectoryServiceProviderManager( Collections.emptyList() );
		assertNull( manager.getServiceProvider( new InternalUserDirectory() ) );
		assertNull( manager.getServiceProvider( InternalUserDirectory.class ) );
	}

	@Test(expected = IllegalArgumentException.class)
	public void nullServiceProviderManagerThrowsException() {
		UserDirectoryServiceProviderManager manager
				= new UserDirectoryServiceProviderManager( Collections.emptyList() );
		assertNull( manager.getServiceProvider( (UserDirectory) null ) );
	}

}
