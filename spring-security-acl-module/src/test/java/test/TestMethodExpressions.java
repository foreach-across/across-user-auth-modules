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

package test;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.EmptyAcrossModule;
import com.foreach.across.core.context.bootstrap.AcrossBootstrapConfig;
import com.foreach.across.core.context.bootstrap.AcrossBootstrapConfigurer;
import com.foreach.across.modules.spring.security.acl.SpringSecurityAclModule;
import com.foreach.across.modules.spring.security.acl.business.AclPermission;
import com.foreach.across.modules.spring.security.acl.business.AclSecurityEntity;
import com.foreach.across.modules.spring.security.acl.services.AclPermissionFactory;
import com.foreach.across.modules.spring.security.acl.services.AclSecurityService;
import com.foreach.across.modules.spring.security.configuration.SpringSecurityWebConfigurerAdapter;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import com.foreach.across.modules.spring.security.infrastructure.services.CloseableAuthentication;
import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalService;
import com.foreach.across.test.AcrossTestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = TestMethodExpressions.Config.class)
public class TestMethodExpressions
{
	@Autowired
	private AclSecurityEntity aclHolder;

	@Autowired
	private MyService myService;

	@Autowired
	private SecurityPrincipalService principalService;

	@Test
	public void defaultPermissionRefused() {
		try (CloseableAuthentication ignore = principalService.authenticate( principal( "user", "not-allowed" ) )) {
			assertThatExceptionOfType( AccessDeniedException.class )
					.isThrownBy( () -> myService.defaultPermission( aclHolder ) );
		}
	}

	@Test
	public void customPermissionRefused() {
		try (CloseableAuthentication ignore = principalService.authenticate( principal( "user", "not-allowed" ) )) {
			assertThatExceptionOfType( AccessDeniedException.class )
					.isThrownBy( () -> myService.customPermission( aclHolder ) );
		}
	}

	@Test
	public void defaultPermissionAllowed() {
		try (CloseableAuthentication ignore = principalService.authenticate( principal( "user", "allowed" ) )) {
			assertThat( myService.defaultPermission( aclHolder ) ).isTrue();
		}
	}

	@Test
	public void customPermissionAllowed() {
		try (CloseableAuthentication ignore = principalService.authenticate( principal( "user", "allowed" ) )) {
			assertThat( myService.customPermission( aclHolder ) ).isTrue();
		}
	}

	static SecurityPrincipal principal( String name, String... authorities ) {
		return new SecurityPrincipal()
		{
			@Override
			public String getPrincipalName() {
				return name;
			}

			@Override
			public Collection<? extends GrantedAuthority> getAuthorities() {
				return Stream.of( authorities ).map( SimpleGrantedAuthority::new ).collect( Collectors.toList() );
			}
		};
	}

	@Configuration
	@AcrossTestConfiguration(modules = SpringSecurityAclModule.NAME)
	protected static class Config extends SpringSecurityWebConfigurerAdapter implements AcrossBootstrapConfigurer
	{
		@Override
		public void configureContext( AcrossBootstrapConfig contextConfiguration ) {
			contextConfiguration.extendModule( SpringSecurityAclModule.NAME, DataSourceTransactionManagerAutoConfiguration.class,
			                                   TransactionAutoConfiguration.class );
		}

		@Bean
		public AcrossModule myModule() {
			return new EmptyAcrossModule( "MyModule", MyService.class );
		}

		@Bean
		public AclSecurityEntity aclHolder() {
			return AclSecurityEntity.builder().id( 123L ).name( "acl holder" ).build();
		}
	}

	@Service
	static class MyService
	{
		MyService( SecurityPrincipalService principalService,
		           AclSecurityService aclSecurityService,
		           AclSecurityEntity aclHolder,
		           AclPermissionFactory permissionFactory ) {
			AclPermission customPermission = AclPermission.create( 10, 'C' );
			permissionFactory.registerPermission( customPermission, "my-custom-permission" );

			try (CloseableAuthentication ignore = principalService.authenticate( principal( "system" ) )) {
				aclSecurityService.allow( "allowed", aclHolder, AclPermission.READ, customPermission );
			}
		}

		@PreAuthorize("hasPermission(#object, 'READ')")
		boolean defaultPermission( AclSecurityEntity object ) {
			return true;
		}

		@PreAuthorize("hasPermission(#object, 'my-custom-permission')")
		boolean customPermission( AclSecurityEntity object ) {
			return true;
		}
	}
}
