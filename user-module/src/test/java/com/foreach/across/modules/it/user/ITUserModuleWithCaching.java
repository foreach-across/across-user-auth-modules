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
import com.foreach.across.modules.user.dto.GroupDto;
import com.foreach.across.modules.user.dto.MachinePrincipalDto;
import com.foreach.across.modules.user.services.GroupService;
import com.foreach.across.modules.user.services.MachinePrincipalService;
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

	private Map<Object, Object> securityPrincipalCache, groupCache;

	@Autowired
	public void registerCaches(
			@Qualifier(SpringSecurityModuleCache.SECURITY_PRINCIPAL) ConcurrentMapCache securityPrincipalCache,
			@Qualifier(UserModuleCache.GROUPS) ConcurrentMapCache groupCache
	) {
		this.securityPrincipalCache = securityPrincipalCache.getNativeCache();
		this.groupCache = groupCache.getNativeCache();
	}

	@Before
	public void before() {
		securityPrincipalCache.clear();

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
		MachinePrincipalDto principalDto = new MachinePrincipalDto();
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

		MachinePrincipalDto otherDto = new MachinePrincipalDto();
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
		GroupDto principalDto = new GroupDto();
		principalDto.setName( "someGroup" );

		// Null value should be cached
		assertTrue( securityPrincipalCache.isEmpty() );
		assertTrue( groupCache.isEmpty() );
		assertNull( groupService.getGroupByName( "someGroup" ) );
		assertTrue( securityPrincipalCache.isEmpty() );
		assertEquals( 1, groupCache.size() );

		Group created = groupService.save( principalDto );
		assertNotNull( created );

		assertTrue( securityPrincipalCache.isEmpty() );

		Group byName = groupService.getGroupByName( created.getName() );
		assertNotNull( byName );
		assertEquals( created, byName );
		assertEquals( 1, groupCache.size() );
		assertEquals( 2, securityPrincipalCache.size() );
		assertSame( byName, groupCache.get( created.getName() ) );
		assertSame( byName, securityPrincipalCache.get( created.getId() ) );
		assertSame( byName, securityPrincipalCache.get( created.getPrincipalName() ) );

		Group byId = groupService.getGroupById( created.getId() );
		assertNotNull( byId );
		assertEquals( created, byId );
		assertSame( byName, byId );
		assertEquals( 1, groupCache.size() );
		assertEquals( 2, securityPrincipalCache.size() );
		assertSame( byId, groupCache.get( created.getName() ) );
		assertSame( byId, securityPrincipalCache.get( created.getId() ) );
		assertSame( byId, securityPrincipalCache.get( created.getPrincipalName() ) );

		GroupDto otherDto = new GroupDto();
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
