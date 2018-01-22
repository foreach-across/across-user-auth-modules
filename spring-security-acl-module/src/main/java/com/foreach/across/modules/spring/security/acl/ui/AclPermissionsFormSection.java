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

import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import lombok.*;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;

import java.io.Serializable;
import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a single section of an ACL permissionsSupplier form for an entity.
 * Requires a unique name and a permissions supplier.
 * <p/>
 * Allows for full customization of every element of a section, but most can be
 * left empty and will get default values when executed by the {@link AclPermissionsFormViewProcessor}.
 *
 * @author Arne Vandamme
 * @see AclPermissionsForm
 * @since 3.0.0
 */
@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AclPermissionsFormSection
{
	/**
	 * Name of this section, used as a key in message codes.
	 */
	@NonNull
	private final String name;

	/**
	 * The type of the entity that this section manages. When using the {@link com.foreach.across.modules.entity.registry.EntityRegistry},
	 * if the entity type is fully managed you can simply set the entity type and omit most of the other properties.
	 * <p/>
	 * The other properties will be automatically determined by the view processor.
	 */
	private final Class<?> entityType;

	/**
	 * Supplier that will return the set of {@link Permission} that should be selectable on this section.
	 * Note that all permissionsSupplier must be registered with the {@link com.foreach.across.modules.spring.security.acl.services.AclPermissionFactory}
	 * to be able to select them.
	 */
	private final Supplier<Permission[]> permissionsSupplier;

	/**
	 * Function that resolves an {@link Sid} to the actual object.
	 * Note that there is no absolute guarantee that the resolver will only be called with {@link Sid} values it can handle.
	 * <p/>
	 * If left empty, a default resolver will be created.
	 */
	private final Function<Sid, Object> objectForSidResolver;

	/**
	 * Function that will generate the corresponding {@link Sid} for a particular object.
	 * <p/>
	 * If left empty, a default resolver will be created.
	 */
	private final Function<Object, Sid> sidForObjectResolver;

	/**
	 * Function that generates a transport id for a resolved target object.
	 * The transport id is used as the actual form field value.
	 * <p/>
	 * If left empty, a default resolver will be created.
	 */
	private Function<Object, Serializable> transportIdForObjectResolver;

	/**
	 * Function that resolves the target object for a transport id.
	 * The transport id is used as the actual form field value.
	 * <p/>
	 * If left empty, a default resolver will be created.
	 */
	private final Function<String, Object> objectForTransportIdResolver;

	/**
	 * Function that create the {@link ViewElement} to use as label for the target object.
	 */
	private final BiFunction<Object, ViewElementBuilderContext, ViewElement> objectLabelViewElementProvider;

	/**
	 * Predicate that checks if a particular {@link Sid} belongs to this section.
	 * The second parameter of the matcher would be object resolved by the identity.
	 * <p/>
	 * If left empty, a default matcher will be created.
	 */
	private final BiPredicate<Sid, Object> sidMatcher;

	/**
	 * Comparator for ordering the object entries to be added to a form section.
	 */
	private final Comparator<Object> objectRowComparator;

	/**
	 * ViewElementBuilder to generated the new item selector control below a form section.
	 * If {@code null} no new items can be added. See the {@link AclPermissionsFormItemSelectorControl}
	 * for creating a valid item selector row with a member control of your choice.
	 * Use {@link AclPermissionsForm#selectorControl()} for easy access.
	 */
	private ViewElementBuilder itemSelectorBuilder;

	// fixed rows

	@SuppressWarnings("unused")
	public static class AclPermissionsFormSectionBuilder
	{
		private Supplier<Permission[]> permissionsSupplier = () -> new Permission[0];

		public AclPermissionsFormSectionBuilder permissions( Permission... permissions ) {
			return permissionsSupplier( () -> permissions );
		}
	}
}
