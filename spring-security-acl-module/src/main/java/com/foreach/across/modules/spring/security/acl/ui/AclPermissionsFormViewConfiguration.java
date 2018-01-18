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

import com.foreach.across.core.annotations.ConditionalOnAcrossModule;
import com.foreach.across.core.annotations.PostRefresh;
import com.foreach.across.modules.adminweb.menu.EntityAdminMenuEvent;
import com.foreach.across.modules.bootstrapui.components.builder.NavComponentBuilder;
import com.foreach.across.modules.bootstrapui.elements.FaIcon;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.web.EntityLinkBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

/**
 * Registers the different ACL permissions form views on the relevant entities.
 * This is an {@link EntityConfigurer} that is supposed to run as late as possible as it will simply register the default
 * ACL permissions form view on all {@link com.foreach.across.modules.entity.registry.EntityConfiguration} with an
 * attribute value for {@link AclPermissionsFormRegistry#ATTR_ACL_PROFILE} but not yet having a view with the name
 * {@link AclPermissionsFormViewProcessor#VIEW_NAME}.
 * <p/>
 * Because this configurer runs so late, customization of the default view is not really possible. When that is required,
 * the view should be manually registered instead and then this configurer will simply back off.
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
@ConditionalOnAcrossModule(EntityModule.NAME)
@ConditionalOnClass(EntityConfigurer.class)
@RequiredArgsConstructor
@Order
@Component
class AclPermissionsFormViewConfiguration implements EntityConfigurer
{
	private final AclPermissionsFormViewProcessor aclFormProcessor;
	private final AclPermissionsFormRegistry aclFormRegistry;

	private EntityViewContext entityViewContext;

	@PostRefresh
	void loadRequiredBeans( EntityViewContext entityViewContext ) {
		this.entityViewContext = entityViewContext;
	}

	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		entities.matching( entityConfiguration -> !entityConfiguration.hasView( AclPermissionsFormViewProcessor.VIEW_NAME ) )
		        // Register the view to any configuration that does not have it yet
		        .formView( AclPermissionsFormViewProcessor.VIEW_NAME, vb -> vb.showProperties().viewProcessor( aclFormProcessor ) )
		        // Remove the view again for any entity that does not have the required attribute set at the end
		        .postProcessor( entityConfiguration -> {
			        if ( !entityConfiguration.hasAttribute( AclPermissionsFormRegistry.ATTR_ACL_PROFILE ) ) {
				        entityConfiguration.removeView( AclPermissionsFormViewProcessor.VIEW_NAME );
			        }
		        } );
	}

	/**
	 * Register the menu item to the ACL permissions view. This requires an {@link com.foreach.across.modules.entity.views.context.EntityViewContext}
	 * to be available with an existing entity attached. This is usually the case when working with default entity views, but a manual context
	 * might need to be created if you want to manually create the full entity menu.
	 */
	@EventListener
	void registerAclPermissionsMenuItem( EntityAdminMenuEvent<?> entityAdminMenu ) {
		if ( entityAdminMenu.isForUpdate() ) {
			EntityViewContext viewContext = retrieveEntityViewContext();

			if ( viewContext != null ) {
				EntityConfiguration entityConfiguration = entityViewContext.getEntityConfiguration();
				if ( entityConfiguration.hasView( AclPermissionsFormViewProcessor.VIEW_NAME ) ) {
					Optional<AclPermissionsForm> permissionsForm = aclFormRegistry.getForEntityConfiguration( entityConfiguration );
					permissionsForm.ifPresent(
							form -> {
								EntityLinkBuilder linkBuilder = entityConfiguration.getAttribute( EntityLinkBuilder.class );
								entityAdminMenu.builder()
								               .item(
										               form.getMenuPath(),
										               "#{adminMenu.views[aclPermissions]=Permissions}",
										               UriComponentsBuilder.fromUriString( linkBuilder.update( entityAdminMenu.getEntity() ) )
										                                   .queryParam( "view", "aclPermissions" )
										                                   .toUriString()
								               )
								               .attribute( NavComponentBuilder.ATTR_ICON, new FaIcon( FaIcon.WebApp.LOCK ) );
							}
					);
				}
			}
		}
	}

	private EntityViewContext retrieveEntityViewContext() {
		EntityViewContext context = this.entityViewContext.isForAssociation() ? this.entityViewContext.getParentContext() : this.entityViewContext;
		return context.holdsEntity() ? context : null;
	}
}

