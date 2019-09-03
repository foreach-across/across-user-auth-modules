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
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.bootstrapui.components.builder.NavComponentBuilder;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static com.foreach.across.modules.entity.views.EntityViewCustomizers.basicSettings;
import static com.foreach.across.modules.entity.views.EntityViewCustomizers.formSettings;
import static com.foreach.across.modules.entity.views.EntityViewFactoryAttributes.defaultAccessValidator;
import static com.foreach.across.modules.spring.security.acl.config.icons.SpringSecurityAclModuleIcons.springSecurityAclIcons;

/**
 * Registers the different ACL permissions form views on the relevant entities.
 * This is an {@link EntityConfigurer} that is supposed to run as late as possible as it will simply register the default
 * ACL permissions form view on all {@link com.foreach.across.modules.entity.registry.EntityConfiguration} with an
 * attribute value for {@link AclPermissionsFormRegistry#ATTR_ACL_PROFILE} but not yet having a view with the name
 * {@link AclPermissionsForm#VIEW_NAME}.
 * <p/>
 * Because this configurer runs so late, customization of the default view is not really possible. When that is required,
 * the view should be manually registered instead and then this configurer will simply back off.
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
@ConditionalOnAcrossModule({ EntityModule.NAME, AdminWebModule.NAME })
@ConditionalOnClass(EntityConfigurer.class)
@RequiredArgsConstructor
@Order
@Component
class AclPermissionsFormViewConfiguration implements EntityConfigurer
{
	private final AclPermissionsFormViewProcessor aclFormProcessor;
	private final AclPermissionsFormRegistry aclFormRegistry;

	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		entities.matching( entityConfiguration -> !entityConfiguration.hasView( AclPermissionsForm.VIEW_NAME ) )
		        // Register the view to any configuration that does not have it yet
		        .formView(
				        AclPermissionsForm.VIEW_NAME,
				        basicSettings()
						        .adminMenu( AclPermissionsForm.MENU_PATH,
						                    item -> item.attribute( NavComponentBuilder.ATTR_ICON, springSecurityAclIcons.permission.menuItem() ) )
						        .accessValidator( defaultAccessValidator().and( this::isAclFormAllowed ) )
						        .andThen( formSettings().forExtension( true ) )
						        .andThen( builder -> builder.viewProcessor( aclFormProcessor ) )
		        )
		        // Remove the view again for any entity that does not have the required attribute set at the end
		        .postProcessor( entityConfiguration -> {
			        if ( !entityConfiguration.hasAttribute( AclPermissionsFormRegistry.ATTR_ACL_PROFILE ) ) {
				        entityConfiguration.removeView( AclPermissionsForm.VIEW_NAME );
			        }
		        } );
	}

	/**
	 * Checks if an entity configuration actually has a valid form registered.
	 */
	@SuppressWarnings("unused")
	private boolean isAclFormAllowed( EntityViewFactory viewFactory, EntityViewContext viewContext ) {
		if ( viewContext != null ) {
			EntityConfiguration entityConfiguration = viewContext.getEntityConfiguration();
			return aclFormRegistry.getForEntityConfiguration( entityConfiguration ).isPresent();
		}

		return false;
	}
}

