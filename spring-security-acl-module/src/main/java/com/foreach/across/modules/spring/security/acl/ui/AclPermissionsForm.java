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
	 * Name of this form, used as a key in the message codes.
	 */
	private String name;

	/**
	 * Path of the menu item that should be created for this form.
	 */
	@NonNull
	private String menuPath;

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
	 * @return a builder for an {@link AclPermissionsFormSection}
	 */
	public static AclPermissionsFormSection.AclPermissionsFormSectionBuilder section() {
		return AclPermissionsFormSection.builder();
	}

	/**
	 * @return a builder for creating a custom new item selector control, to be attached to a {@link AclPermissionsFormSection}
	 */
	public static AclPermissionsFormItemSelectorControl selectorControl() {
		return new AclPermissionsFormItemSelectorControl();
	}

	@SuppressWarnings("all")
	public static class AclPermissionsFormBuilder
	{
		private String menuPath = "/aclPermissions";
	}
}
