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
package com.foreach.across.modules.user.config;

import com.foreach.across.core.context.support.AcrossModuleMessageSource;
import com.foreach.across.modules.hibernate.jpa.repositories.config.EnableAcrossJpaRepositories;
import com.foreach.across.modules.user.UserModule;
import com.foreach.across.modules.user.UserModuleSettings;
import com.foreach.across.modules.user.repositories.MachinePrincipalRepository;
import com.foreach.across.modules.user.services.*;
import com.foreach.across.modules.user.validators.GroupValidator;
import com.foreach.across.modules.user.validators.MachinePrincipalValidator;
import org.hibernate.validator.internal.constraintvalidators.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableAcrossJpaRepositories(basePackageClasses = UserModule.class)
public class UserModuleConfiguration
{
	private static final Logger LOG = LoggerFactory.getLogger( UserModuleConfiguration.class );

	@Autowired
	private UserModuleSettings settings;

	@Bean
	public MessageSource messageSource() {
		return new AcrossModuleMessageSource();
	}

	@Bean
	public PermissionService permissionService() {
		return new PermissionServiceImpl();
	}

	@Bean
	public RoleService roleService() {
		return new RoleServiceImpl();
	}

	@Bean
	public GroupValidator groupValidator() {
		return new GroupValidator( groupService() );
	}

	@Bean
	public GroupService groupService() {
		return new GroupServiceImpl();
	}

	@Bean
	public MachinePrincipalValidator machinePrincipalValidator( MachinePrincipalRepository machinePrincipalRepository ) {
		return new MachinePrincipalValidator( machinePrincipalRepository );
	}

	@Bean
	public MachinePrincipalService machinePrincipalService() {
		return new MachinePrincipalServiceImpl();
	}

	@Bean(name = "userPasswordEncoder")
	public PasswordEncoder userPasswordEncoder() {
		PasswordEncoder encoder = settings.getProperty( UserModuleSettings.PASSWORD_ENCODER, PasswordEncoder.class );

		if ( encoder != null ) {
			LOG.debug( "Using the PasswordEncoder instance configured on the UserModule: {}", encoder );
			return encoder;
		}

		LOG.debug( "No PasswordEncoder instance configured on the module - defaulting to BCrypt" );

		return new BCryptPasswordEncoder();
	}

	@Bean
	public UserValidator userValidator() {
		return new UserValidator();
	}

	@Bean
	public EmailValidator emailValidator() {
		return new EmailValidator();
	}

	@Bean
	public UserService userService() {
		return new UserServiceImpl( userPasswordEncoder(),
		                            settings.isUseEmailAsUsername(),
		                            settings.isRequireUniqueEmail() );
	}
}
