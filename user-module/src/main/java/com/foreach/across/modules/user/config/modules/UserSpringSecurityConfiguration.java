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
import com.foreach.across.modules.entity.actions.EntityConfigurationAllowableActionsBuilder;
import com.foreach.across.modules.entity.actions.FixedEntityAllowableActionsBuilder;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
import com.foreach.across.modules.spring.security.actions.AuthorityMatchingAllowableActions;
import com.foreach.across.modules.spring.security.authority.AuthorityMatcher;
import com.foreach.across.modules.spring.security.infrastructure.services.CurrentSecurityPrincipalProxy;
import com.foreach.across.modules.user.UserAuthorities;
import com.foreach.across.modules.user.UserModule;
import com.foreach.across.modules.user.business.Group;
import com.foreach.across.modules.user.business.MachinePrincipal;
import com.foreach.across.modules.user.business.Role;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.security.CurrentUserProxy;
import com.foreach.across.modules.user.security.CurrentUserProxyImpl;
import com.foreach.across.modules.user.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

/**
 * Registers the UserDetailsService implementation for the User module.
 * Configures the security rules for the entities.
 */
@AcrossDepends(required = "SpringSecurityModule")
@Configuration
public class UserSpringSecurityConfiguration implements EntityConfigurer
{
	@Autowired
	private CurrentSecurityPrincipalProxy securityPrincipal;

	@Override
	public void configure( EntitiesConfigurationBuilder configuration ) {
		// Map allowable actions based on the authorities
		configuration.entity( User.class )
		             .allowableActionsBuilder( actionsBuilderForAuthority( UserAuthorities.MANAGE_USERS ) )
		             .and()
		             .entity( Group.class )
		             .allowableActionsBuilder( actionsBuilderForAuthority( UserAuthorities.MANAGE_GROUPS ) )
		             .and()
		             .entity( MachinePrincipal.class )
		             .allowableActionsBuilder( actionsBuilderForAuthority( UserAuthorities.MANAGE_USER_ROLES ) )
		             .and()
		             .entity( Role.class )
		             .allowableActionsBuilder( actionsBuilderForAuthority( UserAuthorities.MANAGE_USER_ROLES ) );
	}

	private EntityConfigurationAllowableActionsBuilder actionsBuilderForAuthority( String authority ) {
		Map<AllowableAction, AuthorityMatcher> actionAuthorityMatcherMap = new HashMap<>();
		actionAuthorityMatcherMap.put( AllowableAction.READ, AuthorityMatcher.allOf( authority ) );
		actionAuthorityMatcherMap.put( AllowableAction.WRITE, AuthorityMatcher.allOf( authority ) );
		actionAuthorityMatcherMap.put( AllowableAction.DELETE, AuthorityMatcher.allOf( authority ) );
		actionAuthorityMatcherMap.put( AllowableAction.CREATE, AuthorityMatcher.allOf( authority ) );

		return new FixedEntityAllowableActionsBuilder(
				AuthorityMatchingAllowableActions.forSecurityPrincipal( securityPrincipal, actionAuthorityMatcherMap )
		);
	}

	@Bean
	public UserDetailsService userDetailsServiceImpl() {
		return new UserDetailsServiceImpl();
	}

	@Bean
	public CurrentUserProxy currentUserProxy() {
		return new CurrentUserProxyImpl();
	}

	/**
	 * Configuration to load inside the SpringSecurityModule ApplicationContext.
	 */
	@AcrossDepends(required = "SpringSecurityModule")
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
