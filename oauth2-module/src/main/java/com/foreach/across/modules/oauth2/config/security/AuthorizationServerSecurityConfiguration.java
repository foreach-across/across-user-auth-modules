package com.foreach.across.modules.oauth2.config.security;

import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.context.AcrossContextUtils;
import com.foreach.across.modules.spring.security.configuration.SpringSecurityWebConfigurerAdapter;
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
public class AuthorizationServerSecurityConfiguration extends SpringSecurityWebConfigurerAdapter
{
	@Autowired
	private ClientDetailsService clientDetailsService;

	@Autowired
	private AuthorizationServerEndpointsConfiguration endpoints;

	@Autowired
	private AcrossContext context;

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
		Collection<AuthorizationServerConfigurer> configurers = AcrossContextUtils.getBeansOfType( context,
		                                                                                           AuthorizationServerConfigurer.class,
		                                                                                           true );

		for ( AuthorizationServerConfigurer configurer : configurers ) {
			configurer.configure( oauthServer );
		}
	}
}
