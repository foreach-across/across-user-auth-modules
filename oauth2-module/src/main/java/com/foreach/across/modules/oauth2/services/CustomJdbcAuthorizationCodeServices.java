/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.foreach.across.modules.oauth2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;

/**
 * @author Andy Somers
 */
public class CustomJdbcAuthorizationCodeServices extends RandomValueAuthorizationCodeServices
{
	@Autowired
	private List<OAuth2AuthenticationSerializer> serializers;

	private static final String DEFAULT_SELECT_STATEMENT = "select code, authentication from oauth_code where code = ?";
	private static final String DEFAULT_INSERT_STATEMENT =
			"insert into oauth_code (code, authentication, time_stamp) values (?, ?, ?)";
	private static final String DEFAULT_DELETE_STATEMENT = "delete from oauth_code where code = ?";

	private String selectAuthenticationSql = DEFAULT_SELECT_STATEMENT;
	private String insertAuthenticationSql = DEFAULT_INSERT_STATEMENT;
	private String deleteAuthenticationSql = DEFAULT_DELETE_STATEMENT;

	private final JdbcTemplate jdbcTemplate;

	public CustomJdbcAuthorizationCodeServices( DataSource dataSource ) {
		this.jdbcTemplate = new JdbcTemplate( dataSource );
	}

	@Override
	protected void store( String code, OAuth2Authentication authentication ) {
		jdbcTemplate.update( insertAuthenticationSql,
		                     new Object[] { code, new SqlLobValue( serializeAuthentication( authentication ) ),
		                                    new Date() }, new int[] {
						Types.VARCHAR, Types.BLOB, Types.TIMESTAMP } );
	}

	@Override
	public OAuth2Authentication remove( String code ) {
		OAuth2Authentication authentication;

		try {
			authentication = jdbcTemplate.queryForObject( selectAuthenticationSql,
			                                              new RowMapper<OAuth2Authentication>()
			                                              {
				                                              public OAuth2Authentication mapRow( ResultSet rs,
				                                                                                  int rowNum )
						                                              throws SQLException {
					                                              byte[] authenticationByteStream = rs.getBytes(
							                                              "authentication" );
					                                              return deserializeAuthentication(
							                                              authenticationByteStream );
				                                              }
			                                              }, code );
		}
		catch ( EmptyResultDataAccessException e ) {
			return null;
		}

		if ( authentication != null ) {
			jdbcTemplate.update( deleteAuthenticationSql, code );
		}

		return authentication;
	}

	private byte[] serializeAuthentication( OAuth2Authentication authentication ) {
		for ( OAuth2AuthenticationSerializer serializer : serializers ) {
			if ( serializer.canSerialize( authentication ) ) {
				return serializer.serialize( authentication );
			}
		}
		return SerializationUtils.serialize( authentication );
	}

	@SuppressWarnings("unchecked")
	private OAuth2Authentication deserializeAuthentication( byte[] authentication ) {
		Object object = org.springframework.util.SerializationUtils.deserialize( authentication );
		if ( object instanceof AuthenticationSerializerObject ) {
			AuthenticationSerializerObject oAuth2AuthenticationSerializerObject =
					(AuthenticationSerializerObject) object;
			for ( OAuth2AuthenticationSerializer serializer : serializers ) {
				if ( serializer.canDeserialize( oAuth2AuthenticationSerializerObject ) ) {
					return serializer.deserialize( oAuth2AuthenticationSerializerObject );
				}
			}
			throw new OAuth2AuthenticationSerializer.SerializationException( object );
		}
		else if ( object instanceof OAuth2Authentication ) {
			return (OAuth2Authentication) object;
		}
		else {
			throw new OAuth2AuthenticationSerializer.SerializationException( object );
		}
	}

	public void setSelectAuthenticationSql( String selectAuthenticationSql ) {
		this.selectAuthenticationSql = selectAuthenticationSql;
	}

	public void setInsertAuthenticationSql( String insertAuthenticationSql ) {
		this.insertAuthenticationSql = insertAuthenticationSql;
	}

	public void setDeleteAuthenticationSql( String deleteAuthenticationSql ) {
		this.deleteAuthenticationSql = deleteAuthenticationSql;
	}
}
