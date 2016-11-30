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

package com.foreach.across.modules.user.installers;

import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.annotations.InstallerMethod;
import com.foreach.across.core.installers.InstallerPhase;
import com.foreach.across.modules.user.UserModule;
import com.foreach.across.modules.user.business.UserDirectory;
import com.foreach.across.modules.user.config.UserSchemaConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author Marc Vanbrabant
 */
@Installer(description = "AXUM-22 - Updates the principal name from , to @@@", version = 1, phase = InstallerPhase.AfterModuleBootstrap)
public class PrincipalNameUpdater
{
	private static final Pattern DIRECTORY_PREFIXED = Pattern.compile( "^-?\\d+," );

	@Autowired
	private DataSource dataSource;
	@Autowired
	private UserModule currentModule;

	@InstallerMethod
	public void install() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate( dataSource );
		String tableName = currentModule.getSchemaConfiguration().getCurrentTableName(
				UserSchemaConfiguration.TABLE_PRINCIPAL );
		jdbcTemplate.query(
				"SELECT id,principal_name, user_directory_id FROM " + tableName + " WHERE principal_name LIKE '%,%'",
				new Object[] {},
				( rs, rowNum ) -> {
					long id = rs.getLong( 1 );
					String name = rs.getString( 2 );
					long userDirectoryId = rs.getLong( 3 );
					if ( !Objects.equals( userDirectoryId,
					                      UserDirectory.DEFAULT_INTERNAL_DIRECTORY_ID ) && DIRECTORY_PREFIXED.matcher(
							name ).find() ) {
						String principalName = StringUtils.lowerCase(
								DIRECTORY_PREFIXED.matcher( name ).replaceFirst( "" ) );
						principalName = userDirectoryId + "@@@" + principalName;
						jdbcTemplate.update( "UPDATE " + tableName + " set principal_name=? where id=?",
						                     new Object[] { principalName, id } );
					}
					return null;
				} );
	}
}
