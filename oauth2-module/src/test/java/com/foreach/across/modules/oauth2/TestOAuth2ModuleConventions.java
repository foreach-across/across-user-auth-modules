package com.foreach.across.modules.oauth2;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.test.AbstractAcrossModuleConventionsTest;

/**
 * @author Arne Vandamme
 */
public class TestOAuth2ModuleConventions extends AbstractAcrossModuleConventionsTest
{
	@Override
	protected boolean hasSettings() {
		return false;
	}

	@Override
	protected AcrossModule createModule() {
		return new OAuth2Module();
	}
}
