package com.foreach.across.modules.oauth2.config;

import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.annotations.Exposed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.DefaultUserApprovalHandler;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

import javax.sql.DataSource;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    @Qualifier(AcrossContext.DATASOURCE)
    private DataSource dataSource;

    @Autowired
    @Qualifier("oAuth2ClientDetailsService")
    private ClientDetailsService clientDetailsService;

    @Bean
    @Exposed
    public TokenStore tokenStore() {
//        return new JdbcTokenStore( dataSource );
        return new InMemoryTokenStore();
    }

    @Override
    public void configure( ClientDetailsServiceConfigurer clients ) throws Exception {
        clients.withClientDetails( clientDetailsService );
    }

    @Override
    public void configure( AuthorizationServerEndpointsConfigurer endpoints ) throws Exception {
        endpoints.tokenStore( tokenStore() ).userApprovalHandler(
                new DefaultUserApprovalHandler() ).authenticationManager( authenticationManager );
    }

    @Override
    public void configure( AuthorizationServerSecurityConfigurer oauthServer ) throws Exception {
        oauthServer.allowFormAuthenticationForClients();
    }
}
