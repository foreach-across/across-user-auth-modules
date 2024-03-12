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

import com.foreach.across.core.annotations.ConditionalOnAcrossModule;
import com.foreach.across.core.registry.IncrementalRefreshableRegistry;
import com.foreach.across.core.registry.RefreshableRegistry;
import com.foreach.across.modules.entity.actions.EntityConfigurationAllowableActionsBuilder;
import com.foreach.across.modules.entity.actions.FixedEntityAllowableActionsBuilder;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
import com.foreach.across.modules.spring.security.actions.AuthorityMatchingAllowableActions;
import com.foreach.across.modules.spring.security.authority.AuthorityMatcher;
import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalService;
import com.foreach.across.modules.user.UserAuthorities;
import com.foreach.across.modules.user.UserModuleSettings;
import com.foreach.across.modules.user.business.*;
import com.foreach.across.modules.user.security.CurrentUserProxy;
import com.foreach.across.modules.user.security.CurrentUserProxyImpl;
import com.foreach.across.modules.user.security.UserDetailsServiceImpl;
import com.foreach.across.modules.user.security.UserDirectoryAuthenticationProvider;
import com.foreach.across.modules.user.services.InternalUserDirectoryServiceProvider;
import com.foreach.across.modules.user.services.UserDirectoryService;
import com.foreach.across.modules.user.services.UserDirectoryServiceProvider;
import com.foreach.across.modules.user.services.UserDirectoryServiceProviderManager;
import com.foreach.across.modules.user.services.support.ExpressionBasedSecurityPrincipalLabelResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.HashMap;
import java.util.Map;

/**
 * Registers the UserDetailsService implementation for the User module.
 * Configures the security rules for the entities.
 */
@ConditionalOnAcrossModule("SpringSecurityModule")
@Configuration
public class UserSpringSecurityConfiguration implements EntityConfigurer
{
	@Autowired
	private UserModuleSettings userModuleSettings;

	@Override
	public void configure( EntitiesConfigurationBuilder configuration ) {
		// Map allowable actions based on the authorities
		configuration.withType( User.class )
		             .allowableActionsBuilder( actionsBuilderForAuthority( UserAuthorities.MANAGE_USERS ) );
		configuration.withType( Group.class )
		             .allowableActionsBuilder( actionsBuilderForAuthority( UserAuthorities.MANAGE_GROUPS ) );
		configuration.withType( MachinePrincipal.class )
		             .allowableActionsBuilder( actionsBuilderForAuthority( UserAuthorities.MANAGE_USER_ROLES ) );
		configuration.withType( Role.class )
		             .allowableActionsBuilder( actionsBuilderForAuthority( UserAuthorities.MANAGE_USER_ROLES ) );
		configuration.withType( Permission.class )
		             .allowableActionsBuilder( actionsBuilderForAuthority( UserAuthorities.MANAGE_USER_ROLES ) );
		configuration.withType( PermissionGroup.class )
		             .allowableActionsBuilder( actionsBuilderForAuthority( UserAuthorities.MANAGE_USER_ROLES ) );
	}

	private EntityConfigurationAllowableActionsBuilder actionsBuilderForAuthority( String authority ) {
		Map<AllowableAction, AuthorityMatcher> actionAuthorityMatcherMap = new HashMap<>();
		actionAuthorityMatcherMap.put( AllowableAction.READ, AuthorityMatcher.allOf( authority ) );
		actionAuthorityMatcherMap.put( AllowableAction.UPDATE, AuthorityMatcher.allOf( authority ) );
		actionAuthorityMatcherMap.put( AllowableAction.DELETE, AuthorityMatcher.allOf( authority ) );
		actionAuthorityMatcherMap.put( AllowableAction.CREATE, AuthorityMatcher.allOf( authority ) );

		return new FixedEntityAllowableActionsBuilder(
				AuthorityMatchingAllowableActions.forSecurityContext( actionAuthorityMatcherMap )
		);
	}

	@Bean
	public UserDirectoryServiceProviderManager userDirectoryServiceProviderManager() {
		return new UserDirectoryServiceProviderManager( userDirectoryServiceProviders() );
	}

	@Bean
	public RefreshableRegistry<UserDirectoryServiceProvider> userDirectoryServiceProviders() {
		return new IncrementalRefreshableRegistry<>( UserDirectoryServiceProvider.class, true );
	}

	@Bean
	public UserDirectoryAuthenticationProvider userDirectoryAuthenticationProvider(
			UserDirectoryService userDirectoryService,
			UserDirectoryServiceProviderManager userDirectoryServiceProviderManager ) {
		return new UserDirectoryAuthenticationProvider( userDirectoryService, userDirectoryServiceProviderManager );
	}

	@Bean
	public InternalUserDirectoryServiceProvider internalUserDirectoryServiceProvider() {
		return new InternalUserDirectoryServiceProvider();
	}

	@Bean
	public UserDetailsService userDetailsService( SecurityPrincipalService securityPrincipalService,
	                                              UserDirectoryService userDirectoryService ) {
		return new UserDetailsServiceImpl( securityPrincipalService, userDirectoryService );
	}

	@Bean
	public CurrentUserProxy currentUserProxy() {
		return new CurrentUserProxyImpl();
	}

	@Bean
	public ExpressionBasedSecurityPrincipalLabelResolver userLabelResolver() {
		return new ExpressionBasedSecurityPrincipalLabelResolver(
				User.class, userModuleSettings.getUserLabelExpression()
		);
	}

	@Bean
	public ExpressionBasedSecurityPrincipalLabelResolver groupLabelResolver() {
		return new ExpressionBasedSecurityPrincipalLabelResolver( Group.class, "name" );
	}

	@Bean
	public ExpressionBasedSecurityPrincipalLabelResolver machinePrincipalLabelResolver() {
		return new ExpressionBasedSecurityPrincipalLabelResolver( MachinePrincipal.class, "name" );
	}
}
