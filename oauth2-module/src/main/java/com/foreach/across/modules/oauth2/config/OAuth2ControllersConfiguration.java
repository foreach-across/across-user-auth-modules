package com.foreach.across.modules.oauth2.config;

import com.foreach.across.modules.oauth2.controllers.OAuth2TokenController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OAuth2ControllersConfiguration
{
	@Bean
	public OAuth2TokenController oAuth2TokenController() {
		return new OAuth2TokenController();
	}
}
