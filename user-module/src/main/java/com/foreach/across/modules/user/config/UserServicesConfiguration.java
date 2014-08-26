package com.foreach.across.modules.user.config;

import com.foreach.across.modules.user.UserModuleSettings;
import com.foreach.across.modules.user.converters.ObjectToPermissionConverter;
import com.foreach.across.modules.user.converters.ObjectToRoleConverter;
import com.foreach.across.modules.user.converters.ObjectToUserConverter;
import com.foreach.across.modules.user.services.*;
import com.foreach.across.modules.user.services.security.SecurityPrincipalService;
import com.foreach.across.modules.user.services.security.SecurityPrincipalServiceImpl;
import org.hibernate.validator.internal.constraintvalidators.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;

@Configuration
public class UserServicesConfiguration
{
	private static final Logger LOG = LoggerFactory.getLogger( UserServicesConfiguration.class );

	@Autowired(required = false)
	private ConfigurableConversionService conversionService;

	@Autowired
	private Environment environment;

	@PostConstruct
	void registerConverters() {
		if ( conversionService != null ) {
			LOG.debug( "ConfigurableConversionService found - auto-registering user converters " );

			conversionService.addConverter( new ObjectToRoleConverter( conversionService, roleService() ) );
			conversionService.addConverter( new ObjectToPermissionConverter( conversionService, permissionService() ) );
			conversionService.addConverter( new ObjectToUserConverter( conversionService, userService() ) );
		}
		else {
			LOG.debug( "No ConfigurableConversionService found - unable to auto-register user converters" );
		}
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
	public GroupService groupService() {
		return new GroupServiceImpl();
	}

	@Bean(name = "userPasswordEncoder")
	public PasswordEncoder userPasswordEncoder() {
		PasswordEncoder encoder = environment.getProperty( UserModuleSettings.PASSWORD_ENCODER, PasswordEncoder.class );

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
		boolean useEmailAsUsername = environment.getProperty( UserModuleSettings.USE_EMAIL_AS_USERNAME, Boolean.class,
		                                                      false );
		boolean requireEmailUnique = environment.getProperty( UserModuleSettings.REQUIRE_EMAIL_UNIQUE, Boolean.class,
		                                                      false );
		return new UserServiceImpl( userPasswordEncoder(), useEmailAsUsername, requireEmailUnique );
	}

	@Bean
	public SecurityPrincipalService securityPrincipalService() {
		return new SecurityPrincipalServiceImpl();
	}
}
