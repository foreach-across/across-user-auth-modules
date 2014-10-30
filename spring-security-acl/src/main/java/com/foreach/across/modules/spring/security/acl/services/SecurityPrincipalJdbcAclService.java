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
package com.foreach.across.modules.spring.security.acl.services;

import com.foreach.across.modules.spring.security.acl.config.AclSecurityConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Sid;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * @author Arne Vandamme
 */
public class SecurityPrincipalJdbcAclService extends JdbcMutableAclService implements SecurityPrincipalAclService
{
	private static final String SELECT_OBJECT_IDENTITIES_FOR_SID =
			"select distinct obj.object_id_identity as obj_id, class.class as class "
					+ "from acl_entry entry inner join acl_object_identity obj on obj.id = entry.acl_object_identity " +
					"inner join acl_class class on obj.object_id_class = class.id "
					+ "where entry.sid = ?";

	@Autowired
	private CacheManager cacheManager;

	private Cache cache = null;

	public SecurityPrincipalJdbcAclService( DataSource dataSource,
	                                        LookupStrategy lookupStrategy,
	                                        AclCache aclCache ) {
		super( dataSource, lookupStrategy, aclCache );
	}

	@PostConstruct
	public void setupCache() {
		cache = cacheManager.getCache( AclSecurityConfiguration.CACHE_NAME );
	}

	@Override
	protected Long createOrRetrieveSidPrimaryKey( Sid sid, boolean allowCreate ) {
		Long sidId;
		Long cachedSid = cache.get( sid, Long.class );
		if ( cachedSid != null ) {
			return cachedSid;
		}
		else {
			sidId = super.createOrRetrieveSidPrimaryKey( sid, allowCreate );
			cache.put( sid, sidId );
		}
		return sidId;
	}

	@Override
	protected Long createOrRetrieveClassPrimaryKey( String type, boolean allowCreate ) {
		Long classId;
		Long cachedClassId = cache.get( type, Long.class );
		if ( cachedClassId != null ) {
			return cachedClassId;
		}
		else {
			classId = super.createOrRetrieveClassPrimaryKey( type, allowCreate );
			cache.put( type, classId );
		}
		return classId;
	}

	@Override
	public List<ObjectIdentity> findObjectIdentitiesWithAclForSid( Sid sid ) {
		Long ownerId = createOrRetrieveSidPrimaryKey( sid, false );

		if ( ownerId == null ) {
			return Collections.emptyList();
		}

		Object[] args = {  ownerId };
		List<ObjectIdentity> objects = jdbcTemplate.query( SELECT_OBJECT_IDENTITIES_FOR_SID, args,
		                                                   new RowMapper<ObjectIdentity>()
		                                                   {
			                                                   public ObjectIdentity mapRow( ResultSet rs,
			                                                                                 int rowNum ) throws SQLException {
				                                                   String javaType = rs.getString( "class" );
				                                                   Long identifier = rs.getLong( "obj_id" );

				                                                   return new ObjectIdentityImpl( javaType,
				                                                                                  identifier );
			                                                   }
		                                                   } );
		return objects;
	}
}
