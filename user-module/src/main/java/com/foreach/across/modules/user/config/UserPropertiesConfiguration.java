package com.foreach.across.modules.user.config;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.Module;
import com.foreach.across.modules.properties.config.AbstractEntityPropertiesConfiguration;
import com.foreach.across.modules.user.UserModule;
import com.foreach.across.modules.user.repositories.UserPropertiesRepository;
import com.foreach.across.modules.user.services.UserPropertiesRegistry;
import com.foreach.across.modules.user.services.UserPropertiesService;
import com.foreach.across.modules.user.services.UserPropertiesServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author Arne Vandamme
 */
@Configuration
public class UserPropertiesConfiguration extends AbstractEntityPropertiesConfiguration
{
	@Autowired
	private DataSource dataSource;

	@Autowired
	@Module(AcrossModule.CURRENT_MODULE)
	private UserModule userModule;

	@Bean
	public UserPropertiesService userPropertiesService() {
		return new UserPropertiesServiceImpl( userPropertiesRegistry(), userPropertiesRepository() );
	}

	@Bean
	public UserPropertiesRegistry userPropertiesRegistry() {
		return new UserPropertiesRegistry( userPropertiesRepository(), conversionService() );
	}

	@Bean
	public UserPropertiesRepository userPropertiesRepository() {
		String userPropertiesTableName = userModule.getSchemaConfiguration()
		                                           .getCurrentTableName(
				                                           UserSchemaConfiguration.TABLE_USER_PROPERTIES );
		return new UserPropertiesRepository( dataSource, userPropertiesTableName );
	}
}
