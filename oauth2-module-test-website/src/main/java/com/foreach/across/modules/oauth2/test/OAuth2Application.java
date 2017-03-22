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

package com.foreach.across.modules.oauth2.test;

import com.foreach.across.config.AcrossApplication;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.bootstrapui.BootstrapUiModule;
import com.foreach.across.modules.debugweb.DebugWebModule;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.oauth2.OAuth2Module;
import com.foreach.across.modules.oauth2.business.OAuth2Client;
import com.foreach.across.modules.oauth2.business.OAuth2ClientScope;
import com.foreach.across.modules.oauth2.business.OAuth2Scope;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

/**
 * @author Marc Vanbrabant
 * @since 2.0.0
 */
@Order(Ordered.LOWEST_PRECEDENCE)
@AcrossApplication(modules = { OAuth2Module.NAME, DebugWebModule.NAME, EntityModule.NAME, BootstrapUiModule.NAME,
                               AdminWebModule.NAME })
public class OAuth2Application implements EntityConfigurer
{
	@Bean
	public DataSource acrossDataSource() {
		return new EmbeddedDatabaseBuilder().setType( EmbeddedDatabaseType.H2 ).build();
	}

	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		entities.withType( OAuth2Client.class ).show();
		entities.withType( OAuth2Scope.class ).show();
	}

	public static void main( String args[] ) {
		SpringApplication.run( OAuth2Application.class, args );
	}
}
