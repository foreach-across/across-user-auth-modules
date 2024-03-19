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

import com.foreach.across.core.annotations.OrderInModule;
import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.util.Collection;

/**
 * Configure additional token endpoints.  These require OAuth2 authentication so behave like a resource,
 * but they are not handled by the default resource server configuration.
 *
 * @author Arne Vandamme
 */
@Configuration
@OrderInModule(2)
public class CustomTokenEndpointsConfiguration
{
	@Autowired(required = false)
	private TokenStore tokenStore;

	@Autowired(required = false)
	private ResourceServerTokenServices tokenServices;

	private AccessDeniedHandler accessDeniedHandler = new OAuth2AccessDeniedHandler();

	@Autowired
	private AuthorizationServerEndpointsConfiguration endpoints;

	@Autowired
	private AcrossContextBeanRegistry contextBeanRegistry;

	public SecurityFilterChain configure( HttpSecurity http ) throws Exception {
		HttpSecurity.RequestMatcherConfigurer requests = http.requestMatchers();

		String userTokenPath = endpoints.oauth2EndpointHandlerMapping().getPath( "/oauth/user_token" );
		String invalidateTokenPath = endpoints.oauth2EndpointHandlerMapping().getPath( "/oauth/invalidate" );
		String authorizePath = endpoints.oauth2EndpointHandlerMapping().getPath( "/oauth/authorize" );
		requests.antMatchers( userTokenPath, invalidateTokenPath, authorizePath );

		http
				.sessionManagement().sessionCreationPolicy( SessionCreationPolicy.STATELESS ).and()
				.exceptionHandling().accessDeniedHandler( accessDeniedHandler )
				.and()
				.anonymous().disable()
				.csrf().disable()
				.authorizeRequests()
				.antMatchers( userTokenPath ).hasAuthority( "manage users" )
				.antMatchers( authorizePath ).authenticated()
				.antMatchers( invalidateTokenPath ).authenticated();

		// And set the default expression handler in case one isn't explicit elsewhere
		http.authorizeRequests().expressionHandler( new OAuth2WebSecurityExpressionHandler() );
		ResourceServerSecurityConfigurer resources = new ResourceServerSecurityConfigurer();
		http.apply( resources );
		if ( tokenServices != null ) {
			resources.tokenServices( tokenServices );
		}
		else {
			if ( tokenStore != null ) {
				resources.tokenStore( tokenStore );
			}
		}

		// Resource ids are required to be configured to support the same access tokens as the others
		Collection<ResourceServerConfigurer> configurers = contextBeanRegistry.getBeansOfType(
				ResourceServerConfigurer.class,
				true );

		for ( ResourceServerConfigurer configurer : configurers ) {
			configurer.configure( resources );
		}
		return http.build();
	}
}
