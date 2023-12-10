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
package com.foreach.across.modules.oauth2.config.security;

import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.modules.spring.security.configuration.AcrossWebSecurityConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpointHandlerMapping;

import java.util.Collection;

/**
 * @author Arne Vandamme
 */
@Configuration
@Import({ AuthorizationServerEndpointsConfiguration.class, AuthorizationServerConfiguration.class })
public class AuthorizationServerSecurityConfiguration implements AcrossWebSecurityConfigurer
{
	@Autowired
	private ClientDetailsService clientDetailsService;

	@Autowired
	private AuthorizationServerEndpointsConfiguration endpoints;

	@Autowired
	private AcrossContextBeanRegistry contextBeanRegistry;

	@Override
	public void configure( HttpSecurity http ) throws Exception {
		AuthorizationServerSecurityConfigurer configurer = new AuthorizationServerSecurityConfigurer();
		FrameworkEndpointHandlerMapping handlerMapping = endpoints.oauth2EndpointHandlerMapping();
		http.setSharedObject( FrameworkEndpointHandlerMapping.class, handlerMapping );
		configure( configurer );
		http.apply( configurer );
		String tokenEndpointPath = handlerMapping.getPath( "/oauth/token" );
		String tokenKeyPath = handlerMapping.getPath( "/oauth/token_key" );
		String checkTokenPath = handlerMapping.getPath( "/oauth/check_token" );

		// @formatter:off
		http
				.authorizeRequests()
				.antMatchers( tokenEndpointPath ).fullyAuthenticated()
				.antMatchers( tokenKeyPath ).access( configurer.getTokenKeyAccess() )
				.antMatchers( checkTokenPath ).access( configurer.getCheckTokenAccess() )
				.and()
				.requestMatchers()
				.antMatchers( tokenEndpointPath, tokenKeyPath, checkTokenPath );
		// @formatter:on
		http.setSharedObject( ClientDetailsService.class, clientDetailsService );
	}

	protected void configure( AuthorizationServerSecurityConfigurer oauthServer ) throws Exception {
		Collection<AuthorizationServerConfigurer> configurers = contextBeanRegistry.getBeansOfType(
				AuthorizationServerConfigurer.class,
				true );

		for ( AuthorizationServerConfigurer configurer : configurers ) {
			configurer.configure( oauthServer );
		}
	}
}
