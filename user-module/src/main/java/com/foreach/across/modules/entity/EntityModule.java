package com.foreach.across.modules.entity;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.context.configurer.ApplicationContextConfigurer;
import com.foreach.across.core.context.configurer.ComponentScanConfigurer;

import java.util.Set;

@AcrossDepends(required = { "AdminWebModule", "AcrossHibernateModule" })
public class EntityModule extends AcrossModule
{
	public static final String NAME = "EntityModule";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return "Provide entity management functionality.";
	}

	@Override
	protected void registerDefaultApplicationContextConfigurers( Set<ApplicationContextConfigurer> contextConfigurers ) {
		contextConfigurers.add( new ComponentScanConfigurer( getClass().getPackage().getName() + ".config" ) );
	}
}
