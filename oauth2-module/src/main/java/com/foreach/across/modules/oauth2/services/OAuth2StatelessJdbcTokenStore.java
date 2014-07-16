package com.foreach.across.modules.oauth2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.util.SerializationUtils;

import javax.sql.DataSource;
import java.util.List;

public class OAuth2StatelessJdbcTokenStore extends JdbcTokenStore
{
	@Autowired
	private List<OAuth2AuthenticationSerializer> serializers;

	public OAuth2StatelessJdbcTokenStore( DataSource dataSource ) {
		super( dataSource );
	}

	@Override
	protected byte[] serializeAuthentication( OAuth2Authentication authentication ) {
		for( OAuth2AuthenticationSerializer serializer : serializers ) {
			if( serializer.canSerialize( authentication ) ) {
				return serializer.serialize( authentication );
			}
		}
		return super.serializeAuthentication( authentication );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	protected OAuth2Authentication deserializeAuthentication( byte[] authentication ) {
		Object object = SerializationUtils.deserialize( authentication );
		if( object instanceof AuthenticationSerializerObject ) {
			AuthenticationSerializerObject oAuth2AuthenticationSerializerObject = (AuthenticationSerializerObject) object;
			for( OAuth2AuthenticationSerializer serializer : serializers ) {
				if( serializer.canDeserialize( oAuth2AuthenticationSerializerObject ) ) {
					return serializer.deserialize( oAuth2AuthenticationSerializerObject );
				}
			}
			throw new OAuth2AuthenticationSerializer.SerializationException( object );
		} else if ( object instanceof OAuth2Authentication ) {
			return (OAuth2Authentication) object;
		} else {
			throw new OAuth2AuthenticationSerializer.SerializationException( object );
		}
	}
}
