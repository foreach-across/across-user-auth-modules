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
import com.foreach.across.core.filters.BeanFilterComposite;
import com.foreach.across.core.filters.ClassBeanFilter;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.oauth2.OAuth2Module;
import com.foreach.across.modules.oauth2.OAuth2ModuleCache;
import com.foreach.across.modules.oauth2.business.OAuth2Client;
import com.foreach.across.modules.oauth2.business.OAuth2ClientScope;
import com.foreach.across.modules.oauth2.business.OAuth2Scope;
import com.foreach.across.modules.oauth2.services.OAuth2Service;
import com.foreach.across.modules.properties.PropertiesModule;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.modules.spring.security.SpringSecurityModuleCache;
import com.foreach.across.modules.user.UserModule;
import com.foreach.across.modules.web.AcrossWebModule;
import com.foreach.across.test.AcrossTestConfiguration;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * @author Arne Vandamme
 * @since 1.1.2
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@WebAppConfiguration
@ContextConfiguration
public class ITConcurrentTokenCreation
{
	// Maximum number of threads for concurrency, the datasource needs to have a larger
	// pool size which is at least double to allow for efficient lock taking and releasing
	private static final int MAX_THREADS = 10;

	@Autowired
	private OAuth2Service oAuth2Service;

	@Autowired
	private AuthorizationServerTokenServices tokenServices;

	@Autowired
	private HikariDataSource dataSource;

	@Before
	public void before() {
		dataSource.getHikariConfigMXBean().setMaximumPoolSize( MAX_THREADS * 2 );

		OAuth2Scope scope = new OAuth2Scope();
		scope.setName( "full" );
		oAuth2Service.saveScope( scope );

		// install a test client
		OAuth2Client dto = new OAuth2Client();
		dto.setClientId( "test" );
		dto.setClientSecret( "test" );
		dto.setSecretRequired( true );
		Set<String> trustedGrantTypes = new HashSet<>();
		trustedGrantTypes.add( "client_credentials" );
		trustedGrantTypes.add( "password" );
		trustedGrantTypes.add( "refresh_token" );
		dto.getAuthorizedGrantTypes().addAll( trustedGrantTypes );
		dto.getResourceIds().addAll( Collections.singleton( "test_resources" ) );

		OAuth2ClientScope clientScope = new OAuth2ClientScope();
		clientScope.setAutoApprove( false );
		clientScope.setOAuth2Scope( scope );
		clientScope.setOAuth2Client( dto );
		dto.setOAuth2ClientScopes( Collections.singleton( clientScope ) );

		dto.setAccessTokenValiditySeconds( 86400 );
		dto.setRefreshTokenValiditySeconds( 86400 );

		oAuth2Service.saveClient( dto );
	}

	@Test
	public void concurrentClientCredentialsTokensWithLocking() throws Exception {
		Collection<Callable<String>> tokenTasks = new ArrayList<>( 1000 );
		for ( int i = 0; i < 1000; i++ ) {
			tokenTasks.add( () -> {
				OAuth2Request request = new OAuth2Request( Collections.<String, String>emptyMap(),
				                                           "test",
				                                           Collections.<GrantedAuthority>emptyList(),
				                                           true,
				                                           Collections.singleton( "full" ),
				                                           Collections.singleton( "test_resources" ),
				                                           "",
				                                           Collections.<String>emptySet(),
				                                           Collections.<String, Serializable>emptyMap() );

				OAuth2Authentication authentication = new OAuth2Authentication( request, null );
				OAuth2AccessToken token = tokenServices.createAccessToken( authentication );

				return token.getValue();
			} );
		}

		ExecutorService executor = Executors.newFixedThreadPool( MAX_THREADS );
		Collection<Future<String>> generatedTokens = executor.invokeAll( tokenTasks );

		Map<?, Long> tokenCount = generatedTokens.stream().map( f -> {
			try {
				return f.get();
			}
			catch ( Exception e ) {
				throw new RuntimeException( e );
			}

		} ).collect( Collectors.groupingBy( p -> p, Collectors.counting() ) );

		executor.shutdownNow();

		assertEquals( 1, tokenCount.size() );
		tokenCount.forEach( ( t, c ) -> assertEquals( Long.valueOf( 1000 ), c ) );
	}

	@Configuration
	@AcrossTestConfiguration(modules = { AcrossWebModule.NAME })
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
			oAuth2Module.setExposeFilter( new BeanFilterComposite( oAuth2Module.getExposeFilter(), new ClassBeanFilter(
					AuthorizationServerTokenServices.class ) ) );
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
}
