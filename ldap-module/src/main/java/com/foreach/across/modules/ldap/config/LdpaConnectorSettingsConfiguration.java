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

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.modules.ldap.business.LdapConnector;
import com.foreach.across.modules.ldap.infrastructure.aop.LdapConnectorEntityInterceptor;
import com.foreach.across.modules.ldap.services.properties.LdapConnectorSettingsRegistry;
import com.foreach.across.modules.ldap.services.properties.LdapConnectorSettingsRepository;
import com.foreach.across.modules.ldap.services.properties.LdapConnectorSettingsService;
import com.foreach.across.modules.ldap.services.properties.LdapConnectorSettingsServiceImpl;
import com.foreach.across.modules.properties.config.AbstractEntityPropertiesConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AcrossDepends(required = "PropertiesModule")
public class LdpaConnectorSettingsConfiguration extends AbstractEntityPropertiesConfiguration
{
	@Override
	public Class<?> entityClass() {
		return LdapConnector.class;
	}

	@Override
	public String propertiesId() {
		return "LdapModule.LdapConnectorSettings";
	}

	@Override
	protected String originalTableName() {
		return "ldap_connector_properties";
	}

	@Override
	public String keyColumnName() {
		return "ldap_connector_id";
	}

	@Bean(name = "ldapSettingsService")
	@Override
	public LdapConnectorSettingsService service() {
		return new LdapConnectorSettingsServiceImpl( registry(), ldapConnectorSettingsRepository() );
	}

	@Bean
	public LdapConnectorSettingsRepository ldapConnectorSettingsRepository() {
		return new LdapConnectorSettingsRepository( this );
	}

	@Bean(name = "ldapConnectorSettingsRegistry")
	@Override
	public LdapConnectorSettingsRegistry registry() {
		return new LdapConnectorSettingsRegistry( this );
	}

	@Bean
	public LdapConnectorEntityInterceptor ldapConnectorEntityInterceptor() {
		return new LdapConnectorEntityInterceptor();
	}
}
