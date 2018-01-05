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

import com.foreach.across.core.annotations.Event;
import com.foreach.across.modules.adminweb.menu.EntityAdminMenuEvent;
import com.foreach.across.modules.bootstrapui.elements.builder.FormViewElementBuilder;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.processors.EntityViewProcessorAdapter;
import com.foreach.across.modules.entity.views.processors.support.ViewElementBuilderMap;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.web.EntityLinkBuilder;
import com.foreach.across.modules.hibernate.business.IdBasedEntity;
import com.foreach.across.modules.spring.security.acl.business.AclPermission;
import com.foreach.across.modules.spring.security.acl.services.AclOperations;
import com.foreach.across.modules.spring.security.acl.services.AclSecurityService;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.builder.ContainerViewElementBuilderSupport;
import lombok.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.Sid;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders.*;
import static com.foreach.across.modules.web.ui.elements.TextViewElement.text;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
@Component
@RequiredArgsConstructor
public class EntityAclPermissionsViewProcessor extends EntityViewProcessorAdapter
{
	private final ObjectProvider<EntityRegistry> entityRegistry;
	private final AclSecurityService aclSecurityService;

	@Event
	public void registerTab( EntityAdminMenuEvent entityAdminMenu ) {
		final EntityConfiguration entityConfiguration = entityRegistry.getObject().getEntityConfiguration(
				entityAdminMenu.getEntityType() );
		if ( entityAdminMenu.isForUpdate() && entityConfiguration.hasView( "aclPermissions" ) ) {

			EntityLinkBuilder linkBuilder = entityConfiguration.getAttribute( EntityLinkBuilder.class );

			entityAdminMenu.builder().item( "/aclPermissions", "Permissions",
			                                UriComponentsBuilder.fromUriString(
					                                linkBuilder.update( entityAdminMenu.getEntity() ) )
			                                                    .queryParam( "view", "aclPermissions" )
			                                                    .toUriString() );
		}
	}

	@Override
	public void initializeCommandObject( EntityViewRequest entityViewRequest, EntityViewCommand command, WebDataBinder dataBinder ) {
		IdBasedEntity entity = entityViewRequest.getEntityViewContext().getEntity( IdBasedEntity.class );
		MutableAcl acl = aclSecurityService.getAcl( entity );

		if ( acl == null ) {
			acl = aclSecurityService.createAclWithParent( entity, null );
		}

		command.addExtension( "aclPermissions", new AclPermissionEntries( acl ) );
	}

	@Override
	protected void doPost( EntityViewRequest entityViewRequest, EntityView entityView, EntityViewCommand command, BindingResult bindingResult ) {
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
