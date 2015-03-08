package com.foreach.across.modules.bootstrapui.testmodule.config;

import com.foreach.across.modules.bootstrapui.testmodule.controllers.RenderComponentController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebTestControllersConfiguration
{
	@Bean
	public RenderComponentController renderComponentController() {
		return new RenderComponentController();
	}
}
