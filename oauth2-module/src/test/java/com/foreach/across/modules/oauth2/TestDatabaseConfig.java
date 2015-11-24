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
package com.foreach.across.modules.oauth2;

import com.foreach.across.core.AcrossContext;
import com.foreach.across.database.support.HikariDataSourceHelper;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.hibernate.provider.PackagesToScanProvider;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class TestDatabaseConfig
{
	@Bean
	public AcrossContext acrossContext( ConfigurableApplicationContext applicationContext ) throws Exception {
		AcrossContext acrossContext = new AcrossContext( applicationContext );
		acrossContext.setDataSource( dataSource() );
		acrossContext.addModule( acrossHibernateJpaModule() );

		return acrossContext;
	}

	@Bean
	public AcrossHibernateJpaModule acrossHibernateJpaModule() {
		AcrossHibernateJpaModule acrossHibernateModule = new AcrossHibernateJpaModule();
		acrossHibernateModule.addHibernatePackageProvider(
				new PackagesToScanProvider( "com.foreach.across.modules.oauth2.business",
				                            "com.foreach.across.modules.user.business" ) );

		acrossHibernateModule.setHibernateProperty( AvailableSettings.AUTOCOMMIT, "false" );
		acrossHibernateModule.setHibernateProperty( AvailableSettings.HBM2DDL_AUTO, "create-drop" );

		return acrossHibernateModule;
	}

	@Bean
	public DataSource dataSource() throws Exception {
		return HikariDataSourceHelper.create( "org.hsqldb.jdbc.JDBCDriver", "jdbc:hsqldb:mem:/hsql/user-module", "sa",
		                                      StringUtils.EMPTY );
	}
}
