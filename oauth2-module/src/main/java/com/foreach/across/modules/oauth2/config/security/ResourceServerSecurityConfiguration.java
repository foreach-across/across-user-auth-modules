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
import com.foreach.across.modules.spring.security.configuration.SpringSecurityWebConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpointHandlerMapping;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 * @author Arne Vandamme
 */
@Configuration
@Import(ResourceServerConfiguration.class)
@OrderInModule(3)
public class ResourceServerSecurityConfiguration extends SpringSecurityWebConfigurerAdapter
{
	@Autowired(required = false)
	private TokenStore tokenStore;

	@Autowired(required = false)
	private ResourceServerTokenServices tokenServices;

	private AccessDeniedHandler accessDeniedHandler = new OAuth2AccessDeniedHandler();

	@Autowired(required = false)
	private AuthorizationServerEndpointsConfiguration endpoints;

	@Autowired
	private AcrossContextBeanRegistry contextBeanRegistry;

	private static class NotOAuthRequestMatcher implements RequestMatcher
	{

		private FrameworkEndpointHandlerMapping mapping;

		public NotOAuthRequestMatcher( FrameworkEndpointHandlerMapping mapping ) {
			this.mapping = mapping;
		}

		@Override
		public boolean matches( HttpServletRequest request ) {
			String requestPath = getRequestPath( request );
			for ( String path : mapping.getPaths() ) {
				if ( requestPath.startsWith( path ) ) {
					return false;
				}
			}
			return true;
		}

		private String getRequestPath( HttpServletRequest request ) {
			String url = request.getServletPath();

			if ( request.getPathInfo() != null ) {
				url += request.getPathInfo();
			}

			return url;
		}

	}

	@Override
	public void configure( HttpSecurity http ) throws Exception {
		HttpSecurity.RequestMatcherConfigurer requests = http.requestMatchers();
		if ( endpoints != null ) {
			// Assume we are in an Authorization Server
			requests.requestMatchers( new NotOAuthRequestMatcher( endpoints.oauth2EndpointHandlerMapping() ) );
		}
		// @formatter:off
		http
				.exceptionHandling().accessDeniedHandler( accessDeniedHandler )
				.and()
				.anonymous().disable()
				.csrf().disable();
		// @formatter:on

		Collection<ResourceServerConfigurer> configurers = contextBeanRegistry.getBeansOfType(
				ResourceServerConfigurer.class,
				true );

		for ( ResourceServerConfigurer configurer : configurers ) {
			// Delegates can add authorizeRequests() here
			configurer.configure( http );
		}
		if ( configurers.isEmpty() ) {
			// Add anyRequest() last as a fall back. Spring Security would replace an existing anyRequest() matcher
			// with this one, so to avoid that we only add it if the user hasn't configured anything.
			http.authorizeRequests().anyRequest().authenticated();
		}
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
		for ( ResourceServerConfigurer configurer : configurers ) {
			configurer.configure( resources );
		}
	}
}
