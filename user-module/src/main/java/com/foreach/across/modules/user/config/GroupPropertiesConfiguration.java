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
import com.foreach.across.modules.user.repositories.GroupPropertiesRepository;
import com.foreach.across.modules.user.services.GroupPropertiesRegistry;
import com.foreach.across.modules.user.services.GroupPropertiesService;
import com.foreach.across.modules.user.services.GroupPropertiesServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Arne Vandamme
 */
@Configuration
public class GroupPropertiesConfiguration extends AbstractEntityPropertiesConfiguration
{
	public static final String ID = UserModule.NAME + ".GroupProperties";

	@Override
	public String propertiesId() {
		return ID;
	}

	@Override
	protected String originalTableName() {
		return UserSchemaConfiguration.TABLE_GROUP_PROPERTIES;
	}

	@Override
	public String keyColumnName() {
		return UserSchemaConfiguration.COLUMN_GROUP_ID;
	}

	@Bean
	public GroupPropertiesService groupPropertiesService() {
		return new GroupPropertiesServiceImpl( groupPropertiesRegistry(), groupPropertiesRepository() );
	}

	@Bean
	public GroupPropertiesRegistry groupPropertiesRegistry() {
		return new GroupPropertiesRegistry( this );
	}

	@Bean
	public GroupPropertiesRepository groupPropertiesRepository() {
		return new GroupPropertiesRepository( this );
	}
}
