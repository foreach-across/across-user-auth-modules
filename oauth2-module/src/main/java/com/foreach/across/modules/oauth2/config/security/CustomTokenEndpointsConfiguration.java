package com.foreach.across.modules.oauth2.config.security;

import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.annotations.OrderInModule;
import com.foreach.across.core.context.AcrossContextUtils;
import com.foreach.across.modules.spring.security.configuration.SpringSecurityWebConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
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
public class CustomTokenEndpointsConfiguration extends SpringSecurityWebConfigurerAdapter
{
	@Autowired(required = false)
	private TokenStore tokenStore;

	@Autowired(required = false)
	private ResourceServerTokenServices tokenServices;

	private AccessDeniedHandler accessDeniedHandler = new OAuth2AccessDeniedHandler();

	@Autowired
	private AuthorizationServerEndpointsConfiguration endpoints;

	@Autowired
	private AcrossContext context;

	@Override
	public void configure( HttpSecurity http ) throws Exception {
		HttpSecurity.RequestMatcherConfigurer requests = http.requestMatchers();

		String userTokenPath = endpoints.oauth2EndpointHandlerMapping().getPath( "/oauth/user_token" );
		String invalidateTokenPath = endpoints.oauth2EndpointHandlerMapping().getPath( "/oauth/invalidate" );
		requests.antMatchers( userTokenPath, invalidateTokenPath );

		http
				.exceptionHandling().accessDeniedHandler( accessDeniedHandler )
				.and()
				.anonymous().disable()
				.csrf().disable()
				.authorizeRequests()
				.antMatchers( userTokenPath ).hasAuthority( "manage users" )
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
		Collection<ResourceServerConfigurer> configurers = AcrossContextUtils.getBeansOfType( context,
		                                                                                      ResourceServerConfigurer.class,
		                                                                                      true );
		for ( ResourceServerConfigurer configurer : configurers ) {
			configurer.configure( resources );
		}
	}
}
