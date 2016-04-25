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

package com.foreach.across.modules.ldap.test;

import com.foreach.across.config.AcrossApplication;
import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.filters.BeanFilter;
import com.foreach.across.core.filters.BeanFilterComposite;
import com.foreach.across.core.filters.ClassBeanFilter;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.bootstrapui.BootstrapUiModule;
import com.foreach.across.modules.debugweb.DebugWebModule;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.controllers.ViewRequestValidator;
import com.foreach.across.modules.ldap.LdapModule;
import com.foreach.across.modules.user.UserModule;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import javax.sql.DataSource;

/**
 * @author Marc Vanbrabant
 * @version 1.0.0
 */
@AcrossApplication(modules = { LdapModule.NAME, DebugWebModule.NAME })
public class LdapModuleApplication
{
	@Bean
	public DataSource acrossDataSource() {
		return new EmbeddedDatabaseBuilder().build();
	}

	public static void main( String[] args ) {
		SpringApplication.run( LdapModuleApplication.class, args );
	}

	@Profile("entitymodule")
	@Configuration
	public static class EntityModuleProfile
	{
		@Bean
		public AcrossModule entityModule() {
			EntityModule entityModule = new EntityModule();
			BeanFilter exposeViewRequestValidator = new ClassBeanFilter( ViewRequestValidator.class );
			entityModule.setExposeFilter(
					new BeanFilterComposite( entityModule.getExposeFilter(), exposeViewRequestValidator ) );
			return entityModule;
		}

		@Bean
		public AcrossModule bootstrapUiModule() {
			return new BootstrapUiModule();
		}
	}

	@Profile("adminwebmodule")
	@Configuration
	public static class AdminWebModuleProfile
	{
		@Bean
		public AcrossModule adminWebModule() {
			return new AdminWebModule();
		}
	}

	@Profile("usermodule")
	@Configuration
	public static class UserModuleProfile
	{
		@Bean
		public AcrossModule userModule() {
			return new UserModule();
		}
	}
}
