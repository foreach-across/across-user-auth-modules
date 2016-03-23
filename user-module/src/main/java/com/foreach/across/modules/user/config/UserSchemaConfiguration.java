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

import com.foreach.across.core.database.SchemaConfiguration;
import com.foreach.across.core.database.SchemaObject;

import java.util.Arrays;

public class UserSchemaConfiguration extends SchemaConfiguration
{
	public static final String TABLE_PERMISSION = "um_permission";
	public static final String TABLE_PERMISSION_GROUP = "um_permission_group";
	public static final String TABLE_ROLE = "um_role";
	public static final String TABLE_ROLE_PERMISSION = "um_role_permission";
	public static final String TABLE_USER = "um_user";
	public static final String TABLE_USER_DIRECTORY = "um_user_directory";
	public static final String TABLE_USER_DIRECTORY_INTERNAL = "um_user_directory_internal";
	public static final String TABLE_USER_PROPERTIES = "um_user_properties";
	public static final String TABLE_PRINCIPAL = "um_principal";
	public static final String TABLE_PRINCIPAL_ROLE = "um_principal_role";
	public static final String TABLE_GROUP = "um_group";
	public static final String TABLE_GROUP_PROPERTIES = "um_group_properties";
	public static final String TABLE_PRINCIPAL_GROUP = "um_principal_group";
	public static final String TABLE_MACHINE_PRINCIPAL = "um_machine";

	public static final String COLUMN_USER_ID = "user_id";
	public static final String COLUMN_GROUP_ID = "group_id";

	public UserSchemaConfiguration() {
		super( Arrays.asList( new SchemaObject( "table.permission", TABLE_PERMISSION ),
		                      new SchemaObject( "table.permission_group", TABLE_PERMISSION_GROUP ),
		                      new SchemaObject( "table.role", TABLE_ROLE ),
		                      new SchemaObject( "table.role_permission", TABLE_ROLE_PERMISSION ),
		                      new SchemaObject( "table.principal", TABLE_PRINCIPAL ),
		                      new SchemaObject( "table.principal_role", TABLE_PRINCIPAL_ROLE ),
		                      new SchemaObject( "table.group", TABLE_GROUP ),
		                      new SchemaObject( "table.group_properties", TABLE_GROUP_PROPERTIES ),
		                      new SchemaObject( "table.principal_group", TABLE_PRINCIPAL_GROUP ),
		                      new SchemaObject( "table.user", TABLE_USER ),
		                      new SchemaObject( "table.user_directory", TABLE_USER_DIRECTORY ),
		                      new SchemaObject( "table.user_directory_internal", TABLE_USER_DIRECTORY_INTERNAL ),
		                      new SchemaObject( "table.user_properties", TABLE_USER_PROPERTIES ),
		                      new SchemaObject( "table.machine", TABLE_MACHINE_PRINCIPAL ) ) );
	}
}

