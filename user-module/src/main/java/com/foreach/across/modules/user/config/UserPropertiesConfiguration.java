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
