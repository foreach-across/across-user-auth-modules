package com.foreach.across.modules.oauth2.config;

import com.foreach.across.core.database.SchemaConfiguration;
import com.foreach.across.core.database.SchemaObject;

import java.util.Arrays;

public class OAuth2SchemaConfiguration extends SchemaConfiguration {

    public static final String TABLE_CLIENT = "oauth_client";
    public static final String TABLE_CLIENT_ROLE = "oauth_client_role";
    public static final String TABLE_CLIENT_SCOPE = "oauth_client_scope";
    public static final String TABLE_SCOPE = "oauth_scope";
    public static final String TABLE_RESOURCEID = "oauth_resource_id";
    public static final String TABLE_GRANT_TYPE = "oauth_grant_type";
    public static final String TABLE_REDIRECT_URI = "oauth_redirect_uri";

    public OAuth2SchemaConfiguration() {
        super( Arrays.asList( new SchemaObject( "table.client", TABLE_CLIENT ),
                new SchemaObject( "table.client_role", TABLE_CLIENT_ROLE ),
                new SchemaObject( "table.client_scope", TABLE_CLIENT_SCOPE ),
                new SchemaObject( "table.scope", TABLE_SCOPE ),
                new SchemaObject( "table.resource", TABLE_RESOURCEID ),
                new SchemaObject( "table.grant_type", TABLE_GRANT_TYPE ),
                new SchemaObject( "table.redirect_uri", TABLE_REDIRECT_URI ) ) );
    }
}
