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

package com.foreach.across.modules.it.ldap;

import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.bootstrapui.BootstrapUiModule;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.ldap.LdapModule;
import com.foreach.across.modules.spring.security.configuration.AcrossWebSecurityConfigurer;
import com.foreach.across.modules.user.UserModule;
import com.foreach.across.test.AcrossTestConfiguration;
import com.foreach.across.test.AcrossWebAppConfiguration;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ActiveProfilesResolver;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Arne Vandamme
 * @since 1.0.0
 */
@AcrossWebAppConfiguration
@RunWith(Parameterized.class)
@ActiveProfiles(resolver = ITBootstrapWithAdditionalModules.CustomProfilesResolver.class)
public class ITBootstrapWithAdditionalModules
{
	private final String profile;

	private TestContextManager testContextManager;

	public ITBootstrapWithAdditionalModules( String profile
	) {
		this.profile = profile;
	}

	@Parameterized.Parameters(name = "{index}: modules: {0}")
	public static Collection primeNumbers() {
		Set<Set<String>> powerset = Sets.powerSet( Sets.newHashSet( "entitymodule", "adminwebmodule", "usermodule" ) );
		Object[] parameters = new Object[powerset.size()];
		final AtomicInteger i = new AtomicInteger();
		powerset.stream().forEach( item -> {
			parameters[i.get()] = new Object[] { StringUtils.join( item, "," ) };
			i.incrementAndGet();
		} );
		return Arrays.asList( parameters );
	}

	@Before
	public void setUpContext() throws Exception {
		// For Spring 4.2
		// @ClassRule public static final SpringClassRule SCR = new SpringClassRule();
		// @Rule public final SpringMethodRule springMethodRule = new SpringMethodRule();
		System.setProperty( AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, profile );
		this.testContextManager = new TestContextManager( getClass() );
		this.testContextManager.prepareTestInstance( this );
	}

	@After
	public void destroyContext() {
		testContextManager.getTestContextFromTestContextManager().markApplicationContextDirty(
				DirtiesContext.HierarchyMode.EXHAUSTIVE );
		testContextManager.getTestContextFromTestContextManager().setAttribute(
				DependencyInjectionTestExecutionListener.REINJECT_DEPENDENCIES_ATTRIBUTE, Boolean.TRUE );
		System.clearProperty( AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME );
	}

	@Test
	public void servicesShouldBeExposed() {
	}

	@Configuration
	@AcrossTestConfiguration(modules = LdapModule.NAME)
	protected static class Config
	{
		@Profile("entitymodule")
		@Configuration
		public static class EntityModuleProfile
		{
			@Bean
			public EntityModule entityModule() {
				return new EntityModule();
			}

			@Bean
			public BootstrapUiModule bootstrapUiModule() {
				return new BootstrapUiModule();
			}
		}

		@Profile("adminwebmodule")
		@Configuration
		public static class AdminWebModuleProfile
		{
			@Bean
			public AdminWebModule adminWebModule() {
				return new AdminWebModule();
			}
		}

		@Profile("usermodule")
		@Configuration
		public static class UserModuleProfile implements AcrossWebSecurityConfigurer
		{
			@Bean
			public UserModule userModule() {
				return new UserModule();
			}
		}
	}

	private static class TestContextManager extends org.springframework.test.context.TestContextManager
	{
		private TestContextManager( Class<?> testClass ) {
			super( testClass );
		}

		TestContext getTestContextFromTestContextManager() {
			return super.getTestContext();
		}
	}

	static class CustomProfilesResolver implements ActiveProfilesResolver
	{
		public CustomProfilesResolver() {
		}

		@Override
		public String[] resolve( Class<?> testClass ) {
			return System.getProperty( AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME ).split( "," );
		}
	}
}
