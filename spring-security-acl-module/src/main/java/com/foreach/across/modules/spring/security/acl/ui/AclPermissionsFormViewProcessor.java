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
import com.foreach.across.modules.bootstrapui.elements.builder.FormViewElementBuilder;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.EntityViewProcessor;
import com.foreach.across.modules.entity.views.processors.EntityViewProcessorAdapter;
import com.foreach.across.modules.entity.views.processors.support.ViewElementBuilderMap;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.spring.security.acl.business.AclPermission;
import com.foreach.across.modules.spring.security.acl.services.AclOperations;
import com.foreach.across.modules.spring.security.acl.services.AclPermissionFactory;
import com.foreach.across.modules.spring.security.acl.services.AclSecurityService;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.builder.ContainerViewElementBuilderSupport;
import lombok.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Sid;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders.*;
import static com.foreach.across.modules.web.ui.elements.TextViewElement.text;

/**
 * View processor for an ACL permissions form.
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
@ConditionalOnAcrossModule(EntityModule.NAME)
@ConditionalOnClass(EntityViewProcessor.class)
@Component
@RequiredArgsConstructor
public class AclPermissionsFormViewProcessor extends EntityViewProcessorAdapter
{
	/**
	 * Name of the ACL permissions view. Any {@code EntityConfiguration} with this view present will
	 * automatically get a menu item added to that view.
	 */
	public static final String VIEW_NAME = "aclPermissions";

	private static final String EXTENSION = "aclPermissionController";

	private final ObjectProvider<EntityRegistry> entityRegistry;
	private final AclSecurityService aclSecurityService;
	//private final EntityViewElementBuilderService elementBuilderService;

	private final AclPermissionFactory permissionFactory;
	private final AclPermissionsFormRegistry permissionsFormRegistry;

	@Override
	public void initializeCommandObject( EntityViewRequest entityViewRequest, EntityViewCommand command, WebDataBinder dataBinder ) {
		// retrieve the form for the entity
		EntityConfiguration entityConfiguration = entityViewRequest.getEntityViewContext().getEntityConfiguration();
		Optional<AclPermissionsForm> formHolder = permissionsFormRegistry.getForEntityConfiguration( entityConfiguration );

		// retrieve the acl and create the acl operations

		// create the controller and register it as an extension

		// TODO: custom object identity
		// TODO: default parent on creation (what to do?)
		// TODO: avoid auto-creation, make it an extra step

		//new AclPermissionsFormController( aclOperations, permissionsForm )

		/*
		Persistable entity = entityViewRequest.getEntityViewContext().getEntity( Persistable.class );
		ObjectIdentity identity = AclUtils.objectIdentity( entity );
		MutableAcl acl = aclSecurityService.getAcl( identity );

		if ( acl == null ) {
			acl = aclSecurityService.createAclWithParent( identity, null );
		}

		command.addExtension( "aclPermissions", new AclPermissionEntries( acl ) );*/
	}

	protected ObjectIdentity createObjectIdentity( EntityConfiguration entityConfiguration, Object entity ) {
		return null;
	}

	@Override
	protected void doPost( EntityViewRequest entityViewRequest, EntityView entityView, EntityViewCommand command, BindingResult bindingResult ) {

		// fetch the controller
		// MutableAcl acl = controller.updateAclWithModel();
		// save the acl
		// set feedback message and ensure not redirected

		AclPermissionEntries entries = command.getExtension( "aclPermissions" );
		MutableAcl acl = entries.getAcl();

		// sid, permissions, granted, denied
		AclOperations operations = aclSecurityService.createAclOperations( acl );

		entries.getEntries()
		       .values()
		       .forEach( entry -> {
			       Sid sid = new PrincipalSid( entry.getId() );
			       operations.apply(
					       sid,
					       new AclPermission[] { AclPermission.CREATE, AclPermission.READ, AclPermission.WRITE, AclPermission.DELETE,
					                             AclPermission.ADMINISTRATION },
					       entry.getPermissions()
			       );
		       } );

		aclSecurityService.updateAcl( acl );

		entityView.setRedirectUrl( null );
	}

	@Override
	protected void render( EntityViewRequest entityViewRequest,
	                       EntityView entityView,
	                       ContainerViewElementBuilderSupport<?, ?> containerBuilder,
	                       ViewElementBuilderMap builderMap,
	                       ViewElementBuilderContext builderContext ) {

		// retrieve the controller
		// create the view element builder
		// add the view element builder to the form

		AclPermissionEntries entries = entityViewRequest.getCommand().getExtension( "aclPermissions" );

		AclPermission[] permissions =
				new AclPermission[] { AclPermission.CREATE, AclPermission.READ, AclPermission.WRITE, AclPermission.DELETE, AclPermission.ADMINISTRATION };

		val table = table();

		val headerRow = tableRow().add( tableHeaderCell() );
		Stream.of( permissions )
		      .forEach( p -> headerRow.add( table.heading().add( text( p.getPattern() ) ) ) );
		table.header().add( headerRow );

		String prefix = "extensions[aclPermissions].entries";

		AtomicInteger counter = new AtomicInteger();
		AclOperations operations = aclSecurityService.createAclOperations( entries.getAcl() );

		Stream.of( "anonymous", "registered user" )
		      .forEach(
				      principal -> {
					      int index = counter.incrementAndGet();
					      Sid sid = new PrincipalSid( principal );
					      val row = table.row()
					                     .add( table.cell().add( text( principal ) ) )
					                     .add( hidden().controlName( prefix + "['" + index + "'].type" ).value( "principal" ) )
					                     .add( hidden().controlName( prefix + "['" + index + "'].id" ).value( principal ) );

					      Stream.of( permissions )
					            .forEach( aclPermission -> row
							            .add( table.cell().add(
									            checkbox().controlName( prefix + "['" + index + "'].permissions" )
									                      .unwrapped()
									                      .value( aclPermission.getMask() )
									                      .selected( operations.getAce( sid, aclPermission ).isPresent() )
							            ) ) );

					      table.body().add( row );
				      }
		      );

		builderMap.get( "entityForm", FormViewElementBuilder.class )
		          .addFirst( table );
	}

	@RequiredArgsConstructor
	static class AclPermissionEntries
	{
		@Getter
		private final Map<Integer, AclPermissionEntry> entries = new HashMap<>();

		@Getter
		private final MutableAcl acl;

		AclPermissionEntry createEntry() {
			AclPermissionEntry entry = new AclPermissionEntry();
			entries.put( entries.size() + 1, entry );
			return entry;
		}

		int size() {
			return entries.size();
		}
	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class AclPermissionEntry
	{
		private String type;
		private String id;
		private int[] permissions;
	}
}
