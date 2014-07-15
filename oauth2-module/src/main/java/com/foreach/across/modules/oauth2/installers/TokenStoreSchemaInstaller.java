package com.foreach.across.modules.oauth2.installers;

import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.installers.AcrossLiquibaseInstaller;
import com.foreach.across.core.installers.InstallerPhase;

@Installer(
        description = "Creates the database schema for all token stores.",
        phase = InstallerPhase.BeforeContextBootstrap,
        version = 1
)
public class TokenStoreSchemaInstaller extends AcrossLiquibaseInstaller {
}
