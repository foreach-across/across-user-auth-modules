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

import com.foreach.across.core.annotations.ModuleConfiguration;
import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.modules.user.UserModule;
import com.foreach.across.modules.user.security.UserDirectoryAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration to load inside the SpringSecurityModule ApplicationContext.
 */
@ModuleConfiguration(SpringSecurityModule.NAME)
@EnableGlobalAuthentication
@RequiredArgsConstructor
class SpringSecurityUserDetailsConfiguration
{
	private final AcrossContextBeanRegistry contextBeanRegistry;

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

		UserDirectoryAuthenticationProvider userDirectoryAuthenticationProvider
				= contextBeanRegistry.getBeanOfTypeFromModule( UserModule.NAME,
				                                               UserDirectoryAuthenticationProvider.class );
		auth.authenticationProvider( userDirectoryAuthenticationProvider );

		// Configure the global user details for remember me functionality
		auth.userDetailsService( userDetailsService ).passwordEncoder( userPasswordEncoder );
	}

	// I haven't been able to figure out a way to get rid of the AuthenticationManagerBuilder above; see:
	// https://docs.spring.io/spring-security/reference/5.8/migration/servlet/config.html#_publish_an_authenticationmanager_bean

	/*
	// This naive option 1 doesn't work:

	@Bean
	public AuthenticationProvider authenticationProvider() {
		return contextBeanRegistry.getBeanOfTypeFromModule(
				UserModule.NAME,
				UserDirectoryAuthenticationProvider.class
		);
	}

	// (Option 1b) But is slightly better with this removed:
	@Bean
	public PasswordEncoder passwordEncoder() {
		return contextBeanRegistry.getBeanOfTypeFromModule(
				UserModule.NAME,
				PasswordEncoder.class
		);
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return contextBeanRegistry.getBeanOfTypeFromModule(
				UserModule.NAME,
				UserDetailsService.class
		);
	}
	*/

	/*
	// (Option 2) This is as close as I can get to the AuthenticationManager that is created by the AuthenticationManagerBuilder,
	// but the ITPlatformTestApplication.adminWebModuleListsUserOverviewForAuthenticatedUser() fails with:
	//   java.lang.IllegalArgumentException: There is no PasswordEncoder mapped for the id "null"
	//	 at org.springframework.security.crypto.password.DelegatingPasswordEncoder$UnmappedIdPasswordEncoder.matches(DelegatingPasswordEncoder.java:289)
	//	 at org.springframework.security.crypto.password.DelegatingPasswordEncoder.matches(DelegatingPasswordEncoder.java:237)
	//	 at org.springframework.security.authentication.dao.DaoAuthenticationProvider.additionalAuthenticationChecks(DaoAuthenticationProvider.java:77)
	@Bean
	public AuthenticationManager authenticationManager() {
		PasswordEncoder userPasswordEncoder = contextBeanRegistry.getBeanOfTypeFromModule(
				UserModule.NAME,
				PasswordEncoder.class
		);
		UserDetailsService userDetailsService = contextBeanRegistry.getBeanOfTypeFromModule(
				UserModule.NAME,
				UserDetailsService.class
		);

		UserDirectoryAuthenticationProvider userDirectoryAuthenticationProvider
				= contextBeanRegistry.getBeanOfTypeFromModule( UserModule.NAME,
				                                               UserDirectoryAuthenticationProvider.class );
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setPasswordEncoder( userPasswordEncoder );
		daoAuthenticationProvider.setUserDetailsService( userDetailsService );
		return new ProviderManager( userDirectoryAuthenticationProvider, daoAuthenticationProvider );
	}
*/

}
