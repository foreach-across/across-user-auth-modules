package com.foreach.across.modules.oauth2.config;

import com.foreach.across.modules.oauth2.controllers.InvalidateTokenEndpoint;
import com.foreach.across.modules.oauth2.controllers.UserTokenEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Adds some additional OAuth endpoints.
 *
 * @see org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint
 * @see org.springframework.security.oauth2.provider.endpoint.FrameworkEndpointHandlerMapping
 */
@Configuration
public class OAuth2EndpointsConfiguration
{
	@Bean
	public InvalidateTokenEndpoint invalidateTokenEndpoint() {
		return new InvalidateTokenEndpoint();
	}

	@Bean
	public UserTokenEndpoint userTokenEndpoint() {
		return new UserTokenEndpoint();
	}
}
