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
import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.across.modules.hibernate.AcrossHibernateModule;
import com.foreach.across.modules.properties.PropertiesModule;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.modules.user.UserModule;
import com.foreach.across.modules.user.business.BasicSecurityPrincipal;
import com.foreach.across.modules.user.business.MachinePrincipal;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.business.UserRestriction;
import com.foreach.across.modules.user.services.GroupAclInterceptor;
import com.foreach.across.modules.user.services.MachinePrincipalService;
import com.foreach.across.modules.user.services.UserService;
import com.foreach.across.test.AcrossTestConfiguration;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.EnumSet;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = ITUserModuleWithCaching.Config.class)
public class ITUserModuleWithCaching
{
	@Autowired
	private UserService userService;

	@Autowired
	private MachinePrincipalService machinePrincipalService;

	@Autowired
	private AcrossContextInfo acrossContextInfo;

	@Test
	public void verifyBootstrapped() {
		assertNotNull( userService );
		User admin = userService.getUserByUsername( "admin" );
		assertNotNull( admin );
		assertEquals( "admin", admin.getUsername() );
		assertEquals( EnumSet.noneOf( UserRestriction.class ), admin.getRestrictions() );
		assertEquals( false, admin.isDeleted() );
		assertEquals( true, admin.getEmailConfirmed() );

		assertEquals( true, admin.isEnabled() );
		assertEquals( true, admin.isAccountNonExpired() );
		assertEquals( true, admin.isAccountNonLocked() );
		assertEquals( true, admin.isCredentialsNonExpired() );

		MachinePrincipal machine = machinePrincipalService.getMachinePrincipalByName( "system" );
		assertNotNull( machine );

		AcrossModuleInfo moduleInfo = acrossContextInfo.getModuleInfo( UserModule.NAME );

		try {
			assertNull( moduleInfo.getApplicationContext().getBean( GroupAclInterceptor.class ) );
		}
		catch ( NoSuchBeanDefinitionException e ) {
			assertTrue( true ); //If we get this exception, the desired result has been achieved.
		}

		CacheManager cacheManager = CacheManager.getCacheManager( "hibernate" );
		Cache cache = cacheManager.getCache( BasicSecurityPrincipal.class.getName() );
		cache.flush();
		assertEquals( 0, cache.getSize() );

		assertNotNull( machinePrincipalService.getMachinePrincipalByName( "system" ) );
		assertEquals( 1, cache.getSize() );
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
		}

		private PropertiesModule propertiesModule() {
			return new PropertiesModule();
		}

		private AcrossHibernateModule acrossHibernateModule() {
			AcrossHibernateModule acrossHibernateModule = new AcrossHibernateModule();
			acrossHibernateModule.setHibernateProperty( "hibernate.cache.use_second_level_cache", "true" );
			acrossHibernateModule.setHibernateProperty( "hibernate.cache.use_query_cache", "true" );
			acrossHibernateModule.setHibernateProperty( "hibernate.cache.region.factory_class",
			                                            "org.hibernate.cache.ehcache.EhCacheRegionFactory" );
			return acrossHibernateModule;
		}

		private UserModule userModule() {
			return new UserModule();
		}
	}
}
