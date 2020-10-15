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

package com.foreach.across.modules.oauth2.repositories;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.oauth2.OAuth2Module;
import com.foreach.across.modules.properties.PropertiesModule;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.modules.user.UserModule;
import com.foreach.across.test.AcrossTestConfiguration;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * @author jurgen
 * @since 11/03/2016
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestOAuthAccessTokenRepository.Config.class)
@DirtiesContext
public class TestOAuthAccessTokenRepository
{
	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Autowired
	private AcrossContextBeanRegistry registry;

	@Test
	public void testThatJdbcTokenStoreDoesNotFail() {
		TokenStore tokenStore = registry.getBeanOfTypeFromModule( OAuth2Module.NAME, TokenStore.class );
		OAuth2AccessToken defaultOAuth2AccessToken = new DefaultOAuth2AccessToken( "test" );

		OAuth2Request oAuth2Request = new OAuth2Request( Collections.<String, String>emptyMap(),
		                                                 "testClient",
		                                                 Collections.<GrantedAuthority>emptyList(),
		                                                 true,
		                                                 Collections.singleton( "full" ),
		                                                 Collections.singleton( "someresource" ),
		                                                 "",
		                                                 Collections.<String>emptySet(),
		                                                 Collections.<String, Serializable>emptyMap() );
		OAuth2Authentication auth2Authentication = new OAuth2Authentication( oAuth2Request,
		                                                                     new UsernamePasswordAuthenticationToken(
				                                                                     "principal", "credentials" ) );
		OAuth2AccessToken oAuth2AccessToken = new DefaultOAuth2AccessToken( "test" );

		tokenStore.storeAccessToken( oAuth2AccessToken, auth2Authentication );
		OAuth2AccessToken retrievedOAuth2AccessToken = tokenStore.getAccessToken( auth2Authentication );
		assertNotNull( retrievedOAuth2AccessToken );
		assertEquals( "test", retrievedOAuth2AccessToken.getValue() );

		Collection<OAuth2AccessToken> accessTokenCollection = tokenStore.findTokensByClientId( "testClient" );
		assertEquals( 1, accessTokenCollection.size() );

	}

	@Test
	public void testThatDateCreatedIsAddedToAccessToken() {
		Date now = new Date();
		List<OAuth2AccessToken> accessTokens = jdbcTemplate.query( "SELECT * FROM OAUTH_ACCESS_TOKEN",
		                                                           ( rs, rowNum ) -> {
			                                                           Timestamp timestamp = rs.getTimestamp(
					                                                           "date_created" );
			                                                           assertNotNull( timestamp );
			                                                           assertTrue(
					                                                           DateUtils.isSameDay( now, timestamp ) );
			                                                           return mock( OAuth2AccessToken.class );
		                                                           } );
		assertEquals( 1, accessTokens.size() );
	}

	@Configuration
	@AcrossTestConfiguration
	static class Config extends ResourceServerConfigurerAdapter implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			context.addModule( new AcrossHibernateJpaModule() );
			context.addModule( new SpringSecurityModule() );
			context.addModule( new OAuth2Module() );
			context.addModule( new UserModule() );
			context.addModule( new PropertiesModule() );
		}

		@Bean
		@Autowired
		public NamedParameterJdbcTemplate jdbcTemplate( DataSource dataSource ) {
			return new NamedParameterJdbcTemplate( dataSource );
		}

		@Override
		public void configure( HttpSecurity http ) throws Exception {
			http.authorizeRequests().antMatchers( "/api/**" ).authenticated();
		}
	}
}
