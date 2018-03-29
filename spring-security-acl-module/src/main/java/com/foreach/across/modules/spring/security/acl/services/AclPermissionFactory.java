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

package com.foreach.across.modules.spring.security.acl.services;

import org.springframework.security.acls.domain.PermissionFactory;
import org.springframework.security.acls.model.Permission;

/**
 * Extension of the {@link PermissionFactory} from Spring security ACL.
 * Allows for manually registering additional permissions using {@link #registerPermission(Permission, String)},
 * as well as retrieving the name for a registered permission using {@link #getNameForPermission(Permission)}.
 * <p/>
 * Any custom permission registered can also be used in expression based access control, eg:
 * <pre>{@code
 * @PreAuthorize("hasPermission(#object, 'my-custom-permission')")
 * void customPermission( Object object ) {
 *    ...
 * }
 * }</pre>
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
public interface AclPermissionFactory extends PermissionFactory
{
	/**
	 * Retrieve the registered name for a permission.
	 * Will throw an {@code IllegalArgumentException} if the permission is not registered.
	 * <p/>
	 * This method cannot be used for {@link org.springframework.security.acls.domain.CumulativePermission} instances
	 * unless they are also registered.
	 *
	 * @param permission to get the name for
	 * @return registered name
	 */
	String getNameForPermission( Permission permission );

	/**
	 * Register an additional permission to the permission factory.
	 *
	 * @param permission     to register
	 * @param permissionName associated with that permission
	 * @see com.foreach.across.modules.spring.security.acl.business.AclPermission
	 */
	void registerPermission( Permission permission, String permissionName );


}
