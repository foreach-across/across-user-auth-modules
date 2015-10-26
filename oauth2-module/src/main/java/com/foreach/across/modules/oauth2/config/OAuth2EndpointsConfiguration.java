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
package com.foreach.across.modules.oauth2.config;

import com.foreach.across.modules.oauth2.controllers.AcrossWhitelabelApprovalEndpoint;
import com.foreach.across.modules.oauth2.controllers.InvalidateTokenEndpoint;
import com.foreach.across.modules.oauth2.controllers.UserTokenEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
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
	@Autowired
	private CacheManager cacheManager;

	@Bean
	public InvalidateTokenEndpoint invalidateTokenEndpoint() {
		return new InvalidateTokenEndpoint( cacheManager );
	}

	@Bean
	public UserTokenEndpoint userTokenEndpoint() {
		return new UserTokenEndpoint();
	}

	@Bean
	public AcrossWhitelabelApprovalEndpoint acrossWhitelabelApprovalEndpoint() {
		return new AcrossWhitelabelApprovalEndpoint();
	}
}
