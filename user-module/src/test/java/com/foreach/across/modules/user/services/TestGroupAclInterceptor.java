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

import com.foreach.across.modules.spring.security.acl.business.AclSecurityEntity;
import com.foreach.across.modules.spring.security.acl.services.AclSecurityEntityService;
import com.foreach.across.modules.spring.security.acl.services.AclSecurityService;
import com.foreach.across.modules.user.UserModuleSettings;
import com.foreach.across.modules.user.business.Group;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestGroupAclInterceptor.Config.class)
@DirtiesContext
public class TestGroupAclInterceptor
{
	@Autowired
	private GroupAclInterceptor groupAclInterceptor;

	@Autowired
	private UserModuleSettings userModuleSettings;

	@Autowired
	private AclSecurityService aclSecurityService;

	@Autowired
	private AclSecurityEntityService aclSecurityEntityService;

	@BeforeEach
	public void resetMocks() {
		reset( userModuleSettings, aclSecurityService, aclSecurityEntityService );
	}

	@Test
	public void testAclCreatedWithConfigEnabled() {
		Group group = new Group();
		group.setId( -1L );
		group.setName( "Foo" );

		AclSecurityEntity groups = new AclSecurityEntity();
		groups.setId( -1L );
		groups.setName( "groups" );

		when( userModuleSettings.isEnableDefaultAcls() ).thenReturn( true );
		when( aclSecurityEntityService.getSecurityEntityByName( "groups" ) ).thenReturn( Optional.of( groups ) );

		groupAclInterceptor.afterCreate( group );

		verify( aclSecurityService ).createAclWithParent( group, groups );
		verifyNoMoreInteractions( aclSecurityService );
	}

	@Test
	public void testAclNotCreatedWithConfigDisabled() {
		Group group = new Group();
		group.setId( -1L );
		group.setName( "Foo" );

		when( userModuleSettings.isEnableDefaultAcls() ).thenReturn( false );

		groupAclInterceptor.afterCreate( group );

		verifyZeroInteractions( aclSecurityService, aclSecurityEntityService );
	}

	@Test
	public void testAclDeleted() {
		Group group = new Group();
		group.setId( -1L );
		group.setName( "Foo" );

		when( userModuleSettings.isEnableDefaultAcls() ).thenReturn( true );

		groupAclInterceptor.beforeDelete( group );

		verify( aclSecurityService ).deleteAcl( group, true );
		verifyNoMoreInteractions( aclSecurityService );
	}

	@Test
	public void testAclNotDeletedWithConfigDisabled() {
		Group group = new Group();
		group.setId( -1L );
		group.setName( "Foo" );

		when( userModuleSettings.isEnableDefaultAcls() ).thenReturn( false );

		groupAclInterceptor.afterDelete( group );

		verifyZeroInteractions( aclSecurityService, aclSecurityEntityService );
	}

	@Configuration
	static class Config
	{
		@Bean
		public AclSecurityService aclSecurityService() {
			return mock( AclSecurityService.class );
		}

		@Bean
		public AclSecurityEntityService aclSecurityEntityService() {
			return mock( AclSecurityEntityService.class );
		}

		@Bean
		public UserModuleSettings userModuleSettings() {
			return mock( UserModuleSettings.class );
		}

		@Bean
		public GroupAclInterceptor groupAclInterceptor() {
			return new GroupAclInterceptor();
		}
	}
}
