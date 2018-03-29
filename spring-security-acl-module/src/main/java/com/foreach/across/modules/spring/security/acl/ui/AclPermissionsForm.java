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

import lombok.*;

import java.util.List;

/**
 * Represents an ACL permissions form to be rendered for an entity.
 * A form consists out of one or more {@link AclPermissionsFormSection}.
 *
 * @author Arne Vandamme
 * @see AclPermissionsFormRegistry
 * @see AclPermissionsFormViewProcessor
 * @since 3.0.0
 */
@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AclPermissionsForm
{
	/**
	 * Name of the {@link com.foreach.across.modules.entity.registry.EntityConfiguration} attribute that contains the name
	 * of the form profile to use for the ACL permissions form.
	 */
	public static final String ATTR_PROFILE_NAME = AclPermissionsFormRegistry.ATTR_ACL_PROFILE;

	/**
	 * Default menu path for the menu item created if an permissions form view is present.
	 */
	public static final String MENU_PATH = "/aclPermissions";

	/**
	 * Name of the ACL permissions view. Any {@code EntityConfiguration} with this view present will
	 * automatically get a menu item added to that view.
	 */
	public static final String VIEW_NAME = "aclPermissions";

	/**
	 * Name of this form, used as a key in the message codes.
	 */
	@Builder.Default
	private String name = "default";

	/**
	 * Sections in this form.
	 */
	@Singular
	@NonNull
	private List<AclPermissionsFormSection> sections;

	/**
	 * Get the section with the name.
	 * Will return {@code null} if not found.
	 *
	 * @param name of the section
	 * @return section or null
	 */
	public AclPermissionsFormSection getSectionWithName( @NonNull String name ) {
		return sections.stream().filter( section -> name.equals( section.getName() ) ).findFirst().orElse( null );
	}

	/**
	 * @param sectionName name of the section
	 * @return a builder for an {@link AclPermissionsFormSection}
	 */
	public static AclPermissionsFormSection.AclPermissionsFormSectionBuilder section( String sectionName ) {
		return AclPermissionsFormSection.builder().name( sectionName );
	}

	/**
	 * @return a builder for a {@link AclPermissionsFormPermissionGroup}
	 */
	public static AclPermissionsFormPermissionGroup.AclPermissionsFormPermissionGroupBuilder permissionGroup( String groupName ) {
		return AclPermissionsFormPermissionGroup.builder().name( groupName );
	}

	/**
	 * @return a builder for creating a custom new item selector control, to be attached to a {@link AclPermissionsFormSection}
	 */
	public static AclPermissionsFormItemSelectorControl selectorControl() {
		return new AclPermissionsFormItemSelectorControl();
	}
}
