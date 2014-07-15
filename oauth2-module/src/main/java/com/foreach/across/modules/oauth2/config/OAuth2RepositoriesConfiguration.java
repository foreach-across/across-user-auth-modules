package com.foreach.across.modules.oauth2.config;

import com.foreach.across.modules.oauth2.repositories.OAuth2Repository;
import com.foreach.across.modules.oauth2.repositories.OAuth2RepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OAuth2RepositoriesConfiguration {
    @Bean
    public OAuth2Repository oAuth2Repository() {
        return new OAuth2RepositoryImpl();
    }
}
