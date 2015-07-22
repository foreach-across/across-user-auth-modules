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
package com.foreach.across.modules.it.properties.extendingmodule.config;

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.it.properties.extendingmodule.registry.ClientPropertyRegistry;
import com.foreach.across.modules.it.properties.extendingmodule.repositories.ClientPropertiesRepository;
import com.foreach.across.modules.it.properties.extendingmodule.services.ClientPropertyService;
import com.foreach.across.modules.properties.config.AbstractEntityPropertiesConfiguration;
import com.foreach.common.spring.properties.support.SingletonPropertyFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Arne Vandamme
 */
@Configuration
public class ClientPropertiesConfig extends AbstractEntityPropertiesConfiguration
{
	public static final String BOOLEAN = "extending.booleanProperty";

	@Override
	public Class<?> entityClass() {
		return Object.class;
	}

	@Override
	protected String originalTableName() {
		return "client_properties";
	}

	@Override
	public String propertiesId() {
		return "ExtendingModule.ClientProperties";
	}

	@Override
	public String keyColumnName() {
		return "client_id";
	}

	@Bean
	@Exposed
	@Override
	public ClientPropertyService service() {
		return new ClientPropertyService( registry(), clientPropertiesRepository() );
	}

	@Bean
	public ClientPropertiesRepository clientPropertiesRepository() {
		return new ClientPropertiesRepository( this );
	}

	@Bean(name = "clientPropertyRegistry")
	@Exposed
	@Override
	public ClientPropertyRegistry registry() {
		ClientPropertyRegistry registry = new ClientPropertyRegistry( this );

		registry.register( currentModule, BOOLEAN, Boolean.class,
		                   SingletonPropertyFactory.<String, Boolean>forValue( true ) );

		return registry;
	}
}
