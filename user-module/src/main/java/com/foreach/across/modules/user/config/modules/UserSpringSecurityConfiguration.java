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
package com.foreach.across.modules.user.config.modules;

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.modules.user.UserModule;
import com.foreach.across.modules.user.controllers.UserAclController;
import com.foreach.across.modules.user.security.CurrentUserProxy;
import com.foreach.across.modules.user.security.CurrentUserProxyImpl;
import com.foreach.across.modules.user.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Registers the UserDetailsService implementation for the User module.
 */
@AcrossDepends(required = "SpringSecurityModule")
@Configuration
public class UserSpringSecurityConfiguration
{
	@Bean
	public UserDetailsService userDetailsServiceImpl() {
		return new UserDetailsServiceImpl();
	}

	@Bean
	public CurrentUserProxy currentUserProxy() {
		return new CurrentUserProxyImpl();
	}

	@Bean
	@AcrossDepends(required = "SpringSecurityAclModule")
	public UserAclController userAclController() {
		return new UserAclController();
	}

	/**
	 * Configuration to load inside the SpringSecurityModule ApplicationContext.
	 */
	@Configuration
	public static class UserDetailsServiceConfiguration
	{
		@Autowired
		private AcrossContextBeanRegistry contextBeanRegistry;

		@Autowired
		@SuppressWarnings("SignatureDeclareThrowsException")
		public void configureGlobal( AuthenticationManagerBuilder auth ) throws Exception {
			PasswordEncoder userPasswordEncoder = contextBeanRegistry.getBeanOfTypeFromModule(
					UserModule.NAME,
					PasswordEncoder.class
			);
			UserDetailsService userDetailsService = contextBeanRegistry.getBeanOfTypeFromModule(
					UserModule.NAME,
					UserDetailsService.class
			);

			auth.userDetailsService( userDetailsService ).passwordEncoder( userPasswordEncoder );
		}
	}
}
