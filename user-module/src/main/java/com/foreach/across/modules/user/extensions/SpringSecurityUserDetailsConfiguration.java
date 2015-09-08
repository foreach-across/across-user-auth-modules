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
package com.foreach.across.modules.user.extensions;

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.modules.user.UserModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration to load inside the SpringSecurityModule ApplicationContext.
 */
@AcrossDepends(required = "SpringSecurityModule")
@Configuration
public class SpringSecurityUserDetailsConfiguration
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
