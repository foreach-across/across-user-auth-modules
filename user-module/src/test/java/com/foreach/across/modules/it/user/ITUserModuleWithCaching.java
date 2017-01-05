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

package com.foreach.across.modules.it.user;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.context.configurer.AnnotatedClassConfigurer;
import com.foreach.across.core.context.configurer.ConfigurerScope;
import com.foreach.across.modules.spring.security.SpringSecurityModuleCache;
import com.foreach.across.modules.spring.security.infrastructure.SpringSecurityInfrastructureModule;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalService;
import com.foreach.across.modules.user.UserModule;
import com.foreach.across.modules.user.UserModuleCache;
import com.foreach.across.modules.user.business.Group;
import com.foreach.across.modules.user.business.MachinePrincipal;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.services.GroupService;
import com.foreach.across.modules.user.services.MachinePrincipalService;
import com.foreach.across.modules.user.services.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = { ITUserModule.Config.class,
                                  ITUserModuleWithCaching.CacheConfig.class })
public class ITUserModuleWithCaching
{
	@Autowired
	private MachinePrincipalService machinePrincipalService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private SecurityPrincipalService securityPrincipalService;

	@Autowired
	private UserService userService;

	private Map<Object, Object> securityPrincipalCache, groupCache, userCache;

	@Autowired
	public void registerCaches(
			@Qualifier(SpringSecurityModuleCache.SECURITY_PRINCIPAL) ConcurrentMapCache securityPrincipalCache,
			@Qualifier(UserModuleCache.GROUPS) ConcurrentMapCache groupCache,
			@Qualifier(UserModuleCache.USERS) ConcurrentMapCache userCache
	) {
		this.securityPrincipalCache = securityPrincipalCache.getNativeCache();
		this.groupCache = groupCache.getNativeCache();
		this.userCache = userCache.getNativeCache();
	}

	@Before
	public void before() {
		securityPrincipalCache.clear();
		groupCache.clear();
		userCache.clear();

		SecurityPrincipal principal = mock( SecurityPrincipal.class );
		when( principal.toString() ).thenReturn( "principal" );

		securityPrincipalService.authenticate( principal );
	}

	@After
	public void after() {
		securityPrincipalService.clearAuthentication();
	}

	@Test
	public void createAndGetMachinePrincipal() {
		MachinePrincipal principalDto = new MachinePrincipal();
		principalDto.setName( "somePrincipal" );

		// Null value should be cached
		assertTrue( securityPrincipalCache.isEmpty() );
		assertNull( machinePrincipalService.getMachinePrincipalByName( "somePrincipal" ) );
		assertEquals( 1, securityPrincipalCache.size() );

		MachinePrincipal created = machinePrincipalService.save( principalDto );
		assertNotNull( created );

		assertTrue( securityPrincipalCache.isEmpty() );

		MachinePrincipal byName = machinePrincipalService.getMachinePrincipalByName( created.getName() );
		assertNotNull( byName );
		assertEquals( created, byName );
		assertEquals( 2, securityPrincipalCache.size() );
		assertSame( byName, securityPrincipalCache.get( created.getId() ) );
		assertSame( byName, securityPrincipalCache.get( created.getName() ) );

		MachinePrincipal byId = machinePrincipalService.getMachinePrincipalById( created.getId() );
		assertNotNull( byId );
		assertEquals( created, byId );
		assertSame( byName, byId );
		assertEquals( 2, securityPrincipalCache.size() );
		assertSame( byId, securityPrincipalCache.get( created.getId() ) );
		assertSame( byId, securityPrincipalCache.get( created.getName() ) );

		MachinePrincipal otherDto = new MachinePrincipal();
		otherDto.setName( "otherEntity" );

		MachinePrincipal other = machinePrincipalService.save( otherDto );
		assertNotNull( other );
		assertEquals( 2, securityPrincipalCache.size() );
		assertSame( byId, securityPrincipalCache.get( created.getId() ) );
		assertSame( byId, securityPrincipalCache.get( created.getName() ) );

		other = machinePrincipalService.getMachinePrincipalByName( other.getName() );
		assertEquals( 4, securityPrincipalCache.size() );

		machinePrincipalService.save( principalDto );
		assertEquals( 2, securityPrincipalCache.size() );
		assertSame( other, securityPrincipalCache.get( other.getId() ) );
		assertSame( other, securityPrincipalCache.get( other.getName() ) );
	}

	@Test
	public void createAndGetGroup() {
		Group principalDto = new Group();
		principalDto.setName( "someGroup" );

		// Null value should be cached
		assertTrue( securityPrincipalCache.isEmpty() );
		assertTrue( groupCache.isEmpty() );
		assertNull( groupService.getGroupByName( "someGroup" ) );
		assertTrue( securityPrincipalCache.isEmpty() );
		assertEquals( 1, groupCache.size() );

		Group created = groupService.save( principalDto );
		String cacheItemName = created.getUserDirectory().getId() + ":" + created.getName();
		assertNotNull( created );

		assertTrue( securityPrincipalCache.isEmpty() );

		Group byName = groupService.getGroupByName( created.getName() );
		assertNotNull( byName );
		assertEquals( created, byName );
		assertEquals( 1, groupCache.size() );
		assertEquals( 2, securityPrincipalCache.size() );
		assertSame( byName, groupCache.get( cacheItemName ) );
		assertSame( byName, securityPrincipalCache.get( created.getId() ) );
		assertSame( byName, securityPrincipalCache.get( created.getPrincipalName() ) );

		Group byId = groupService.getGroupById( created.getId() );
		assertNotNull( byId );
		assertEquals( created, byId );
		assertSame( byName, byId );
		assertEquals( 1, groupCache.size() );
		assertEquals( 2, securityPrincipalCache.size() );
		assertSame( byId, groupCache.get( cacheItemName ) );
		assertSame( byId, securityPrincipalCache.get( created.getId() ) );
		assertSame( byId, securityPrincipalCache.get( created.getPrincipalName() ) );

		Group otherDto = new Group();
		otherDto.setName( "otherGroup" );

		Group other = groupService.save( otherDto );
		assertNotNull( other );
		assertEquals( 0, groupCache.size() );
		assertEquals( 2, securityPrincipalCache.size() );
		assertSame( byId, securityPrincipalCache.get( created.getId() ) );
		assertSame( byId, securityPrincipalCache.get( created.getPrincipalName() ) );

		other = groupService.getGroupByName( other.getName() );
		assertEquals( 1, groupCache.size() );
		assertEquals( 4, securityPrincipalCache.size() );

		groupService.save( principalDto );
		assertEquals( 2, securityPrincipalCache.size() );
		assertSame( other, securityPrincipalCache.get( other.getId() ) );
		assertSame( other, securityPrincipalCache.get( other.getPrincipalName() ) );
		assertEquals( 0, groupCache.size() );
	}

	@Test
	public void createAndGetUser() {
		User User = new User();
		User.setUsername( "someUser" );
		User.setEmail( "someEmail@localhost" );
		User.setPassword( "pwd" );

		// Null value should be cached
		assertTrue( securityPrincipalCache.isEmpty() );
		assertTrue( userCache.isEmpty() );
		assertNull( userService.getUserByUsername( "someUser" ) );
		assertNull( userService.getUserByEmail( "someEmail@localhost" ) );
		assertTrue( securityPrincipalCache.isEmpty() );
		assertEquals( 2, userCache.size() );

		User actual = mock( User.class );
		userCache.put( "email:someemail@localhost", actual );
		assertSame( actual, userService.getUserByEmail( "someEmail@localhost" ) );

		User created = userService.save( User );
		assertNotNull( created );

		assertTrue( userCache.isEmpty() );

		User byUsername = userService.getUserByUsername( created.getUsername() );
		assertNotNull( byUsername );
		assertEquals( created, byUsername );
		assertEquals( 1, userCache.size() );
		assertEquals( 2, securityPrincipalCache.size() );
		assertSame( byUsername, userCache.get( "username:" + created.getUsername() ) );
		assertSame( byUsername, securityPrincipalCache.get( created.getId() ) );
		assertSame( byUsername, securityPrincipalCache.get( created.getPrincipalName() ) );

		User byId = userService.getUserById( created.getId() );
		assertSame( byUsername, byId );
		assertEquals( 1, userCache.size() );
		assertEquals( 2, securityPrincipalCache.size() );

		User byEmail = userService.getUserByEmail( created.getEmail() );
		assertEquals( created, byEmail );
		assertEquals( 2, userCache.size() );
		assertEquals( 2, securityPrincipalCache.size() );
		assertSame( byEmail, userCache.get( "username:" + created.getUsername() ) );
		assertSame( byEmail, userCache.get( "email:" + created.getEmail() ) );
		assertSame( byEmail, securityPrincipalCache.get( created.getId() ) );
		assertSame( byEmail, securityPrincipalCache.get( created.getPrincipalName() ) );

		User otherDto = new User();
		otherDto.setUsername( "otherUser" );
		otherDto.setEmail( "otherEmail@localhost" );
		otherDto.setPassword( "pwd" );

		User other = userService.save( otherDto );
		assertNotNull( other );
		assertEquals( 2, userCache.size() );
		assertEquals( 2, securityPrincipalCache.size() );

		other = userService.getUserByEmail( other.getEmail() );
		assertEquals( 4, userCache.size() );
		assertEquals( 4, securityPrincipalCache.size() );
		assertSame( other, userCache.get( "username:" + other.getUsername() ) );
		assertSame( other, userCache.get( "email:" + other.getEmail() ) );
		assertSame( other, securityPrincipalCache.get( other.getId() ) );
		assertSame( other, securityPrincipalCache.get( other.getPrincipalName() ) );

		userService.save( User );
		assertEquals( 2, userCache.size() );
		assertEquals( 2, securityPrincipalCache.size() );
		assertSame( other, userCache.get( "username:" + other.getUsername() ) );
		assertSame( other, userCache.get( "email:" + other.getEmail() ) );
		assertSame( other, securityPrincipalCache.get( other.getId() ) );
		assertSame( other, securityPrincipalCache.get( other.getPrincipalName() ) );
	}

	@Configuration
	static class CacheConfig implements AcrossContextConfigurer
	{
		@Bean(name = SpringSecurityModuleCache.SECURITY_PRINCIPAL)
		public ConcurrentMapCache securityPrincipalCache() {
			return new ConcurrentMapCache( SpringSecurityModuleCache.SECURITY_PRINCIPAL );
		}

		@Bean(name = UserModuleCache.USERS)
		public ConcurrentMapCache userCache() {
			return new ConcurrentMapCache( UserModuleCache.USERS );
		}

		@Bean(name = UserModuleCache.USER_PROPERTIES)
		public ConcurrentMapCache userPropertiesCache() {
			return new ConcurrentMapCache( UserModuleCache.USER_PROPERTIES );
		}

		@Bean(name = UserModuleCache.GROUPS)
		public ConcurrentMapCache groupCache() {
			return new ConcurrentMapCache( UserModuleCache.GROUPS );
		}

		@Bean(name = UserModuleCache.GROUP_PROPERTIES)
		public ConcurrentMapCache groupPropertiesCache() {
			return new ConcurrentMapCache( UserModuleCache.GROUP_PROPERTIES );
		}

		@Override
		public void configure( AcrossContext acrossContext ) {
			acrossContext.addApplicationContextConfigurer( new AnnotatedClassConfigurer(
					                                               CacheConfiguration.class ),
			                                               ConfigurerScope.CONTEXT_ONLY
			);

			acrossContext.getModule( UserModule.NAME )
			             .addApplicationContextConfigurer( EnableCachingConfiguration.class );
			acrossContext.getModule( SpringSecurityInfrastructureModule.NAME )
			             .addApplicationContextConfigurer( EnableCachingConfiguration.class );
		}
	}

	@EnableCaching
	static class EnableCachingConfiguration
	{
	}

	@Configuration
	static class CacheConfiguration
	{
		@Bean
		@Autowired
		@Primary
		public CacheManager cacheManager( Collection<ConcurrentMapCache> concurrentMapCacheCollection ) {
			SimpleCacheManager cacheManager = new SimpleCacheManager();
			cacheManager.setCaches( concurrentMapCacheCollection );

			return cacheManager;
		}
	}
}
