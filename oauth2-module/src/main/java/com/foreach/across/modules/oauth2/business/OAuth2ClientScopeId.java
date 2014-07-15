package com.foreach.across.modules.oauth2.business;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
public class OAuth2ClientScopeId implements Serializable{
    @ManyToOne
    private OAuth2Client oAuth2Client;

    @ManyToOne
    private OAuth2Scope oAuth2Scope;

    public OAuth2Client getOAuth2Client() {
        return oAuth2Client;
    }

    public void setOAuth2Client( OAuth2Client oAuth2Client ) {
        this.oAuth2Client = oAuth2Client;
    }

    public OAuth2Scope getOAuth2Scope() {
        return oAuth2Scope;
    }

    public void setOAuth2Scope( OAuth2Scope oAuth2Scope ) {
        this.oAuth2Scope = oAuth2Scope;
    }
}
