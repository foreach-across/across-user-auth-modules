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
package com.foreach.across.modules.it.oauth2;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.core.cache.AcrossCompositeCacheManager;
import com.foreach.across.core.context.configurer.AnnotatedClassConfigurer;
import com.foreach.across.core.context.configurer.ConfigurerScope;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.oauth2.OAuth2Module;
import com.foreach.across.modules.oauth2.OAuth2ModuleCache;
import com.foreach.across.modules.oauth2.business.OAuth2Client;
import com.foreach.across.modules.oauth2.services.OAuth2Service;
import com.foreach.across.modules.properties.PropertiesModule;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.modules.spring.security.SpringSecurityModuleCache;
import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalService;
import com.foreach.across.modules.user.UserModule;
import com.foreach.across.modules.web.AcrossWebModule;
import com.foreach.across.test.AcrossTestConfiguration;
import com.foreach.across.test.AcrossWebAppConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.concurrent.ConcurrentMap;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@AcrossWebAppConfiguration(classes = ITOAuth2ModuleWithCaching.Config.class)
public class ITOAuth2ModuleWithCaching
{
	@Autowired
	private OAuth2Service oauth2Service;

	@Autowired
	private SecurityPrincipalService securityPrincipalService;

	@Autowired
	@Qualifier(SpringSecurityModuleCache.SECURITY_PRINCIPAL)
	private ConcurrentMapCache principalCache;

	@Autowired
	@Qualifier(OAuth2ModuleCache.CLIENTS)
	private ConcurrentMapCache clientCache;

	@Test
	public void verifyCachingEnabled() {
		ConcurrentMap principalMap = principalCache.getNativeCache();
		ConcurrentMap clientMap = clientCache.getNativeCache();

		assertNotSame( principalMap, clientMap );

		OAuth2Client oAuth2Client = new OAuth2Client();
		oAuth2Client.setClientId( "fredClient" );
		oAuth2Client.setClientSecret( "fred" );
		oAuth2Client.setSecretRequired( true );

		oauth2Service.saveClient( oAuth2Client );

		assertTrue( principalMap.isEmpty() );

		OAuth2Client unexisting = oauth2Service.getClientById( "blablabla" );
		assertNull( unexisting );
		assertTrue( principalMap.isEmpty() );
		assertEquals( 1, clientMap.size() );
		assertTrue( clientMap.containsKey( "blablabla" ) );

		assertNull( oauth2Service.getClientById( "blablabla" ) );

		OAuth2Client existing = oauth2Service.getClientById( "fredClient" );
		assertNotNull( existing );
		assertEquals( oAuth2Client, existing );

		assertSame( existing, principalMap.get( oAuth2Client.getId() ) );
		assertSame( existing, principalMap.get( oAuth2Client.getPrincipalName() ) );
		assertSame( existing, clientMap.get( "fredClient" ) );

		OAuth2Client fetchedAgain = oauth2Service.getClientById( "fredClient" );
		assertSame( existing, fetchedAgain );

		assertSame( existing, securityPrincipalService.getPrincipalByName( fetchedAgain.getPrincipalName() ) );
	}

	@Configuration
	@AcrossTestConfiguration(modules = AcrossWebModule.NAME)
	static class Config implements AcrossContextConfigurer
	{
		@Bean(name = SpringSecurityModuleCache.SECURITY_PRINCIPAL)
		@Exposed
		public ConcurrentMapCache securityPrincipalCache() {
			return new ConcurrentMapCache( SpringSecurityModuleCache.SECURITY_PRINCIPAL, true );
		}

		@Bean(name = OAuth2ModuleCache.CLIENTS)
		@Exposed
		public ConcurrentMapCache clientCache() {
			return new ConcurrentMapCache( OAuth2ModuleCache.CLIENTS, true );
		}

		@Override
		public void configure( AcrossContext context ) {
			context.addModule( acrossHibernateJpaModule() );
			context.addModule( userModule() );
			context.addModule( oauth2Module() );
			context.addModule( propertiesModule() );
			context.addModule( springSecurityModule() );

			context.addApplicationContextConfigurer(
					new AnnotatedClassConfigurer( EnableCachingConfiguration.class ),
					ConfigurerScope.MODULES_ONLY
			);
		}

		private PropertiesModule propertiesModule() {
			return new PropertiesModule();
		}

		private AcrossHibernateJpaModule acrossHibernateJpaModule() {
			return new AcrossHibernateJpaModule();
		}

		private UserModule userModule() {
			return new UserModule();
		}

		private OAuth2Module oauth2Module() {
			OAuth2Module oAuth2Module = new OAuth2Module();
			oAuth2Module.addApplicationContextConfigurer( RegisterCache.class );
			return oAuth2Module;
		}

		private SpringSecurityModule springSecurityModule() {
			return new SpringSecurityModule();
		}

		@Bean
		@Exposed
		public ResourceServerConfigurerAdapter dummyResourceServerConfigurerAdapter() {
			return new ResourceServerConfigurerAdapter()
			{
				@Override
				public void configure( ResourceServerSecurityConfigurer resources ) throws Exception {
					resources.resourceId( "dummyResourceId" );
				}

				@Override
				public void configure( HttpSecurity http ) throws Exception {
					http.requestMatchers().antMatchers( "/users/**", "/user/**", "/oauth/user_token" );
					http.authorizeRequests().anyRequest().authenticated();
				}
			};
		}
	}

	@EnableCaching
	@Configuration
	static class EnableCachingConfiguration
	{
	}

	@Configuration
	static class RegisterCache
	{
		@Autowired
		public SimpleCacheManager simpleCacheManager( AcrossCompositeCacheManager cacheManager,
		                                              @Qualifier(SpringSecurityModuleCache.SECURITY_PRINCIPAL) ConcurrentMapCache securityPrincipalCache,
		                                              @Qualifier(OAuth2ModuleCache.CLIENTS) ConcurrentMapCache clientCache ) {
			SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
			simpleCacheManager.setCaches( Arrays.asList( securityPrincipalCache, clientCache ) );

			cacheManager.addCacheManager( simpleCacheManager );

			simpleCacheManager.afterPropertiesSet();

			return simpleCacheManager;
		}
	}
}
