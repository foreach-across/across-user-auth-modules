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

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.modules.spring.security.acl.SpringSecurityAclModule;
import com.foreach.across.modules.spring.security.acl.infrastructure.CurrentAclSecurityPrincipalProxy;
import com.foreach.across.modules.spring.security.acl.services.AclSecurityService;
import com.foreach.across.modules.spring.security.configuration.AcrossWebSecurityConfigurer;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalRetrievalStrategy;
import com.foreach.across.test.AcrossTestConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 */
@ExtendWith(SpringExtension.class)
@DirtiesContext
@ContextConfiguration(classes = ITSpringSecurityAclModule.Config.class)
public class ITSpringSecurityAclModule
{
	@Autowired
	private AcrossContextBeanRegistry contextBeanRegistry;

	@Autowired(required = false)
	private AclSecurityService aclSecurityService;

	@Autowired(required = false)
	private CurrentAclSecurityPrincipalProxy currentPrincipal;

	@Autowired
	private SecurityPrincipalRetrievalStrategy principalRetrievalStrategy;

	@Test
	public void aclServiceShouldExist() {
		assertNotNull( contextBeanRegistry.getBeanOfTypeFromModule( SpringSecurityAclModule.NAME,
		                                                            MutableAclService.class ) );
		assertNotNull( aclSecurityService );
	}

	@Test
	public void currentSecurityPrincipalShouldBeOfAclType() {
		assertNotNull( currentPrincipal );
		assertFalse( currentPrincipal.isAuthenticated() );

		Authentication auth = mock( Authentication.class );
		when( auth.isAuthenticated() ).thenReturn( true );
		when( auth.getPrincipal() ).thenReturn( "principalName" );

		SecurityPrincipal principal = mock( SecurityPrincipal.class );
		when( principalRetrievalStrategy.getPrincipalByName( "principalName" ) ).thenReturn( Optional.of( principal ) );

		SecurityContextHolder.getContext().setAuthentication( auth );

		assertTrue( currentPrincipal.isAuthenticated() );
		assertSame( principal, currentPrincipal.getPrincipal() );
	}

	@Configuration
	@AcrossTestConfiguration
	protected static class Config implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			context.addModule( new SpringSecurityAclModule() );
			context.addModule( new SpringSecurityModule() );
		}

		@Bean
		public SecurityPrincipalRetrievalStrategy principalRetrievalStrategy() {
			return mock( SecurityPrincipalRetrievalStrategy.class );
		}
	}
}
