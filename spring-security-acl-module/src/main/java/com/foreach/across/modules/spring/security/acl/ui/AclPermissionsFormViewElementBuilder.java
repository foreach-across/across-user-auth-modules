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
import com.foreach.across.modules.bootstrapui.elements.GlyphIcon;
import com.foreach.across.modules.bootstrapui.elements.Style;
import com.foreach.across.modules.entity.util.EntityUtils;
import com.foreach.across.modules.spring.security.acl.services.AclOperations;
import com.foreach.across.modules.spring.security.acl.services.AclPermissionFactory;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementBuilder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders.*;

/**
 * Responsible for rendering an ACL permissions form.
 * <p/>
 * This implementation is not thread-safe and an instance should not be re-used.
 * A new instance is usually created in the scope of a single {@link AclPermissionsFormViewProcessor}.
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
@NotThreadSafe
@RequiredArgsConstructor
final class AclPermissionsFormViewElementBuilder implements ViewElementBuilder<ContainerViewElement>
{
	private final AclPermissionsForm permissionsForm;
	private final AclOperations aclOperations;
	private final AclPermissionFactory permissionFactory;

	@Setter
	private String formName = "default";

	@Setter
	@NonNull
	private String controlPrefix = "aclPermissionsFormController.model";

	/**
	 * Optionally set a cache instance that should be used (and updated),
	 * for faster retrieval of the target object of an {@link Sid}.
	 */
	@Setter
	private Map<Sid, Object> sidCache = new HashMap<>();

	private transient ViewElementBuilderContext builderContext;
	private transient String formPrefix, sectionPrefix;
	private transient Map<AclPermissionsFormSection, List<Sid>> sidsForSection;
	private transient Map<Sid, List<AccessControlEntry>> acesForSid;
	private transient AtomicInteger aceCounter;

	@Override
	public ContainerViewElement build( ViewElementBuilderContext viewElementBuilderContext ) {
		this.formPrefix = "aclForm[" + formName + "]";
		this.builderContext = viewElementBuilderContext;
		this.aceCounter = new AtomicInteger();

		buildSidsForSections();

		NodeViewElementBuilder container = BootstrapUiBuilders.div();
		permissionsForm.getSections().stream().map( this::buildSection ).forEach( container::add );

		return container.build();
	}

	private void buildSidsForSections() {
		MutableAcl acl = aclOperations.getAcl();

		acesForSid = acl.getEntries()
		                .stream()
		                .collect( Collectors.groupingBy( AccessControlEntry::getSid ) );

		sidsForSection = new HashMap<>();

		// for every ace, add it to the different sections where it matches
		permissionsForm.getSections()
		               .forEach( section ->
				                         acesForSid.forEach( ( sid, aces ) -> {
					                         Permission[] permissions = section.getPermissionsSupplier().get();

					                         if ( aces.stream()
					                                  .map( AccessControlEntry::getPermission )
					                                  .anyMatch( p -> ArrayUtils.contains( permissions, p ) ) ) {
						                         Object target = retrieveObjectForSid( section, sid );
						                         if ( target != null ) {
							                         // if section matches for that Sid type!
							                         sidsForSection.computeIfAbsent( section, s -> new ArrayList<>() ).add( sid );
						                         }
					                         }
				                         } )
		               );
	}

	private Object retrieveObjectForSid( AclPermissionsFormSection section, Sid sid ) {
		return sidCache.computeIfAbsent( sid, s -> section.getObjectForSidResolver().apply( s ) );
	}

	private ViewElementBuilder buildSection( AclPermissionsFormSection section ) {
		this.sectionPrefix = formPrefix + ".section[" + section.getName() + "]";

		NodeViewElementBuilder panel = BootstrapUiBuilders.div().css( "acl-permissions-form-section" );

		// section heading
		panel.add( node( "h3" ).add( html( message( sectionPrefix + ".title", section.getName() ) ) ) );
		String description = message( sectionPrefix + ".description" );
		if ( !description.isEmpty() ) {
			panel.add( paragraph().add( html( description ) ) );
		}

		panel.add( buildPermissionsTable( section ) );

		ViewElementBuilder selectorBuilder = section.getItemSelectorBuilder();

		if ( selectorBuilder != null ) {
			panel.add( selectorBuilder );
		}

		return panel;
	}

	private ViewElementBuilder buildPermissionsTable( AclPermissionsFormSection section ) {
		val table = table().style( Style.Table.STRIPED, Style.Table.HOVER );

		// heading row
		val headerRow = tableRow().add( tableHeaderCell() );

		Permission[] permissions = section.getPermissionsSupplier().get();
		Stream.of( permissions )
		      .map( permissionFactory::getNameForPermission )
		      .map( this::resolvePermissionLabel )
		      .forEach( label -> headerRow.add( table.heading().add( html( label ) ) ) );
		table.header().add( headerRow );

		// members
		List<Sid> sids = sidsForSection.get( section );

		if ( sids != null ) {
			sids.forEach( sid -> {
				int index = aceCounter.incrementAndGet();
				Object target = retrieveObjectForSid( section, sid );

				if ( target != null && section.getSidMatcher().test( sid, target ) ) {
					Serializable transportId = section.getTransportIdForObjectResolver().apply( target );

					val row = table.row()
					               .add( table.cell().add( section.getObjectLabelViewElementProvider().apply( target, builderContext ) ) )
					               .add( hidden().controlName( controlPrefix + "['" + index + "'].section" ).value( section.getName() ) )
					               .add( hidden().controlName( controlPrefix + "['" + index + "'].id" ).value( transportId ) );

					Stream.of( permissions )
					      .forEach( aclPermission -> row
							      .add( table.cell().add(
									      checkbox().controlName( controlPrefix + "['" + index + "'].permissions" )
									                .unwrapped()
									                .value( aclPermission.getMask() )
									                .selected( aclOperations.getAce( sid, aclPermission ).isPresent() )
							      ) ) );

					table.body().add( row );
				}
			} );
		}

		// template row
		val templateRow = table.row()
		                       .css( "hidden" )
		                       .add( table.cell().add( text( "{{item}}" ) ) )
		                       .add( hidden().disabled().controlName( controlPrefix + "['{{itemIndex}}'].section" ).value( section.getName() ) )
		                       .add( hidden().disabled().controlName( controlPrefix + "['{{itemIndex}}'].id" ).value( "" ) );
		Stream.of( permissions )
		      .forEach( aclPermission -> templateRow
				      .add( table.cell().add(
						      checkbox().disabled()
						                .controlName( controlPrefix + "['{{itemIndex}}'].permissions" )
						                .unwrapped()
						                .value( aclPermission.getMask() )
						                .selected( false )
				      ) ) );
		table.body().add( templateRow );

		return table;
	}

	private String resolvePermissionLabel( String permissionName ) {
		String permissionPrefix = "aclPermission[" + permissionName + "]";

		String label = message( sectionPrefix + "." + permissionPrefix, "" );

		if ( StringUtils.isEmpty( label ) ) {
			label = message( formPrefix + "." + permissionPrefix, "" );
		}

		return StringUtils.isEmpty( label ) ? message( permissionPrefix, EntityUtils.generateDisplayName( permissionName ) ) : label;
	}

	private ViewElementBuilder buildNewItemSelector( AclPermissionsFormSection section ) {
		return div()
				.add(
						inputGroup( BootstrapUiBuilders.textbox().controlName( "selector" ) )
								.addonAfter( BootstrapUiBuilders.button().iconOnly( new GlyphIcon( GlyphIcon.PLUS_SIGN ) ) )
				);
	}

	private String message( String code ) {
		return builderContext.getMessage( code, "" );
	}

	private String message( String code, String defaultValue ) {
		return builderContext.getMessage( code, defaultValue );
	}
}
