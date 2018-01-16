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
package com.foreach.across.test.modules.it.spring.security.acl;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.spring.security.acl.SpringSecurityAclModule;
import com.foreach.across.modules.spring.security.acl.SpringSecurityAclModuleCache;
import com.foreach.across.modules.spring.security.acl.business.AclSecurityEntity;
import com.foreach.across.modules.spring.security.acl.services.AclSecurityEntityService;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalService;
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
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = { ITSpringSecurityAclModule.Config.class,
                                  ITAclSecurityEntityCaching.CacheConfig.class })
public class ITAclSecurityEntityCaching
{
	@Autowired
	private AclSecurityEntityService securityEntityService;

	@Autowired
	private SecurityPrincipalService securityPrincipalService;

	private Map<Object, Object> entityCache;

	@Autowired
	public void registerCaches(
			@Qualifier(SpringSecurityAclModuleCache.ACL_SECURITY_ENTITY) ConcurrentMapCache entityCache
	) {
		this.entityCache = entityCache.getNativeCache();
	}

	@Before
	public void before() {
		entityCache.clear();

		SecurityPrincipal principal = mock( SecurityPrincipal.class );
		when( principal.toString() ).thenReturn( "principal" );

		securityPrincipalService.authenticate( principal );
	}

	@After
	public void after() {
		securityPrincipalService.clearAuthentication();
	}

	@Test
	public void createAndGetAclSecurityEntity() {
		AclSecurityEntity entityDto = new AclSecurityEntity();
		entityDto.setName( "someEntity" );

		AclSecurityEntity created = securityEntityService.save( entityDto );
		assertNotNull( created );

		assertTrue( entityCache.isEmpty() );

		AclSecurityEntity byName = securityEntityService.getSecurityEntityByName( created.getName() );
		assertNotNull( byName );
		assertEquals( created, byName );
		assertEquals( 2, entityCache.size() );
		assertSame( byName, entityCache.get( created.getId() ) );
		assertSame( byName, entityCache.get( created.getName() ) );

		AclSecurityEntity byId = securityEntityService.getSecurityEntityById( created.getId() );
		assertNotNull( byId );
		assertEquals( created, byId );
		assertSame( byName, byId );
		assertEquals( 2, entityCache.size() );
		assertSame( byId, entityCache.get( created.getId() ) );
		assertSame( byId, entityCache.get( created.getName() ) );

		AclSecurityEntity otherDto = new AclSecurityEntity();
		otherDto.setName( "otherEntity" );

		AclSecurityEntity other = securityEntityService.save( otherDto );
		assertNotNull( other );
		assertEquals( 2, entityCache.size() );
		assertSame( byId, entityCache.get( created.getId() ) );
		assertSame( byId, entityCache.get( created.getName() ) );

		other = securityEntityService.getSecurityEntityByName( other.getName() );
		assertEquals( 4, entityCache.size() );

		securityEntityService.save( entityDto );
		assertEquals( 2, entityCache.size() );
		assertSame( other, entityCache.get( other.getId() ) );
		assertSame( other, entityCache.get( other.getName() ) );
	}

	@Configuration
	static class CacheConfig implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext acrossContext ) {
			acrossContext.addModule( new AcrossHibernateJpaModule() );

			acrossContext.getModule( SpringSecurityAclModule.NAME )
			             .addApplicationContextConfigurer( EnableCachingConfiguration.class );
		}
	}

	@EnableCaching
	@Configuration
	static class EnableCachingConfiguration
	{
		@Bean(name = SpringSecurityAclModuleCache.ACL_SECURITY_ENTITY)
		@Exposed
		public ConcurrentMapCache aclSecurityEntityCache() {
			return new ConcurrentMapCache( SpringSecurityAclModuleCache.ACL_SECURITY_ENTITY );
		}

		@Bean(name = SpringSecurityAclModuleCache.ACL)
		@Exposed
		public ConcurrentMapCache aclCache() {
			return new ConcurrentMapCache( SpringSecurityAclModuleCache.ACL );
		}

		@Bean
		@Primary
		public CacheManager cacheManager() {
			SimpleCacheManager cacheManager = new SimpleCacheManager();
			cacheManager.setCaches( Arrays.asList(
					aclSecurityEntityCache(), aclCache()
			) );

			return cacheManager;
		}
	}
}
