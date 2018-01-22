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

package com.foreach.across.modules.spring.security.acl.ui;

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderService;
import com.foreach.across.modules.spring.security.acl.business.AclPermission;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalService;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("unchecked")
public class TestEntityAclPermissionsFormSectionAdapter
{
	private AclPermissionsFormSection empty = AclPermissionsForm.section().name( "user" ).entityType( String.class ).build();

	@Mock
	private EntityConfiguration entityConfiguration;

	@Mock
	private EntityModel entityModel;

	@Mock
	private ConversionService conversionService;

	@Mock
	private SecurityPrincipalService securityPrincipalService;

	@Before
	public void defaults() {
		when( entityConfiguration.getName() ).thenReturn( "user" );
		when( entityConfiguration.getEntityType() ).thenReturn( SecurityPrincipal.class );
		when( entityConfiguration.hasEntityModel() ).thenReturn( true );
		when( entityConfiguration.getEntityModel() ).thenReturn( entityModel );
	}

	@Test
	public void nothingChangedIfAllPropertiesSet() {
		AclPermissionsFormSection original = AclPermissionsFormSection
				.builder()
				.name( "user" )
				.entityType( String.class )
				.permissions( AclPermission.CREATE, AclPermission.READ, AclPermission.WRITE, AclPermission.DELETE )
				.sidMatcher( ( sid, entity ) -> entity instanceof String )
				.sidForObjectResolver( user -> new PrincipalSid( (String) user ) )
				.objectForSidResolver( sid -> sid )
				.transportIdForObjectResolver( user -> (String) user )
				.objectForTransportIdResolver( transportId -> "original:" + transportId )
				.objectLabelViewElementProvider( ( object, builderContext ) -> TextViewElement.text( (String) object ) )
				.itemSelectorBuilder( AclPermissionsForm.selectorControl().control( BootstrapUiBuilders.textbox() ) )
				.build();

		AclPermissionsFormSection adapted = adapt( original );
		assertThat( adapted ).isEqualTo( original );
	}

	@Test
	public void sidMatcherMatchesIfEntityIsOfType() {
		val adapted = adapt( empty );
		val sidMatcher = adapted.getSidMatcher();

		assertThat( sidMatcher ).isNotNull();
		assertThat( sidMatcher.test( null, 123L ) ).isFalse();
		assertThat( sidMatcher.test( null, mock( SecurityPrincipal.class ) ) ).isTrue();
	}

	@Test
	public void sidForObjectResolverSupportsBothSecurityPrincipalAndGrantedAuthority() {
		val adapted = adapt( empty );
		val sidForObjectResolver = adapted.getSidForObjectResolver();

		assertThat( sidForObjectResolver ).isNotNull();
		assertThat( sidForObjectResolver.apply( (GrantedAuthority) () -> "some authority" ) ).isEqualTo( new GrantedAuthoritySid( "some authority" ) );
		assertThat( sidForObjectResolver.apply( new SecurityPrincipal()
		{
			@Override
			public String getPrincipalName() {
				return "some principal";
			}

			@Override
			public Collection<? extends GrantedAuthority> getAuthorities() {
				return null;
			}
		} ) ).isEqualTo( new PrincipalSid( "some principal" ) );
	}

	@Test
	public void objectForSidResolverRequiresSecurityPrincipal() {
		when( entityConfiguration.getEntityType() ).thenReturn( String.class );

		assertThatExceptionOfType( IllegalStateException.class )
				.isThrownBy( () -> adapt( empty ) );
	}

	@Test
	public void objectForSidResolverSupportsSecurityPrincipal() {
		val adapted = adapt( empty );
		val objectForSidResolver = adapted.getObjectForSidResolver();

		SecurityPrincipal principal = mock( SecurityPrincipal.class );
		when( securityPrincipalService.getPrincipalByName( "some principal" ) ).thenReturn( principal );

		assertThat( objectForSidResolver ).isNotNull();
		assertThat( objectForSidResolver.apply( new GrantedAuthoritySid( "some principal" ) ) ).isNull();
		assertThat( objectForSidResolver.apply( new PrincipalSid( "some principal" ) ) ).isSameAs( principal );
	}

	@Test
	public void transportIdForObjectResolverUsesEntityModel() {
		val adapted = adapt( empty );
		val transportIdForObjectResolver = adapted.getTransportIdForObjectResolver();

		when( entityConfiguration.getId( "my object" ) ).thenReturn( 123L );

		assertThat( transportIdForObjectResolver ).isNotNull();
		assertThat( transportIdForObjectResolver.apply( "my object" ) ).isEqualTo( 123L );
	}

	@Test
	public void objectForTransportIdResolverUsesEntityModel() {
		val adapted = adapt( empty );
		val objectForTransportIdResolver = adapted.getObjectForTransportIdResolver();

		when( entityConfiguration.getIdType() ).thenReturn( Long.class );
		when( conversionService.convert( "123", Long.class ) ).thenReturn( 123L );
		when( entityModel.findOne( 123L ) ).thenReturn( "my object" );

		assertThat( objectForTransportIdResolver ).isNotNull();
		assertThat( objectForTransportIdResolver.apply( "123" ) ).isEqualTo( "my object" );
	}

	@Test
	public void objectLabelViewElementProviderUsesEntityLabel() {
		val adapted = adapt( empty );
		val objectLabelViewElementProvider = adapted.getObjectLabelViewElementProvider();

		when( entityConfiguration.getLabel( "my object" ) ).thenReturn( "my object label" );

		assertThat( objectLabelViewElementProvider ).isNotNull();
		assertThat( ( (TextViewElement) objectLabelViewElementProvider.apply( "my object", new DefaultViewElementBuilderContext() ) ).getText() )
				.isEqualTo( "my object label" );
	}

	@Test
	public void itemSelectorBuilderGetsCreated() {
		val adapted = adapt( empty );
		val selectorBuilder = adapted.getItemSelectorBuilder();

		assertThat( selectorBuilder ).isNotNull();
		assertThat( selectorBuilder ).isInstanceOf( AclPermissionsFormItemSelectorControl.class );
	}

	private AclPermissionsFormSection adapt( AclPermissionsFormSection original ) {
		ObjectProvider<EntityRegistry> registryObjectProvider = mock( ObjectProvider.class );
		EntityRegistry entityRegistry = mock( EntityRegistry.class );
		when( entityRegistry.getEntityConfiguration( String.class ) ).thenReturn( entityConfiguration );
		when( registryObjectProvider.getIfAvailable() ).thenReturn( entityRegistry );

		ObjectProvider<EntityViewElementBuilderService> builderServiceObjectProvider = mock( ObjectProvider.class );
		when( builderServiceObjectProvider.getIfAvailable() ).thenReturn( mock( EntityViewElementBuilderService.class ) );
		return new EntityAclPermissionsFormSectionAdapter( registryObjectProvider, builderServiceObjectProvider, securityPrincipalService, conversionService )
				.adapt( original );
	}
}
