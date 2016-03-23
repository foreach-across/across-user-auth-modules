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

import com.foreach.across.modules.user.business.Group;
import com.foreach.across.modules.user.business.InternalUserDirectory;
import com.foreach.across.modules.user.business.UserDirectory;
import com.foreach.across.modules.user.repositories.GroupRepository;
import com.foreach.across.modules.user.services.support.DefaultUserDirectoryStrategy;
import com.foreach.common.test.MockedLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 1.2.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockedLoader.class, classes = TestGroupService.Config.class)
public class TestGroupService extends AbstractQueryDslPredicateExecutorTest
{
	@Autowired
	private DefaultUserDirectoryStrategy defaultUserDirectoryStrategy;

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private GroupService groupService;

	@Test
	public void serviceDelegatesToRepositoryForQueryDslPredicateExecutor() {
		queryDslPredicateExecutorTest( groupService, groupRepository );
	}

	@Test
	public void getGroupByNameShouldUseDefaultDirectory() {
		UserDirectory dir = new InternalUserDirectory();
		dir.setId( 123L );
		when( defaultUserDirectoryStrategy.getDefaultUserDirectory() ).thenReturn( dir );

		Group expected = new Group();
		expected.setName( "expected" );

		when( groupRepository.findByNameAndUserDirectory( "groupName", dir ) ).thenReturn( expected );

		assertSame( expected, groupService.getGroupByName( "groupName" ) );
	}

	@Test
	public void userDirectoryStrategyShouldBeAppliedBeforeRepositoryCall() {
		Group one = new Group();
		one.setId( 1L );
		Group two = new Group();
		two.setId( 2L );
		assertNotEquals( one, two );

		doAnswer(
				invocationOnMock -> {
					( (Group) invocationOnMock.getArguments()[0] ).setId( 2L );
					return null;
				}
		).when( defaultUserDirectoryStrategy ).apply( one );

		when( groupRepository.save( two ) ).thenReturn( two );

		groupService.save( one );

		verify( groupRepository ).save( two );
	}

	@Configuration
	protected static class Config
	{
		@Bean
		public GroupService groupService() {
			return new GroupServiceImpl();
		}
	}
}
