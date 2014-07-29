package com.foreach.across.modules.user.config;

import com.foreach.across.modules.properties.config.AbstractEntityPropertiesConfiguration;
import com.foreach.across.modules.user.UserModule;
import com.foreach.across.modules.user.repositories.UserPropertiesRepository;
import com.foreach.across.modules.user.services.UserPropertiesRegistry;
import com.foreach.across.modules.user.services.UserPropertiesService;
import com.foreach.across.modules.user.services.UserPropertiesServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Arne Vandamme
 */
@Configuration
public class UserPropertiesConfiguration extends AbstractEntityPropertiesConfiguration
{
	public static final String ID = UserModule.NAME + ".UserProperties";

	@Override
	public String propertiesId() {
		return ID;
	}

	@Override
	protected String originalTableName() {
		return UserSchemaConfiguration.TABLE_USER_PROPERTIES;
	}

	@Override
	public String keyColumnName() {
		return UserSchemaConfiguration.COLUMN_USER_ID;
	}

	@Bean
	public UserPropertiesService userPropertiesService() {
		return new UserPropertiesServiceImpl( userPropertiesRegistry(), userPropertiesRepository() );
	}

	@Bean
	public UserPropertiesRegistry userPropertiesRegistry() {
		return new UserPropertiesRegistry( this );
	}

	@Bean
	public UserPropertiesRepository userPropertiesRepository() {
		return new UserPropertiesRepository( this );
	}
}
