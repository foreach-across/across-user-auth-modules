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
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

/**
 * Central registry for ACL permission form configurations.
 * Different form configurations should be registered with a unique name (duplicates will replace the previous).
 * <p>
 * Entities for which the ACL permissions form view should be available, must have the name of the relevant form profile
 * registered on their {@link com.foreach.across.modules.entity.registry.EntityConfiguration} under the key {@link #ATTR_ACL_PROFILE}.
 * <p/>
 * This implementation is basically a simple {@link HashMap} of profile name - permissions form.
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
@ConditionalOnAcrossModule({ EntityModule.NAME, AdminWebModule.NAME })
@Service
public class AclPermissionsFormRegistry extends HashMap<String, AclPermissionsForm>
{
	/**
	 * Name of the {@link com.foreach.across.modules.entity.registry.EntityConfiguration} attribute that contains the name
	 * of the form profile to use for the ACL permissions form.
	 */
	public static final String ATTR_ACL_PROFILE = AclPermissionsFormRegistry.class.getName() + ".profileName";

	/**
	 * Get the form for a {@link EntityConfiguration}. Will check if the attribute {@link #ATTR_ACL_PROFILE} is set
	 * on the configuration, and use the value of that attribute for the profile name of the form.
	 * <p/>
	 * If either the attribute is not present or the form is not registered under that profile name, empty will be returned.
	 *
	 * @param configuration to get the form for
	 * @return form
	 */
	public Optional<AclPermissionsForm> getForEntityConfiguration( EntityConfiguration configuration ) {
		String profileName = configuration.getAttribute( ATTR_ACL_PROFILE, String.class );
		return Optional.ofNullable( profileName != null ? get( profileName ) : null );
	}
}
