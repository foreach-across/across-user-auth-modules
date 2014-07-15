package com.foreach.across.modules.oauth2.services;

import com.foreach.across.modules.oauth2.business.OAuth2Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.stereotype.Service;

@Service
public class ClientDetailsServiceImpl implements ClientDetailsService {

    @Autowired
    private OAuth2Service oAuth2Service;

    @Override
    public ClientDetails loadClientByClientId( String clientId ) throws ClientRegistrationException {
        OAuth2Client clientById = oAuth2Service.getClientById( clientId );
        if ( clientById == null ) {
            throw new NoSuchClientException( "No client found with clientId: " + clientId );
        }
        return clientById;
    }
}
