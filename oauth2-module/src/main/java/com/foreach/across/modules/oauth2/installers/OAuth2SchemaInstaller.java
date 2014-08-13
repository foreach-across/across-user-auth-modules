package com.foreach.across.modules.oauth2.installers;

import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.database.SchemaConfiguration;
import com.foreach.across.core.installers.AcrossLiquibaseInstaller;

@Installer(description = "Installs database schema for OAuth2 authorization.", version = 2)
public class OAuth2SchemaInstaller extends AcrossLiquibaseInstaller
{
	public OAuth2SchemaInstaller( SchemaConfiguration schemaConfiguration ) {
		super( schemaConfiguration );
	}
}
