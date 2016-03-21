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

import com.foreach.across.modules.ldap.business.ActiveDirectorySettings;
import com.foreach.across.modules.ldap.business.LdapConnectorType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Marc Vanbrabant
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties
public class ActiveDirectorySettingsConfiguration
{
	@Bean
	public YamlPropertySourceLoader yamlPropertySourceLoader() {
		return new YamlPropertySourceLoader();
	}

	@Bean
	@ConfigurationProperties(prefix = "msad", locations = "classpath:activedirectorysettings/connectorsettings.yaml")
	public ActiveDirectorySettings microsoftActiveDirectorySettings() {
		return new ActiveDirectorySettings()
		{
			@Override
			public LdapConnectorType getConnectorType() {
				return LdapConnectorType.MICROSOFT_ACTIVE_DIRECTORY;
			}
		};
	}

	@Bean
	@ConfigurationProperties(prefix = "opends", locations = "classpath:activedirectorysettings/connectorsettings.yaml")
	public ActiveDirectorySettings openDsSettings() {
		return new ActiveDirectorySettings()
		{
			@Override
			public LdapConnectorType getConnectorType() {
				return LdapConnectorType.OPENDS;
			}
		};
	}

	@Bean
	@ConfigurationProperties(prefix = "apache15", locations = "classpath:activedirectorysettings/connectorsettings.yaml")
	public ActiveDirectorySettings apacheDirectoryService15() {
		return new ActiveDirectorySettings()
		{
			@Override
			public LdapConnectorType getConnectorType() {
				return LdapConnectorType.APACHEDS_1_5;
			}
		};
	}
}
