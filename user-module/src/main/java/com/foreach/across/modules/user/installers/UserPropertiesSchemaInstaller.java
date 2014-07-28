package com.foreach.across.modules.user.installers;

import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.database.SchemaConfiguration;
import com.foreach.across.modules.properties.installers.EntityPropertiesInstaller;
import com.foreach.across.modules.user.config.UserSchemaConfiguration;

/**
 * @author Arne Vandamme
 */
@Installer(description = "Installs the user properties table", version = 1)
public class UserPropertiesSchemaInstaller extends EntityPropertiesInstaller
{
	private final SchemaConfiguration schemaConfiguration;

	public UserPropertiesSchemaInstaller( SchemaConfiguration schemaConfiguration ) {
		this.schemaConfiguration = schemaConfiguration;
	}

	@Override
	protected String getTableName() {
		return schemaConfiguration.getCurrentTableName( UserSchemaConfiguration.TABLE_USER_PROPERTIES );
	}

	@Override
	protected String getKeyColumnName() {
		return UserSchemaConfiguration.COLUMN_USER_ID;
	}
}
