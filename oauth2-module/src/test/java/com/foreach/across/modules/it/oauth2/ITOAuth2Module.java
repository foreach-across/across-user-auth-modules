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
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.oauth2.OAuth2Module;
import com.foreach.across.modules.oauth2.OAuth2ModuleSettings;
import com.foreach.across.modules.oauth2.business.OAuth2Client;
import com.foreach.across.modules.oauth2.business.OAuth2ClientScope;
import com.foreach.across.modules.oauth2.business.OAuth2Scope;
import com.foreach.across.modules.oauth2.services.OAuth2Service;
import com.foreach.across.modules.properties.PropertiesModule;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalLabelResolverStrategy;
import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalService;
import com.foreach.across.modules.user.UserModule;
import com.foreach.across.modules.web.AcrossWebModule;
import com.foreach.across.test.AcrossTestWebContext;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.*;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpointHandlerMapping;

import java.util.*;
import java.util.stream.IntStream;

import static com.foreach.across.test.support.AcrossTestBuilders.web;
import static org.junit.Assert.*;

public class ITOAuth2Module
{
	private static final Logger LOG = LoggerFactory.getLogger( "org.hibernate.SQL" );

	@Test
	public void verifyEndpointsDetected() {
		BaseConfiguration configuration = new BaseConfiguration()
		{
			@Override
			protected void configureModule( OAuth2Module module ) {
			}
		};

		try (AcrossTestWebContext ctx = web().configurer( configuration ).build()) {
			FrameworkEndpointHandlerMapping handlerMapping
					= ctx.getBeanOfType( FrameworkEndpointHandlerMapping.class );

			Set<String> endpoints = handlerMapping.getPaths();

			assertTrue( endpoints.contains( "/oauth/invalidate" ) );
			assertTrue( endpoints.contains( "/oauth/user_token" ) );
		}
	}

	@Test
	public void clientWithScopesQueryFetching() {
		BaseConfiguration configuration = new BaseConfiguration()
		{
			@Override
			protected void configureModule( OAuth2Module module ) {
			}
		};

		try (AcrossTestWebContext ctx = web().configurer( configuration ).build()) {
			OAuth2Service oauth2Service = ctx.getBeanOfType( OAuth2Service.class );

			OAuth2Scope full = new OAuth2Scope();
			full.setName( "full" );

			oauth2Service.saveScope( full );

			IntStream.range( 0, 36 )
			         .forEach( i -> {
				         OAuth2Client client = new OAuth2Client();
				         client.setClientId( "generated-" + i );
				         client.setClientSecret( "one" );
				         client.setRegisteredRedirectUri( Arrays.asList( "url-1", "url-2" ) );
				         client.setResourceIds( Arrays.asList( "users", "products" ) );
				         client.setAuthorizedGrantTypes( Arrays.asList( "password", "authorization" ) );
				         client.addScope( full, false );
				         oauth2Service.saveClient( client );
			         } );

			LOG.info( "" );
			LOG.info( "Before fetching client" );
			LOG.info( "" );

			StopWatch sw = new StopWatch();
			sw.start();

			OAuth2Client fetched = oauth2Service.getClientById( "generated-0" );
			assertNotNull( fetched );

			LOG.info( "" );
			LOG.info( "After fetching client" );
			LOG.info( "" );

			assertTrue( oauth2Service.getOAuth2Clients().size() >= 36 );

			sw.stop();

			LOG.info( "" );
			LOG.info( "Fetching single client and all clients took {} ms", sw.getTime() );
			LOG.info( "" );
		}
	}

	@Test
	public void verifyNoCaching() {
		BaseConfiguration configuration = new BaseConfiguration()
		{
			@Override
			protected void configureModule( OAuth2Module module ) {
			}
		};

		try (AcrossTestWebContext ctx = web().configurer( configuration ).build()) {
			OAuth2Service oauth2Service = ctx.getBeanOfType( OAuth2Service.class );
			SecurityPrincipalService securityPrincipalService = ctx.getBeanOfType( SecurityPrincipalService.class );

			OAuth2Client oAuth2Client = new OAuth2Client();
			oAuth2Client.setClientId( "someclient" );
			oAuth2Client.setClientSecret( "fred" );
			oAuth2Client.setSecretRequired( true );

			oauth2Service.saveClient( oAuth2Client );

			OAuth2Client existing = oauth2Service.getClientById( "someclient" );
			assertNotNull( existing );

			assertEquals( oAuth2Client, existing );

			OAuth2Client fetched = oauth2Service.getClientById( "someclient" );
			assertNotNull( fetched );
			assertEquals( oAuth2Client, fetched );
			assertNotSame( existing, fetched );

			fetched = securityPrincipalService.getPrincipalByName( oAuth2Client.getPrincipalName() );
			assertNotNull( fetched );
			assertEquals( oAuth2Client, fetched );
			assertNotSame( existing, fetched );
		}
	}

	@Test
	public void defaultApprovalHandler() {
		BaseConfiguration configuration = new BaseConfiguration()
		{
			@Override
			protected void configureModule( OAuth2Module module ) {
				module.setProperty( OAuth2ModuleSettings.APPROVAL_HANDLER,
				                    OAuth2ModuleSettings.ApprovalHandler.DEFAULT );
			}
		};

		try (AcrossTestWebContext ctx = web().configurer( configuration ).build()) {
			UserApprovalHandler approvalHandler
					= ctx.getBeanOfTypeFromModule( OAuth2Module.NAME, UserApprovalHandler.class );

			assertNotNull( approvalHandler );
			assertTrue( approvalHandler instanceof DefaultUserApprovalHandler );

			ApplicationContext applicationContext
					= ctx.contextInfo().getModuleInfo( OAuth2Module.NAME ).getApplicationContext();

			assertTrue( applicationContext.containsBeanDefinition( "approvalStore" ) );
			assertFalse( ( (SingletonBeanRegistry) applicationContext.getAutowireCapableBeanFactory() )
					             .containsSingleton( "approvalStore" ) );
		}
	}

	@Test
	public void savingOauth2ClientTwiceDoesNotCauseStackOverflow() throws Exception {
		BaseConfiguration configuration = new BaseConfiguration()
		{
			@Override
			protected void configureModule( OAuth2Module module ) {
			}
		};

		try (AcrossTestWebContext ctx = web().configurer( configuration ).build()) {
			OAuth2Service oauth2Service = ctx.getBeanOfType( OAuth2Service.class );

			Set<String> allGrantTypes = new HashSet<>();
			allGrantTypes.add( "authorization_code" );
			allGrantTypes.add( "client_credentials" );
			allGrantTypes.add( "refresh_token" );

			Set<String> resourceIds = new HashSet<>();
			resourceIds.add( "knooppunt" );

			OAuth2Client client = new OAuth2Client();
			client.setClientId( "foo" );
			client.setClientSecret( "bar" );
			client.setSecretRequired( true );
			client.setAccessTokenValiditySeconds( 86400 );
			client.setRefreshTokenValiditySeconds( 86400 * 365 * 10 );
			client.getAuthorizedGrantTypes().addAll( allGrantTypes );
			client.getResourceIds().addAll( resourceIds );
			linkFullScope( client, oauth2Service );
			oauth2Service.saveClient( client );

			// client label
			assertEquals(
					"foo",
					ctx.getBeanOfType( SecurityPrincipalLabelResolverStrategy.class ).resolvePrincipalLabel( "foo" )
			);

			OAuth2Client clientById = oauth2Service.getClientById( "foo" );
			OAuth2Client dto = clientById.toDto();
			dto.setRefreshTokenValiditySeconds( 55 );
			try {
				oauth2Service.saveClient( dto );
			}
			catch ( StackOverflowError e ) {
				fail();
			}
		}
	}

	private void linkFullScope( OAuth2Client client, OAuth2Service oAuth2Service ) {
		OAuth2Scope fullScope = getFullScope( oAuth2Service );
		OAuth2ClientScope clientScope = new OAuth2ClientScope();
		clientScope.setAutoApprove( false );
		clientScope.setOAuth2Scope( fullScope );
		clientScope.setOAuth2Client( client );
		client.setOAuth2ClientScopes( Collections.singleton( clientScope ) );
	}

	private OAuth2Scope getFullScope( OAuth2Service oAuth2Service ) {
		Collection<OAuth2Scope> scopes = oAuth2Service.getOAuth2Scopes();
		OAuth2Scope fullScope = null;
		for ( OAuth2Scope scope : scopes ) {
			if ( scope.getName().equals( "full" ) ) {
				fullScope = scope;
			}
		}
		if ( fullScope == null ) {
			fullScope = new OAuth2Scope();
			fullScope.setName( "full" );

			oAuth2Service.saveScope( fullScope );
		}
		return fullScope;
	}

	@Test
	public void tokenStoreApprovalHandler() {
		BaseConfiguration configuration = new BaseConfiguration()
		{
			@Override
			protected void configureModule( OAuth2Module module ) {
				module.setProperty( OAuth2ModuleSettings.APPROVAL_HANDLER,
				                    OAuth2ModuleSettings.ApprovalHandler.TOKEN_STORE );
			}
		};

		try (AcrossTestWebContext ctx = web().configurer( configuration ).build()) {
			UserApprovalHandler approvalHandler
					= ctx.getBeanOfTypeFromModule( OAuth2Module.NAME, UserApprovalHandler.class );

			assertNotNull( approvalHandler );
			assertTrue( approvalHandler instanceof TokenStoreUserApprovalHandler );

			ApplicationContext applicationContext
					= ctx.contextInfo().getModuleInfo( OAuth2Module.NAME ).getApplicationContext();

			assertTrue( applicationContext.containsBeanDefinition( "approvalStore" ) );
			assertFalse( ( (SingletonBeanRegistry) applicationContext.getAutowireCapableBeanFactory() )
					             .containsSingleton( "approvalStore" ) );
		}
	}

	@Test
	public void jdbcApprovalStore() {
		BaseConfiguration configuration = new BaseConfiguration()
		{
			@Override
			protected void configureModule( OAuth2Module module ) {
				module.setProperty( OAuth2ModuleSettings.APPROVAL_HANDLER,
				                    OAuth2ModuleSettings.ApprovalHandler.APPROVAL_STORE );
				module.setProperty( OAuth2ModuleSettings.APPROVAL_STORE, OAuth2ModuleSettings.ApprovalStore.JDBC );
			}
		};

		try (AcrossTestWebContext ctx = web().configurer( configuration ).build()) {
			UserApprovalHandler approvalHandler
					= ctx.getBeanOfTypeFromModule( OAuth2Module.NAME, UserApprovalHandler.class );

			assertNotNull( approvalHandler );
			assertTrue( approvalHandler instanceof ApprovalStoreUserApprovalHandler );

			ApprovalStore approvalStore
					= ctx.getBeanOfTypeFromModule( OAuth2Module.NAME, ApprovalStore.class );

			assertNotNull( approvalStore );
			assertTrue( approvalStore instanceof JdbcApprovalStore );

			ApplicationContext applicationContext
					= ctx.contextInfo().getModuleInfo( OAuth2Module.NAME ).getApplicationContext();
			assertTrue( ( (SingletonBeanRegistry) applicationContext.getAutowireCapableBeanFactory() )
					            .containsSingleton( "approvalStore" ) );
		}
	}

	@Test
	public void memoryApprovalStore() {
		BaseConfiguration configuration = new BaseConfiguration()
		{
			@Override
			protected void configureModule( OAuth2Module module ) {
				module.setProperty( OAuth2ModuleSettings.APPROVAL_HANDLER,
				                    OAuth2ModuleSettings.ApprovalHandler.APPROVAL_STORE );
				module.setProperty( OAuth2ModuleSettings.APPROVAL_STORE, OAuth2ModuleSettings.ApprovalStore.IN_MEMORY );
			}
		};

		try (AcrossTestWebContext ctx = web().configurer( configuration ).build()) {
			UserApprovalHandler approvalHandler
					= ctx.getBeanOfTypeFromModule( OAuth2Module.NAME, UserApprovalHandler.class );

			assertNotNull( approvalHandler );
			assertTrue( approvalHandler instanceof ApprovalStoreUserApprovalHandler );

			ApprovalStore approvalStore
					= ctx.getBeanOfTypeFromModule( OAuth2Module.NAME, ApprovalStore.class );

			assertNotNull( approvalStore );
			assertTrue( approvalStore instanceof InMemoryApprovalStore );

			ApplicationContext applicationContext
					= ctx.contextInfo().getModuleInfo( OAuth2Module.NAME ).getApplicationContext();
			assertTrue( ( (SingletonBeanRegistry) applicationContext.getAutowireCapableBeanFactory() )
					            .containsSingleton( "approvalStore" ) );
		}
	}

	@Test
	public void tokenApprovalStore() {
		BaseConfiguration configuration = new BaseConfiguration()
		{
			@Override
			protected void configureModule( OAuth2Module module ) {
				module.setProperty( OAuth2ModuleSettings.APPROVAL_HANDLER,
				                    OAuth2ModuleSettings.ApprovalHandler.APPROVAL_STORE );
				module.setProperty( OAuth2ModuleSettings.APPROVAL_STORE, OAuth2ModuleSettings.ApprovalStore.TOKEN );
			}
		};

		try (AcrossTestWebContext ctx = web().configurer( configuration ).build()) {
			UserApprovalHandler approvalHandler
					= ctx.getBeanOfTypeFromModule( OAuth2Module.NAME, UserApprovalHandler.class );

			assertNotNull( approvalHandler );
			assertTrue( approvalHandler instanceof ApprovalStoreUserApprovalHandler );

			ApprovalStore approvalStore
					= ctx.getBeanOfTypeFromModule( OAuth2Module.NAME, ApprovalStore.class );

			assertNotNull( approvalStore );
			assertTrue( approvalStore instanceof TokenApprovalStore );

			ApplicationContext applicationContext
					= ctx.contextInfo().getModuleInfo( OAuth2Module.NAME ).getApplicationContext();
			assertTrue( ( (SingletonBeanRegistry) applicationContext.getAutowireCapableBeanFactory() )
					            .containsSingleton( "approvalStore" ) );
		}
	}

	static abstract class BaseConfiguration implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			context.addModule( new AcrossWebModule() );
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
			AcrossHibernateJpaModule jpaModule = new AcrossHibernateJpaModule();
			jpaModule.setHibernateProperty( "show_sql", "true" );
			jpaModule.setHibernateProperty( "format_sql", "true" );
			jpaModule.setHibernateProperty( "use_sql_comments", "true" );
			return jpaModule;
		}

		private UserModule userModule() {
			return new UserModule();
		}

		private OAuth2Module oauth2Module() {
			OAuth2Module oAuth2Module = new OAuth2Module();
			configureModule( oAuth2Module );
			return oAuth2Module;
		}

		private SpringSecurityModule springSecurityModule() {
			SpringSecurityModule springSecurityModule = new SpringSecurityModule();
			springSecurityModule.addApplicationContextConfigurer( DummyResourceServerConfigurerAdapter.class );
			return springSecurityModule;
		}

		protected abstract void configureModule( OAuth2Module module );
	}

	@Configuration
	static class DummyResourceServerConfigurerAdapter extends ResourceServerConfigurerAdapter
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
	}
}
