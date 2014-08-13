package com.foreach.across.modules.oauth2.config;

import com.foreach.across.modules.oauth2.services.ClientDetailsServiceImpl;
import com.foreach.across.modules.oauth2.services.OAuth2Service;
import com.foreach.across.modules.oauth2.services.OAuth2ServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.ClientDetailsService;

@Configuration
public class OAuth2ServicesConfiguration
{

	@Bean
	public OAuth2Service oAuth2Service() {
		return new OAuth2ServiceImpl();
	}

	@Bean(name = "oAuth2ClientDetailsService")
	public ClientDetailsService clientDetailsService() {
		return new ClientDetailsServiceImpl();
	}
}
