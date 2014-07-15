package com.foreach.across.modules.oauth2.controllers;

import com.foreach.across.core.annotations.Refreshable;
import com.foreach.across.modules.oauth2.dto.OAuth2TokenDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@Refreshable
public class OAuth2TokenController {

    @Autowired(required = false)
    private TokenStore tokenStore;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired( required = false )
    private AuthorizationServerTokenServices authorizationServerTokenServices;

    @RequestMapping( "/oauth/invalidate" )
    public ResponseEntity<OAuth2TokenDto> invalidateToken( @RequestParam(value = "access_token") String accessToken ) {
        OAuth2TokenDto response = new OAuth2TokenDto( accessToken );
        if ( StringUtils.isNotEmpty( accessToken ) ) {
            OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken( accessToken );
            if ( oAuth2AccessToken != null ) {
                OAuth2RefreshToken refreshToken = oAuth2AccessToken.getRefreshToken();
                tokenStore.removeAccessToken( oAuth2AccessToken );
                tokenStore.removeRefreshToken( refreshToken );
            } else {
                OAuth2RefreshToken oAuth2RefreshToken = tokenStore.readRefreshToken( accessToken );
                if ( oAuth2RefreshToken != null ) {
                    tokenStore.removeAccessTokenUsingRefreshToken( oAuth2RefreshToken );
                    tokenStore.removeRefreshToken( oAuth2RefreshToken );
                }
            }
        }
        SecurityContextHolder.clearContext();
        return new ResponseEntity<OAuth2TokenDto>( response, HttpStatus.OK );
    }

    @RequestMapping( "/oauth/user_token" )
    public ResponseEntity<Map<String, String>> createUserToken(
            @AuthenticationPrincipal OAuth2Authentication authentication,
            @RequestParam( value = "username" ) String username
    ) {
        UserDetails userDetails = userDetailsService.loadUserByUsername( username );

        // Only if user is enabled can a token be created
        String clientId = authentication.getOAuth2Request().getClientId();

        Map<String, String> response = new HashMap<>();
        //authentication.getOAuth2Request().getRequestParameters().get("ac")

        OAuth2Request request = new OAuth2Request( Collections.<String, String>emptyMap(),
                clientId, Collections.<GrantedAuthority>emptyList(), true,
                Collections.singleton( "full" ),
                Collections.singleton( "knooppunt" ), "",
                Collections.<String>emptySet(), Collections.<String, Serializable>emptyMap()
        );

        Authentication userAuthentication = new PreAuthenticatedAuthenticationToken( userDetails, null, userDetails.getAuthorities() );
        OAuth2Authentication newAuthentication = new OAuth2Authentication( request, userAuthentication );

        OAuth2AccessToken token = authorizationServerTokenServices.createAccessToken( newAuthentication );
        response.put( "access_token", token.getValue() );

        return new ResponseEntity<>( response, HttpStatus.OK );
    }
}
