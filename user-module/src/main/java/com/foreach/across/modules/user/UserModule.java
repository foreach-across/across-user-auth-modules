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

package com.foreach.across.modules.user;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.database.SchemaConfiguration;
import com.foreach.across.core.installers.AcrossSequencesInstaller;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.hibernate.provider.HibernatePackageConfiguringModule;
import com.foreach.across.modules.hibernate.provider.HibernatePackageRegistry;
import com.foreach.across.modules.hibernate.provider.TableAliasProvider;
import com.foreach.across.modules.properties.PropertiesModule;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.modules.spring.security.infrastructure.SpringSecurityInfrastructureModule;
import com.foreach.across.modules.user.config.UserSchemaConfiguration;
import com.foreach.across.modules.user.installers.*;
import org.apache.commons.lang3.StringUtils;

@AcrossDepends(
		required = { AcrossHibernateJpaModule.NAME, PropertiesModule.NAME, SpringSecurityModule.NAME },
		optional = { "AdminWebModule", SpringSecurityInfrastructureModule.ACL_MODULE, "EntityModule" }
)
public class UserModule extends AcrossModule implements HibernatePackageConfiguringModule
{
	public static final String NAME = "UserModule";

	private final SchemaConfiguration schemaConfiguration = new UserSchemaConfiguration();

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return "Provides services and structure for user management with authorization functionality.";
	}

	@Override
	public Object[] getInstallers() {
		return new Object[] {
				AcrossSequencesInstaller.class,
				GroupPropertiesSchemaInstaller.class,
				UserPropertiesSchemaInstaller.class,
				UserSchemaInstaller.class,
				AuditableTablesInstaller.class,
//				new GroupPropertiesSchemaInstaller( schemaConfiguration ),
//				new UserPropertiesSchemaInstaller( schemaConfiguration ),
//				new UserSchemaInstaller( schemaConfiguration ),
//				new AuditableTablesInstaller( schemaConfiguration ),
				DefaultUserInstaller.class,
				AclPermissionsInstaller.class
		};
	}

	@Override
	public void configureHibernatePackage( HibernatePackageRegistry hibernatePackage ) {
		if ( StringUtils.equals( AcrossHibernateJpaModule.NAME, hibernatePackage.getName() ) ) {
			hibernatePackage.addPackageToScan( "com.foreach.across.modules.user.business" );
			hibernatePackage.add( new TableAliasProvider( schemaConfiguration.getTables() ) );
		}
	}

}
