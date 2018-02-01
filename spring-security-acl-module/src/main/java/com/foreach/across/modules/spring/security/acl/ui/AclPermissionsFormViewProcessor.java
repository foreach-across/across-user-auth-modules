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
import com.foreach.across.modules.bootstrapui.elements.ButtonViewElement;
import com.foreach.across.modules.bootstrapui.elements.Style;
import com.foreach.across.modules.bootstrapui.elements.builder.FormViewElementBuilder;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.EntityViewProcessor;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.processors.ExtensionViewProcessorAdapter;
import com.foreach.across.modules.entity.views.processors.support.EntityViewPageHelper;
import com.foreach.across.modules.entity.views.processors.support.ViewElementBuilderMap;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.spring.security.acl.services.AclOperations;
import com.foreach.across.modules.spring.security.acl.services.AclPermissionFactory;
import com.foreach.across.modules.spring.security.acl.services.AclSecurityService;
import com.foreach.across.modules.spring.security.acl.support.AclUtils;
import com.foreach.across.modules.web.resource.WebResource;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.builder.ContainerViewElementBuilderSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.function.Function;

/**
 * View processor for an ACL permissions form.
 *
 * @author Arne Vandamme
 * @see AclPermissionsFormRegistry
 * @since 3.0.0
 */
@ConditionalOnAcrossModule(EntityModule.NAME)
@ConditionalOnClass(EntityViewProcessor.class)
@Component
@RequiredArgsConstructor
@SuppressWarnings("WeakerAccess")
public class AclPermissionsFormViewProcessor extends ExtensionViewProcessorAdapter<AclPermissionsFormController>
{
	/**
	 * Name of the extension holding the {@code AclPermissionsFormController}.
	 */
	public static final String CONTROLLER_EXTENSION = "aclPermissions";

	private final AclSecurityService aclSecurityService;
	private final AclPermissionFactory permissionFactory;
	private final AclPermissionsFormRegistry permissionsFormRegistry;
	private final EntityAclPermissionsFormSectionAdapter entitySectionAdapter;

	private EntityViewPageHelper entityViewPageHelper;

	@PostRefresh
	public void setEntityViewPageHelper( EntityViewPageHelper entityViewPageHelper ) {
		this.entityViewPageHelper = entityViewPageHelper;
	}

	@Override
	protected String extensionName() {
		return CONTROLLER_EXTENSION;
	}

	@Override
	protected AclPermissionsFormController createExtension( EntityViewRequest entityViewRequest,
	                                                        EntityViewCommand entityViewCommand,
	                                                        WebDataBinder webDataBinder ) {
		EntityConfiguration entityConfiguration = entityViewRequest.getEntityViewContext().getEntityConfiguration();
		AclPermissionsForm permissionsForm = permissionsFormRegistry
				.getForEntityConfiguration( entityConfiguration )
				.orElseThrow( () -> new IllegalStateException( "No ACL permissions form registered for " + entityConfiguration.getName() ) );

		AclPermissionsForm adaptedForm = adaptAclPermissionsForm( permissionsForm );

		MutableAcl acl = retrieveAcl( adaptedForm, entityConfiguration,
		                              entityViewRequest.getEntityViewContext().getEntity() );
		AclOperations aclOperations = aclSecurityService.createAclOperations( acl );

		return new AclPermissionsFormController( aclOperations, new AclPermissionsFormData( adaptedForm ) );
	}

	@SuppressWarnings("unused")
	protected MutableAcl retrieveAcl( AclPermissionsForm permissionsForm,
	                                  EntityConfiguration entityConfiguration,
	                                  Object entity ) {
		ObjectIdentity objectIdentity = createObjectIdentity( entityConfiguration, entity );
		MutableAcl acl = aclSecurityService.getAcl( objectIdentity );

		if ( acl == null ) {
			acl = aclSecurityService.createAclWithParent( objectIdentity, null );
		}

		return acl;
	}

	@SuppressWarnings("unchecked")
	protected ObjectIdentity createObjectIdentity( EntityConfiguration entityConfiguration, Object entity ) {
		Function<Object, ObjectIdentity> identityResolver
				= (Function<Object, ObjectIdentity>) entityConfiguration.getAttribute( ObjectIdentity.class.getName(),
				                                                                       Function.class );

		return identityResolver != null ? identityResolver.apply( entity ) : AclUtils.objectIdentity( entity );
	}

	@Override
	protected void registerWebResources( EntityViewRequest entityViewRequest,
	                                     EntityView entityView,
	                                     WebResourceRegistry webResourceRegistry ) {
		webResourceRegistry.addWithKey(
				WebResource.JAVASCRIPT_PAGE_END, AclPermissionsFormViewProcessor.class.getName(),
				"/static/SpringSecurityAclModule/js/acl-permissions-form-controller.js",
				WebResource.VIEWS
		);
		webResourceRegistry.addWithKey(
				WebResource.CSS, AclPermissionsFormViewProcessor.class.getName(),
				"/static/SpringSecurityAclModule/css/acl-permissions-form-controller.css",
				WebResource.VIEWS
		);
	}

	@Override
	protected void doPost( AclPermissionsFormController controller, BindingResult bindingResult, EntityView entityView, EntityViewRequest entityViewRequest ) {
		MutableAcl acl = controller.updateAclWithModel();
		aclSecurityService.updateAcl( acl );

		entityViewPageHelper.addGlobalFeedbackAfterRedirect( entityViewRequest, Style.SUCCESS, "feedback.permissionsUpdated" );

		EntityViewContext entityViewContext = entityViewRequest.getEntityViewContext();
		entityView.setRedirectUrl(
				UriComponentsBuilder.fromUriString( entityViewContext.getLinkBuilder().update( entityViewContext.getEntity() ) )
				                    .queryParam( "view", entityViewRequest.getViewName() )
				                    .toUriString()
		);
	}

	@Override
	protected void render( AclPermissionsFormController controller,
	                       EntityViewRequest entityViewRequest,
	                       EntityView entityView,
	                       ContainerViewElementBuilderSupport<?, ?> containerBuilder,
	                       ViewElementBuilderMap builderMap,
	                       ViewElementBuilderContext builderContext ) {
		// create the view element builder
		AclPermissionsFormViewElementBuilder elementBuilder
				= new AclPermissionsFormViewElementBuilder( controller.getFormData(), controller.getAclOperations(), permissionFactory );
		elementBuilder.setControlPrefix( controlPrefix() + ".model" );

		// add the view element builder to the form - delete default row with 2 columns (?)
		builderMap.get( "entityForm", FormViewElementBuilder.class )
		          .css( "acl-permissions-form" )
		          .addFirst( elementBuilder );
	}

	@Override
	protected void postRender( AclPermissionsFormController extension,
	                           EntityViewRequest entityViewRequest,
	                           EntityView entityView,
	                           ContainerViewElement container,
	                           ViewElementBuilderContext builderContext ) {
		container.find( "btn-cancel", ButtonViewElement.class )
		         .ifPresent( button -> {
			         EntityViewContext entityViewContext = entityViewRequest.getEntityViewContext();
			         button.setUrl( entityViewContext.getLinkBuilder().update( entityViewContext.getEntity() ) );
		         } );
	}

	/**
	 * Adapts the configured permissions form by creating a new instance with default methods where
	 * the configured version has missing values and defaults are possible.
	 * <p/>
	 * This also validates the correctness of the configured form.
	 *
	 * @param permissionsForm original configured form
	 * @return adapted version - should not have any missing properties
	 */
	protected AclPermissionsForm adaptAclPermissionsForm( AclPermissionsForm permissionsForm ) {
		AclPermissionsForm.AclPermissionsFormBuilder adaptedForm = permissionsForm.toBuilder();

		adaptedForm.clearSections();
		permissionsForm.getSections()
		               .stream()
		               .map( this::adaptAclPermissionsFormSection )
		               .forEach( adaptedForm::section );

		return adaptedForm.build();
	}

	protected AclPermissionsFormSection adaptAclPermissionsFormSection( AclPermissionsFormSection section ) {
		return entitySectionAdapter.adapt( section );
	}
}
