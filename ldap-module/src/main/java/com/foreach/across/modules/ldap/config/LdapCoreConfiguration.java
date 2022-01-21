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

package com.foreach.across.modules.ldap.config;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.ConditionalOnAcrossModule;
import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.hibernate.jpa.repositories.config.EnableAcrossJpaRepositories;
import com.foreach.across.modules.ldap.LdapModuleSettings;
import com.foreach.across.modules.ldap.controllers.AjaxTestLdapConnectorController;
import com.foreach.across.modules.ldap.repositories.LdapConnectorRepository;
import com.foreach.across.modules.ldap.repositories.LdapUserDirectoryRepository;
import com.foreach.across.modules.ldap.services.*;
import com.foreach.across.modules.ldap.services.properties.LdapConnectorSettingsService;
import com.foreach.across.modules.ldap.tasks.LdapSynchronizationTask;
import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalService;
import com.foreach.across.modules.user.services.*;
import com.foreach.common.concurrent.locks.distributed.DistributedLockRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.TypeDescriptor;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Marc Vanbrabant
 * @since 1.0.0
 */
@Configuration
@Slf4j
@EnableAcrossJpaRepositories(basePackageClasses = LdapConnectorRepository.class)
public class LdapCoreConfiguration
{
	@Bean
	@ConditionalOnAcrossModule({ "UserModule", "PropertiesModule" })
	@Exposed
	@ConditionalOnProperty(value = "disable-synchronization-task", prefix = "ldap-module", havingValue = "false", matchIfMissing = true)
	public LdapSynchronizationTask ldapSynchronizationTask( LdapUserDirectoryRepository ldapUserDirectoryRepository,
	                                                        DistributedLockRepository lockRepository,
	                                                        LdapSynchronizationService ldapSynchronizationService ) throws UnknownHostException {
		LOG.info( "Creating LdapSynchronizationTask bean" );
		return new LdapSynchronizationTask( ldapSynchronizationService, ldapUserDirectoryRepository,
		                                    lockRepository, InetAddress.getLocalHost().getHostName() );
	}

	@Bean
	@ConditionalOnAcrossModule({ "UserModule", "PropertiesModule" })
	public LdapPropertiesService ldapPropertiesService( AcrossModule currentModule,
	                                                    UserPropertiesRegistry userPropertiesRegistry,
	                                                    GroupPropertiesRegistry groupPropertiesRegistry ) {
		userPropertiesRegistry.register( currentModule, LdapPropertyConstants.OBJECT_GUID, TypeDescriptor.valueOf(
				String.class ) );
		userPropertiesRegistry.register( currentModule, LdapPropertyConstants.DISTINGUISHED_NAME,
		                                 TypeDescriptor.valueOf( String.class ) );
		groupPropertiesRegistry.register( currentModule, LdapPropertyConstants.OBJECT_GUID, TypeDescriptor.valueOf(
				String.class ) );
		groupPropertiesRegistry.register( currentModule, LdapPropertyConstants.DISTINGUISHED_NAME,
		                                  TypeDescriptor.valueOf( String.class ) );
		return new LdapPropertiesServiceImpl();
	}

	@Bean
	@ConditionalOnAcrossModule({ "UserModule", "PropertiesModule" })
	public LdapSynchronizationService ldapSynchronizationService( UserService userService,
	                                                              GroupService groupService,
	                                                              LdapConnectorSettingsService ldapConnectorSettingsService,
	                                                              SecurityPrincipalService securityPrincipalService,
	                                                              ApplicationEventPublisher eventPublisher,
	                                                              LdapPropertiesService ldapPropertiesService,
	                                                              LdapModuleSettings ldapModuleSettings

	) {
		return new LdapSynchronizationServiceImpl( userService, groupService, ldapConnectorSettingsService,
		                                           ldapSearchService(), securityPrincipalService, eventPublisher,
		                                           ldapPropertiesService, ldapModuleSettings );
	}

	@Bean
	@ConditionalOnAcrossModule({ "UserModule", "PropertiesModule" })
	public UserDirectoryServiceProvider ldapUserDirectoryServiceProvider() {
		return new LdapUserDirectoryServiceProvider();
	}

	@Bean
	public LdapSearchService ldapSearchService() {
		return new LdapSearchServiceImpl();
	}

	@Bean
	@ConditionalOnAcrossModule("EntityModule")
	public AjaxTestLdapConnectorController ajaxTestLdapConnectorController() {
		return new AjaxTestLdapConnectorController();
	}
}
