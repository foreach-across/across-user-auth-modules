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

import com.foreach.across.modules.spring.security.acl.services.AclOperations;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.Sid;

import java.util.HashMap;
import java.util.Map;

/**
 * Responsible for verifying the ACE updates against a {@link AclPermissionsForm} configuration
 * and rendering the resulting elements.
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
@RequiredArgsConstructor
public final class AclPermissionsFormController
{
	@Getter
	private final AclOperations aclOperations;

	@Getter
	private final AclPermissionsFormData formData;

	@Getter
	private final Map<String, ModelEntry> model = new HashMap<>();

	/**
	 * Apply the model set on this controller to the {@link #aclOperations}.
	 * This will take into account the configured {@link #formData} and will only allow permission
	 * changes that were present on the form section that applies for a particular sid.
	 *
	 * @return acl after the changes have been applied
	 */
	public MutableAcl updateAclWithModel() {
		model.values()
		     .stream()
		     .filter( ModelEntry::isValid )
		     .forEach( entry -> {
			     AclPermissionsFormSection section = formData.getSectionWithName( entry.getSection() );
			     if ( section != null ) {
				     Sid sid = resolveSid( section, entry );
				     if ( sid != null ) {
					     aclOperations.apply(
							     sid,
							     formData.getPermissionsForSection( section ),
							     ArrayUtils.toPrimitive( entry.getPermissions() )
					     );
				     }
			     }
		     } );

		return aclOperations.getAcl();
	}

	private Sid resolveSid( AclPermissionsFormSection section, ModelEntry entry ) {
		Object sidTarget = section.getObjectForTransportIdResolver().apply( entry.getId() );
		if ( sidTarget != null ) {
			Sid sid = section.getSidForObjectResolver().apply( sidTarget );
			formData.getSidCache().put( sid, sidTarget );
			return section.getSidMatcher().test( sid, sidTarget ) ? sid : null;
		}

		return null;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@SuppressWarnings("WeakerAccess")
	public static class ModelEntry
	{
		private String section;
		private String id;
		private Integer[] permissions;

		boolean isValid() {
			return !StringUtils.isEmpty( section ) && !StringUtils.isEmpty( id );
		}
	}
}
