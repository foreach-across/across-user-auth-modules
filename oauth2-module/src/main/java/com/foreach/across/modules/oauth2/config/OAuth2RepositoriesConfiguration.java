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

import com.foreach.across.modules.oauth2.repositories.OAuth2ClientRepository;
import com.foreach.across.modules.oauth2.repositories.OAuth2ClientRepositoryImpl;
import com.foreach.across.modules.oauth2.repositories.OAuth2ScopeRepository;
import com.foreach.across.modules.oauth2.repositories.OAuth2ScopeRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OAuth2RepositoriesConfiguration
{
	@Bean
	public OAuth2ClientRepository oAuth2ClientRepository() {
		return new OAuth2ClientRepositoryImpl();
	}

	@Deprecated
	@Bean
	public OAuth2ScopeRepository oAuth2Repository() {
		return new OAuth2ScopeRepositoryImpl();
	}
}
