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
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderService;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.spring.security.acl.support.AclUtils;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalService;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * Adapts an existing {@link AclPermissionsFormSection} that manages a registered entity.
 * Generates defaults for any missing property.
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
@Component
@RequiredArgsConstructor
class EntityAclPermissionsFormSectionAdapter
{
	private final ObjectProvider<EntityRegistry> entityRegistry;
	private final ObjectProvider<EntityViewElementBuilderService> viewElementBuilderService;
	private final SecurityPrincipalService securityPrincipalService;
	private final ConversionService conversionService;

	@SuppressWarnings("unchecked")
	AclPermissionsFormSection adapt( AclPermissionsFormSection section ) {
		Class<?> entityType = section.getEntityType();

		if ( entityType != null ) {
			EntityConfiguration<Object> entityConfiguration
					= (EntityConfiguration<Object>) entityRegistry.getIfAvailable().getEntityConfiguration( entityType );
			if ( entityConfiguration != null ) {
				val adapted = section.toBuilder();

				adapted.sidMatcher( adaptSidMatcher( section.getSidMatcher(), entityConfiguration ) );
				adapted.sidForObjectResolver( adaptSidForObjectResolver( section.getSidForObjectResolver() ) );
				adapted.objectForSidResolver( adaptObjectForSidResolver( section.getObjectForSidResolver(), entityConfiguration ) );
				adapted.transportIdForObjectResolver( adaptTransportIdForObjectResolver( section.getTransportIdForObjectResolver(), entityConfiguration ) );
				adapted.objectForTransportIdResolver( adaptObjectForTransportIdResolver( section.getObjectForTransportIdResolver(), entityConfiguration ) );
				adapted.objectLabelViewElementProvider(
						adaptObjectLabelViewElementProvider( section.getObjectLabelViewElementProvider(), entityConfiguration )
				);
				adapted.itemSelectorBuilder( adaptItemSelectorBuilder( section.getItemSelectorBuilder(), entityConfiguration ) );

				return adapted.build();
			}
		}

		return section;
	}

	private BiPredicate<Sid, Object> adaptSidMatcher( BiPredicate<Sid, Object> sidMatcher, EntityConfiguration<Object> entityConfiguration ) {
		if ( sidMatcher == null ) {
			return ( sid, object ) -> entityConfiguration.getEntityType().isInstance( object );
		}
		return sidMatcher;
	}

	private Function<Object, Sid> adaptSidForObjectResolver( Function<Object, Sid> sidForObjectResolver ) {
		if ( sidForObjectResolver == null ) {
			return object -> {
				if ( object instanceof GrantedAuthority ) {
					return AclUtils.sid( (GrantedAuthority) object );
				}
				if ( object instanceof SecurityPrincipal ) {
					return AclUtils.sid( (SecurityPrincipal) object );
				}
				throw new IllegalArgumentException(
						"Unable to generate a default sid for " + object + "; must either implement GrantedAuthority or SecurityPrincipal" );
			};
		}
		return sidForObjectResolver;
	}

	private Function<Sid, Object> adaptObjectForSidResolver( Function<Sid, Object> objectForSidResolver, EntityConfiguration<Object> entityConfiguration ) {
		if ( objectForSidResolver == null ) {
			if ( !SecurityPrincipal.class.isAssignableFrom( entityConfiguration.getEntityType() ) ) {
				throw new IllegalStateException(
						"No ObjectForSidResolver set and entity type does not implement SecurityPrincipal - unable to create one automatically" );
			}

			return sid -> {
				if ( sid instanceof PrincipalSid ) {
					return securityPrincipalService.getPrincipalByName( ( (PrincipalSid) sid ).getPrincipal() );
				}
				return null;
			};
		}
		return objectForSidResolver;
	}

	private Function<Object, Serializable> adaptTransportIdForObjectResolver( Function<Object, Serializable> transportIdForObjectResolver,
	                                                                          EntityConfiguration<Object> entityConfiguration ) {
		if ( transportIdForObjectResolver == null ) {
			return entityConfiguration::getId;
		}
		return transportIdForObjectResolver;
	}

	private Function<String, Object> adaptObjectForTransportIdResolver( Function<String, Object> objectForTransportIdResolver,
	                                                                    EntityConfiguration<Object> entityConfiguration ) {
		if ( objectForTransportIdResolver == null ) {
			if ( !entityConfiguration.hasEntityModel() ) {
				throw new IllegalStateException(
						"No ObjectForTransportIdResolver set and entity does not have an EntityModule - unable to create one automatically" );
			}
			return transportId -> {
				Object primaryId = conversionService.convert( transportId, entityConfiguration.getIdType() );
				return entityConfiguration.getEntityModel().findOne( (Serializable) primaryId );
			};
		}
		return objectForTransportIdResolver;
	}

	private BiFunction<Object, ViewElementBuilderContext, ViewElement> adaptObjectLabelViewElementProvider(
			BiFunction<Object, ViewElementBuilderContext, ViewElement> objectLabelViewElementProvider,
			EntityConfiguration<Object> entityConfiguration ) {
		if ( objectLabelViewElementProvider == null ) {
			return ( object, builderContext ) -> BootstrapUiBuilders.text( entityConfiguration.getLabel( object ) ).build( builderContext );
		}
		return objectLabelViewElementProvider;
	}

	private ViewElementBuilder adaptItemSelectorBuilder( ViewElementBuilder itemSelectorBuilder, EntityConfiguration<Object> entityConfiguration ) {
		if ( itemSelectorBuilder == null ) {
			EntityPropertyDescriptor descriptor = EntityPropertyDescriptor
					.builder( entityConfiguration.getName() )
					.propertyType( entityConfiguration.getEntityType() )
					.valueFetcher( entity -> null )
					.writable( true )
					.attribute( EntityAttributes.CONTROL_NAME, "extensions[aclPermissionsFormItemSelector]" )
					.build();
			val builderService = viewElementBuilderService.getIfAvailable();

			return AclPermissionsForm.selectorControl().control( builderService.createElementBuilder( descriptor, ViewElementMode.FORM_WRITE ) );
		}
		return itemSelectorBuilder;
	}
}
