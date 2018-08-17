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
import com.foreach.across.modules.user.repositories.UserDirectoryRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestUserDirectoryService.Config.class)
public class TestUserDirectoryService
{
	@Autowired
	private UserDirectoryRepository userDirectoryRepository;

	@Autowired
	private UserDirectoryService userDirectoryService;

	@Test
	public void activeDirectoriesOnlyReturnsActiveSortedAccordingToOrder() {
		List<UserDirectory> allDirectories = new ArrayList<>();
		allDirectories.add( dir( "default", false, 0 ) );
		allDirectories.add( dir( "internal-one", true, 2 ) );
		allDirectories.add( dir( "ldap", true, 1 ) );

		when( userDirectoryRepository.findAll() ).thenReturn( allDirectories );

		Collection<UserDirectory> activeDirectories = userDirectoryService.getActiveUserDirectories();
		assertEquals( 2, activeDirectories.size() );
		assertFalse( activeDirectories.contains( allDirectories.get( 0 ) ) );

		Iterator<UserDirectory> it = activeDirectories.iterator();
		assertSame( allDirectories.get( 2 ), it.next() );
		assertSame( allDirectories.get( 1 ), it.next() );
	}

	private UserDirectory dir( String name, boolean active, int order ) {
		UserDirectory dir = new InternalUserDirectory();
		dir.setId( order + 1000L );
		dir.setName( name );
		dir.setActive( active );
		dir.setOrder( order );

		return dir;
	}

	@Configuration
	protected static class Config
	{
		@Bean
		public UserDirectoryRepository userDirectoryRepository() {
			return mock( UserDirectoryRepository.class );
		}

		@Bean
		public UserDirectoryService userDirectoryService() {
			return new UserDirectoryServiceImpl();
		}
	}
}
