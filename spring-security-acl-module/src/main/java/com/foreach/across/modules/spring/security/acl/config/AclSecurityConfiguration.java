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

package com.foreach.across.modules.spring.security.acl.config;

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.core.database.DatabaseInfo;
import com.foreach.across.modules.spring.security.acl.SpringSecurityAclModuleCache;
import com.foreach.across.modules.spring.security.acl.business.AclAuthorities;
import com.foreach.across.modules.spring.security.acl.services.*;
import com.foreach.across.modules.spring.security.acl.strategy.SecurityPrincipalSidRetrievalStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.*;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.acls.model.SidRetrievalStrategy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.sql.DataSource;

/**
 * @author Arne Vandamme
 */
@Configuration
@RequiredArgsConstructor
public class AclSecurityConfiguration
{
	public static final String CACHE_NAME = SpringSecurityAclModuleCache.ACL;

	private final DataSource dataSource;
	private final CacheManager cacheManager;

	/**
	 * Create and expose the custom {@code PermissionFactory} that should be used.
	 */
	@Bean
	@Exposed
	public AclPermissionFactory aclPermissionFactory() {
		return new DefaultAclPermissionFactory();
	}

	@Bean
	public AclPermissionEvaluator aclPermissionEvaluator() {
		AclPermissionEvaluator evaluator = new AclPermissionEvaluator( aclService() );
		evaluator.setSidRetrievalStrategy( new SecurityPrincipalSidRetrievalStrategy() );
		evaluator.setPermissionFactory( aclPermissionFactory() );

		return evaluator;
	}

	@Bean
	public SecurityPrincipalAclService aclService() {
		SecurityPrincipalJdbcAclService aclService = new SecurityPrincipalJdbcAclService( dataSource, lookupStrategy(), aclCache() );

		DatabaseInfo databaseInfo = DatabaseInfo.retrieve( dataSource );

		if ( databaseInfo.isMySQL() ) {
			aclService.setClassIdentityQuery( "SELECT LAST_INSERT_ID()" );
			aclService.setSidIdentityQuery( "SELECT LAST_INSERT_ID()" );
		}
		else if ( databaseInfo.isSqlServer() ) {
			aclService.setClassIdentityQuery( "SELECT @@IDENTITY" );
			aclService.setSidIdentityQuery( "SELECT @@IDENTITY" );
		}
		else if ( databaseInfo.isOracle() ) {
			aclService.setClassIdentityQuery( "SELECT acl_class_sequence.currval FROM dual" );
			aclService.setSidIdentityQuery( "SELECT acl_sid_sequence.currval FROM dual" );
		}
		else if ( databaseInfo.isPostgres() ) {
			aclService.setClassIdentityQuery("select currval(pg_get_serial_sequence('acl_class', 'id'))");
			aclService.setSidIdentityQuery("select currval(pg_get_serial_sequence('acl_sid', 'id'))");
		}

		return aclService;
	}

	@Bean
	public AclSecurityService aclSecurityService() {
		return new AclSecurityServiceImpl( aclService(), aclPermissionEvaluator(), aclPermissionFactory() );
	}

	@Bean
	public AclCache aclCache() {
		return new SpringCacheBasedAclCache( cacheInstance(), permissionGrantingStrategy(), aclAuthorizationStrategy() );
	}

	private Cache cacheInstance() {
		return cacheManager.getCache( CACHE_NAME );
	}

	@Bean
	public LookupStrategy lookupStrategy() {
		BasicLookupStrategy lookupStrategy = new BasicLookupStrategy( dataSource, aclCache(), aclAuthorizationStrategy(), permissionGrantingStrategy() );
		lookupStrategy.setPermissionFactory( aclPermissionFactory() );
		return lookupStrategy;
	}

	@Bean
	public AclAuthorizationStrategy aclAuthorizationStrategy() {
		AclAuthorizationStrategyImpl strategy = new AclAuthorizationStrategyImpl(
				new SimpleGrantedAuthority( AclAuthorities.TAKE_OWNERSHIP ),
				new SimpleGrantedAuthority( AclAuthorities.AUDIT_ACL ),
				new SimpleGrantedAuthority( AclAuthorities.MODIFY_ACL )
		);
		strategy.setSidRetrievalStrategy( sidRetrievalStrategy() );

		return strategy;
	}

	@Bean
	public PermissionGrantingStrategy permissionGrantingStrategy() {
		return new DefaultPermissionGrantingStrategy( new ConsoleAuditLogger() );
	}

	@Bean
	public SidRetrievalStrategy sidRetrievalStrategy() {
		return new SecurityPrincipalSidRetrievalStrategy();
	}
}
