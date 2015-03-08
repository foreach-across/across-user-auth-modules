package com.foreach.across.modules.bootstrapui.testmodule;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.modules.web.AcrossWebModule;

@AcrossDepends(required = AcrossWebModule.NAME)
public class WebTestModule extends AcrossModule
{
	@Override
	public String getName() {
		return "WebTestModule";
	}

	@Override
	public String getDescription() {
		return null;
	}
}
