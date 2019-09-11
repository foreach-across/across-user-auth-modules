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

import com.foreach.across.modules.bootstrapui.elements.FormGroupElement;
import com.foreach.across.modules.bootstrapui.elements.builder.TableViewElementBuilder;
import com.foreach.across.modules.spring.security.acl.services.AclOperations;
import com.foreach.across.modules.spring.security.acl.services.AclPermissionFactory;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
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

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements.bootstrap;
import static com.foreach.across.modules.entity.util.EntityUtils.generateDisplayName;
import static com.foreach.across.modules.spring.security.acl.config.icons.SpringSecurityAclModuleIcons.springSecurityAclIcons;
import static com.foreach.across.modules.web.ui.MutableViewElement.Functions.witherFor;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;
import static com.foreach.across.modules.web.ui.elements.TextViewElement.html;

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
	private final static String CSS_SECTION = "acl-permissions-form-section";
	private final static String CSS_GROUP_HEADER = "acl-permissions-form-group-header-row";
	private final static String CSS_PERMISSION_HEADER = "acl-permissions-form-permission-header-row";
	private final static String CSS_ITEM_ROW = "acl-permissions-form-item-row";
	private final static String CSS_FIRST_OF_GROUP = "acl-permissions-form-first-of-group";
	private final static String CSS_PERMISSION_TOGGLE = "acl-permissions-form-permission-toggle";
	private final static String CSS_REMOVE_CELL = "acl-permissions-form-remove-item";
	private final static String CSS_NO_ENTRIES = "acl-permissions-form-no-entries-row";
	private final static String CSS_TEMPLATE_ROW = "acl-permissions-form-template-row";

	private final AclPermissionsFormData formData;
	private final AclOperations aclOperations;
	private final AclPermissionFactory permissionFactory;

	@Setter
	@NonNull
	private String controlPrefix = "aclPermissionsFormController.model";

	private transient ViewElementBuilderContext builderContext;
	private transient String formPrefix, formSectionPrefix, sectionPrefix;
	private transient Map<AclPermissionsFormSection, List<Sid>> sidsForSection;
	private transient Map<Sid, List<AccessControlEntry>> acesForSid;
	private transient AtomicInteger aceCounter;

	@Override
	public ContainerViewElement build( ViewElementBuilderContext viewElementBuilderContext ) {
		this.formPrefix = "aclForm[" + formData.getPermissionsForm().getName() + "]";
		this.builderContext = viewElementBuilderContext;
		this.aceCounter = new AtomicInteger();

		buildSidsForSections();

		NodeViewElementBuilder container = html.builders.div();
		formData.getSections().stream().map( this::buildSection ).forEach( container::add );

		return container.build();
	}

	private void buildSidsForSections() {
		MutableAcl acl = aclOperations.getAcl();

		acesForSid = acl.getEntries()
		                .stream()
		                .collect( Collectors.groupingBy( AccessControlEntry::getSid ) );

		sidsForSection = new HashMap<>();

		// for every ace, add it to the different sections where it matches
		formData.getSections()
		        .forEach( section ->
				                  acesForSid.forEach( ( sid, aces ) -> {
					                  Permission[] permissions = formData.getPermissionsForSection( section );

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
		return formData.getSidCache().computeIfAbsent( sid, s -> section.getObjectForSidResolver().apply( s ) );
	}

	@SuppressWarnings("unchecked")
	private ViewElementBuilder buildSection( AclPermissionsFormSection section ) {
		this.sectionPrefix = "aclSection[" + section.getName() + "]";
		this.formSectionPrefix = formPrefix + "." + sectionPrefix;

		// section heading
		String description = resolveLabel( "description", "", formSectionPrefix, sectionPrefix );
		ViewElementBuilder itemSelectorBuilder = section.getItemSelectorBuilder();
		if ( itemSelectorBuilder != null ) {
			itemSelectorBuilder = itemSelectorBuilder.andThen( ( ctx, element ) -> {
				if ( element instanceof NodeViewElement ) {
					element.set( css.display.inlineBlock );
					element.set( witherFor( NodeViewElement.class, this::makeFormGroupInlineBlock ) );
				}
			} );

		}

		return html.builders
				.div()
				.css( CSS_SECTION )
				.add( html.builders.h3().add( html( resolveLabel( "title", section.getName(), formSectionPrefix, sectionPrefix ) ) ) )
				.add( !description.isEmpty() ? html.builders.p().add( html( description ) ) : null )
				.add( buildPermissionsTable( section ) )
				.add( itemSelectorBuilder );
	}

	private void makeFormGroupInlineBlock( NodeViewElement selector ) {
		selector.findAll( e -> e instanceof FormGroupElement )
		        .findFirst()
		        .ifPresent( formGroup -> formGroup.set( css.display.inlineBlock ) );
	}

	private ViewElementBuilder buildPermissionsTable( AclPermissionsFormSection section ) {
		val table = bootstrap.builders.table( css.table.striped, css.table.hover );

		// heading row
		boolean showPermissionGroupRow = false;
		val permissionGroupRow = bootstrap.builders.table.row().css( CSS_GROUP_HEADER ).add(
				bootstrap.builders.table.headerCell() );
		val permissionRow = bootstrap.builders.table.row().css( CSS_PERMISSION_HEADER ).add(
				bootstrap.builders.table.headerCell() );

		val permissionsGroupsForSection = formData.getPermissionGroupsForSection( section );

		for ( val entry : permissionsGroupsForSection.entrySet() ) {
			String groupName = StringUtils.defaultString( entry.getKey().getName() );
			boolean hasGroupName = !StringUtils.isEmpty( groupName );
			Permission[] permissions = entry.getValue();

			if ( hasGroupName ) {
				showPermissionGroupRow = true;
			}

			permissionGroupRow.add(
					table.heading()
					     .css( CSS_FIRST_OF_GROUP )
					     .with( css.text.center )
					     .columnSpan( permissions.length )
					     .add( hasGroupName ? html( resolvePermissionCode( "permissionGroup[" + groupName + "]", generateDisplayName( groupName ) ) ) : null )
					     .add( hasGroupName ? tooltip( resolvePermissionCode( "permissionGroup[" + groupName + "].tooltip", "" ) ) : null )
			);

			AtomicInteger itemCount = new AtomicInteger( 0 );
			Stream.of( permissions )
			      .map( permissionFactory::getNameForPermission )
			      .map( permissionName ->
					            table.heading()
					                 .css( itemCount.getAndDecrement() == 0 ? CSS_FIRST_OF_GROUP : "" )
					                 .with( css.text.center )
					                 .add( html( resolvePermissionCode( "permission[" + permissionName + "]", generateDisplayName( permissionName ) ) ) )
					                 .add( tooltip( resolvePermissionCode( "permission[" + permissionName + "].tooltip", "" ) ) )
			      )
			      .forEach( permissionRow::add );
		}

		if ( showPermissionGroupRow ) {
			table.header().add( permissionGroupRow.add( table.heading().css( CSS_REMOVE_CELL ) ) );
		}

		table.header().add( permissionRow.add( table.heading().css( CSS_REMOVE_CELL ) ) );

		// members
		List<Sid> sids = sidsForSection.get( section );
		AtomicInteger rowCount = new AtomicInteger( 0 );

		if ( sids != null ) {
			sids.forEach( sid -> {
				int index = aceCounter.incrementAndGet();
				Object target = retrieveObjectForSid( section, sid );

				if ( target != null && section.getSidMatcher().test( sid, target ) ) {
					Serializable transportId = section.getTransportIdForObjectResolver().apply( target );

					val row = table.row()
					               .css( CSS_ITEM_ROW )
					               .add( table.cell().add( section.getObjectLabelViewElementProvider().apply( target, builderContext ) ) )
					               .add( bootstrap.builders.hidden().controlName( controlPrefix + "['" + index + "'].section" )
					                                       .value( section.getName() ) )
					               .add( bootstrap.builders.hidden().controlName( controlPrefix + "['" + index + "'].id" )
					                                       .value( transportId ) );

					for ( val permissions : permissionsGroupsForSection.values() ) {
						AtomicInteger itemCount = new AtomicInteger( 0 );
						Stream.of( permissions )
						      .forEach( aclPermission -> row
								      .add( table.cell()
								                 .with( css.text.center )
								                 .css( CSS_PERMISSION_TOGGLE, itemCount.getAndDecrement() == 0 ? CSS_FIRST_OF_GROUP : "" )
								                 .add(
										                 bootstrap.builders.checkbox().controlName( controlPrefix + "['" + index + "'].permissions" )
										                                   .unwrapped()
										                                   .value( aclPermission.getMask() )
										                                   .selected( aclOperations.getAce( sid, aclPermission ).isPresent() )
								                 ) ) );

					}

					rowCount.getAndIncrement();
					table.body().add( row.add( removeItemCell() ) );
				}
			} );
		}

		// no entries row
		table.body().add(
				table.row()
				     .css( CSS_NO_ENTRIES, rowCount.get() > 0 ? "d-none" : "" )
				     .add(
						     table.cell().columnSpan( formData.getPermissionsForSection( section ).length + 2 )
						          .add( html( resolvePermissionCode( "noEntries", "No entries yet" ) ) )
				     )
		);

		// template row
		val templateRow = table.row()
		                       .css( CSS_TEMPLATE_ROW, CSS_ITEM_ROW, "d-none" )
		                       .add( table.cell().add( html.builders.text( "{{item}}" ) ) )
		                       .add( bootstrap.builders.hidden().disabled().controlName( controlPrefix + "['{{itemIndex}}'].section" )
		                                               .value( section.getName() ) )
		                       .add( bootstrap.builders.hidden().disabled().controlName( controlPrefix + "['{{itemIndex}}'].id" ).value( "" ) );

		for ( val permissions : permissionsGroupsForSection.values() ) {
			AtomicInteger itemCount = new AtomicInteger( 0 );
			Stream.of( permissions )
			      .forEach( aclPermission -> templateRow
					      .add( table.cell()
					                 .css( CSS_PERMISSION_TOGGLE, itemCount.getAndDecrement() == 0 ? CSS_FIRST_OF_GROUP : "" )
					                 .with( css.text.center )
					                 .add(
							                 bootstrap.builders.checkbox().disabled()
							                                   .controlName( controlPrefix + "['{{itemIndex}}'].permissions" )
							                                   .unwrapped()
							                                   .value( aclPermission.getMask() )
							                                   .selected( false )
					                 ) ) );
		}

		table.body().add( templateRow.add( removeItemCell() ) );

		return table;
	}

	private TableViewElementBuilder.Cell removeItemCell() {
		return bootstrap.builders.table
				.cell()
				.css( CSS_REMOVE_CELL )
				.add(
						bootstrap.builders.link()
						                  .url( "#" )
						                  .title( resolvePermissionCode( "removeEntry", "Remove" ) )
						                  .add( springSecurityAclIcons.permission.remove() )
				);

	}

	private NodeViewElementBuilder tooltip( String tooltip ) {
		if ( !StringUtils.isEmpty( tooltip ) ) {
			return html.builders.a()
			                    .css( "tooltip-link", "text-muted" )
			                    .attribute( "title", tooltip )
			                    .attribute( "data-html", true )
			                    .attribute( "data-toggle", "tooltip" )
			                    .add( springSecurityAclIcons.permission.tooltip() );
		}

		return null;
	}

	private String resolvePermissionCode( String code, String defaultValue ) {
		return resolveLabel( code, defaultValue, formSectionPrefix, sectionPrefix, formPrefix, "" );
	}

	private String resolveLabel( String messageCode, String defaultValue, String... prefixList ) {
		String label;
		for ( String prefix : prefixList ) {
			if ( !StringUtils.isEmpty( prefix ) ) {
				label = message( prefix + "." + messageCode, StringUtils.LF );
			}
			else {
				label = message( messageCode, StringUtils.LF );
			}

			if ( !StringUtils.LF.equals( label ) ) {
				return label;
			}
		}

		return defaultValue;
	}

	private String message( String code, String defaultValue ) {
		return builderContext.getMessage( code, defaultValue );
	}
}
