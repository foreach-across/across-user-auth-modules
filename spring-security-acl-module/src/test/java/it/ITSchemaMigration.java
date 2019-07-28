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

package it;

import com.foreach.across.core.context.bootstrap.AcrossBootstrapConfig;
import com.foreach.across.core.context.bootstrap.AcrossBootstrapConfigurer;
import com.foreach.across.modules.spring.security.acl.business.AclPermission;
import com.foreach.across.modules.spring.security.acl.services.AclSecurityService;
import com.foreach.across.modules.spring.security.infrastructure.SpringSecurityInfrastructureModule;
import liquibase.integration.spring.SpringLiquibase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arne Vandamme
 * @since 4.0.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = { ITSpringSecurityAclModule.Config.class, ITSchemaMigration.InstallPreviousSchema.class })
public class ITSchemaMigration
{
	@Autowired
	private AclSecurityService aclSecurityService;

	@Test
	public void preExistingRecordCanBeRetrieved() {
		MutableAcl acl = aclSecurityService.getAcl( new ObjectIdentityImpl( "my.domain.SomeObject", 123L ) );
		assertThat( acl ).isNotNull();
		assertThat( acl.isGranted( Collections.singletonList( AclPermission.READ ),
		                           Collections.singletonList( new PrincipalSid( "john" ) ),
		                           true ) ).isTrue();
	}

	@Configuration
	protected static class InstallPreviousSchema implements AcrossBootstrapConfigurer
	{
		@Override
		public void configureContext( AcrossBootstrapConfig contextConfiguration ) {
			contextConfiguration.extendModule( SpringSecurityInfrastructureModule.NAME, PreviousSchema.class );
		}
	}

	@Configuration
	protected static class PreviousSchema
	{
		@Bean
		SpringLiquibase springLiquibase( DataSource dataSource ) {
			SpringLiquibase springLiquibase = new SpringLiquibase();
			springLiquibase.setDataSource( dataSource );
			springLiquibase.setChangeLog( "classpath:previous-schema.xml" );
			return springLiquibase;
		}
	}
}
