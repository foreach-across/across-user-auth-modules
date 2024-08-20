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
import com.foreach.across.core.context.info.AcrossContextInfo;
import com.foreach.across.modules.ehcache.EhcacheModule;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.properties.PropertiesModule;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.modules.spring.security.SpringSecurityModuleCache;
import com.foreach.across.modules.user.UserModule;
import com.foreach.across.modules.user.business.MachinePrincipal;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.business.UserRestriction;
import com.foreach.across.modules.user.services.MachinePrincipalService;
import com.foreach.across.modules.user.services.UserService;
import com.foreach.across.test.AcrossTestConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.cache.Cache;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@DirtiesContext
@ContextConfiguration(classes = ITUserModuleWithHibernateCaching.Config.class)
public class ITUserModuleWithHibernateCaching
{
	@Autowired
	private UserService userService;

	@Autowired
	private MachinePrincipalService machinePrincipalService;

	@Autowired
	private AcrossContextInfo acrossContextInfo;

	@Autowired
	private CacheManager cacheManager;

	@Test
	public void verifyBootstrapped() {
		assertNotNull( userService );
		User admin = userService.getUserByUsername( "admin" ).orElse( null );
		assertNotNull( admin );
		assertEquals( "admin", admin.getUsername() );
		assertEquals( EnumSet.noneOf( UserRestriction.class ), admin.getRestrictions() );
		assertEquals( false, admin.isDeleted() );
		assertEquals( true, admin.getEmailConfirmed() );

		assertEquals( true, admin.isEnabled() );
		assertEquals( true, admin.isAccountNonExpired() );
		assertEquals( true, admin.isAccountNonLocked() );
		assertEquals( true, admin.isCredentialsNonExpired() );

		MachinePrincipal machine = machinePrincipalService.getMachinePrincipalByName( "system" ).orElse( null );
		assertNotNull( machine );

		Cache cache = (Cache) cacheManager.getCache( SpringSecurityModuleCache.SECURITY_PRINCIPAL ).getNativeCache();
		assertNotNull( cache );
		cache.clear(); // could be async!
		assertEquals( 0, count( cache ) );

		assertNotNull( machinePrincipalService.getMachinePrincipalByName( "system" ) );
		assertEquals( 1, count( cache ) );
	}

	private int count( Cache cache ) {
		int result = 0;
		for ( Object o : cache ) {
			result++;
		}
		return result;
	}

	@Configuration
	@AcrossTestConfiguration
	static class Config implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			context.addModule( acrossHibernateModule() );
			context.addModule( userModule() );
			context.addModule( propertiesModule() );
			context.addModule( new SpringSecurityModule() );
			context.addModule( new EhcacheModule() );
		}

		private PropertiesModule propertiesModule() {
			return new PropertiesModule();
		}

		private AcrossHibernateJpaModule acrossHibernateModule() {
			AcrossHibernateJpaModule acrossHibernateModule = new AcrossHibernateJpaModule();
			acrossHibernateModule.setHibernateProperty( "hibernate.cache.use_second_level_cache", "true" );
			acrossHibernateModule.setHibernateProperty( "hibernate.cache.use_query_cache", "true" );
			acrossHibernateModule.setHibernateProperty( "hibernate.cache.region.factory_class", "jcache" );
			acrossHibernateModule.setHibernateProperty( "hibernate.javax.cache.provider", "org.ehcache.jsr107.EhcacheCachingProvider" );
			return acrossHibernateModule;
		}

		private UserModule userModule() {
			return new UserModule();
		}
	}
}
