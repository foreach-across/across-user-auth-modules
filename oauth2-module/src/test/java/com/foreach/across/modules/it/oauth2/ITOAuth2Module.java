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
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.oauth2.OAuth2Module;
import com.foreach.across.modules.oauth2.business.OAuth2Client;
import com.foreach.across.modules.oauth2.business.OAuth2ClientScope;
import com.foreach.across.modules.oauth2.business.OAuth2Scope;
import com.foreach.across.modules.oauth2.services.OAuth2Service;
import com.foreach.across.modules.properties.PropertiesModule;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.modules.user.UserModule;
import com.foreach.across.test.AcrossTestWebConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpointHandlerMapping;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@WebAppConfiguration
@ContextConfiguration(classes = ITOAuth2Module.Config.class)
public class ITOAuth2Module
{
	@Autowired
	private OAuth2Service oauth2Service;

	@Autowired
	private FrameworkEndpointHandlerMapping frameworkEndpointHandlerMapping;

	@Test
	public void verifyBootstrapped() {
		assertNotNull( oauth2Service );
		assertNotNull( frameworkEndpointHandlerMapping );
	}

	@Test
	public void verifyEndpointsDetected() {
		Set<String> endpoints = frameworkEndpointHandlerMapping.getPaths();

		assertTrue( endpoints.contains( "/oauth/invalidate" ) );
		assertTrue( endpoints.contains( "/oauth/user_token" ) );
	}

	@Test
	public void savingOauth2ClientTwiceDoesNotCauseStackOverflow() throws Exception {
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
		linkFullScope( client );
		oauth2Service.saveClient( client );

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

	private void linkFullScope( OAuth2Client client ) {
		OAuth2Scope fullScope = getFullScope();
		OAuth2ClientScope clientScope = new OAuth2ClientScope();
		clientScope.setAutoApprove( false );
		clientScope.setOAuth2Scope( fullScope );
		clientScope.setOAuth2Client( client );
		client.setOAuth2ClientScopes( Collections.singleton( clientScope ) );
	}

	private OAuth2Scope getFullScope() {
		Collection<OAuth2Scope> scopes = oauth2Service.getOAuth2Scopes();
		OAuth2Scope fullScope = null;
		for ( OAuth2Scope scope : scopes ) {
			if ( scope.getName().equals( "full" ) ) {
				fullScope = scope;
			}
		}
		if ( fullScope == null ) {
			fullScope = new OAuth2Scope();
			fullScope.setName( "full" );

			oauth2Service.saveScope( fullScope );
		}
		return fullScope;
	}

	@Configuration
	@AcrossTestWebConfiguration
	static class Config implements AcrossContextConfigurer
	{
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
			return new OAuth2Module();
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
