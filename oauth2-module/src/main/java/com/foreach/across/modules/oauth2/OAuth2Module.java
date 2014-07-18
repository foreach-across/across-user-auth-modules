package com.foreach.across.modules.oauth2;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.context.bootstrap.AcrossBootstrapConfig;
import com.foreach.across.core.context.bootstrap.ModuleBootstrapConfig;
import com.foreach.across.core.context.configurer.AnnotatedClassConfigurer;
import com.foreach.across.core.context.configurer.ApplicationContextConfigurer;
import com.foreach.across.core.database.HasSchemaConfiguration;
import com.foreach.across.core.database.SchemaConfiguration;
import com.foreach.across.core.installers.AcrossSequencesInstaller;
import com.foreach.across.modules.hibernate.AcrossHibernateModule;
import com.foreach.across.modules.hibernate.provider.*;
import com.foreach.across.modules.oauth2.config.OAuth2ControllersConfiguration;
import com.foreach.across.modules.oauth2.config.OAuth2RepositoriesConfiguration;
import com.foreach.across.modules.oauth2.config.OAuth2SchemaConfiguration;
import com.foreach.across.modules.oauth2.config.OAuth2ServicesConfiguration;
import com.foreach.across.modules.oauth2.config.security.AuthorizationServerSecurityConfiguration;
import com.foreach.across.modules.oauth2.config.security.ResourceServerSecurityConfiguration;
import com.foreach.across.modules.oauth2.installers.OAuth2SchemaInstaller;
import com.foreach.across.modules.oauth2.installers.TokenStoreSchemaInstaller;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.modules.user.UserModule;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

@AcrossDepends(required = { UserModule.NAME, SpringSecurityModule.NAME })
public class OAuth2Module extends AcrossModule implements HasHibernatePackageProvider, HasSchemaConfiguration
{

	public static final String NAME = "Oauth2Module";

	private final SchemaConfiguration schemaConfiguration = new OAuth2SchemaConfiguration();

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return "Provides Oauth 2 security";
	}

	@Override
	public Object[] getInstallers() {
		return new Object[] {
				new AcrossSequencesInstaller(),
				new OAuth2SchemaInstaller( schemaConfiguration ),
				new TokenStoreSchemaInstaller()
		};
	}

	@Override
	protected void registerDefaultApplicationContextConfigurers( Set<ApplicationContextConfigurer> contextConfigurers ) {
		contextConfigurers.add(
				new AnnotatedClassConfigurer(
						OAuth2RepositoriesConfiguration.class,
						OAuth2ServicesConfiguration.class,
						OAuth2ControllersConfiguration.class,
						AuthorizationServerSecurityConfiguration.class,
						ResourceServerSecurityConfiguration.class
				)
		);
	}

	/**
	 * Returns the package provider associated with this implementation.
	 *
	 * @param hibernateModule AcrossHibernateModule that is requesting packages.
	 * @return HibernatePackageProvider instance.
	 */
	public HibernatePackageProvider getHibernatePackageProvider( AcrossHibernateModule hibernateModule ) {
		if ( StringUtils.equals( "AcrossHibernateModule", hibernateModule.getName() ) ) {
			return new HibernatePackageProviderComposite(
					new PackagesToScanProvider( "com.foreach.across.modules.oauth2.business" ),
					new TableAliasProvider( schemaConfiguration.getTables() ) );
		}

		return null;
	}

	@Override
	public SchemaConfiguration getSchemaConfiguration() {
		return schemaConfiguration;
	}

	@Override
	public void prepareForBootstrap( ModuleBootstrapConfig currentModule,
	                                 AcrossBootstrapConfig contextConfig ) {

		/*contextConfig.extendModule( "SpringSecurityModule",
		                            new AnnotatedClassConfigurer(
				                            AltResourceServerConfiguration.class,
				                            AuthorizationServerConfiguration.class
		                            )
		);

		contextConfig.extendModule( "SpringSecurityModule",
		                            new ClassBeanFilter( FrameworkEndpointHandlerMapping.class )
		);*/
	}
}
