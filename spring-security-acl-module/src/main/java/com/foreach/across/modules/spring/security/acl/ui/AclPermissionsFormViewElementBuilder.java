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
import com.foreach.across.modules.spring.security.acl.services.AclPermissionFactory;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import lombok.RequiredArgsConstructor;

/**
 * Responsible for rendering an ACL permissions form.
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
@RequiredArgsConstructor
public final class AclPermissionsFormViewElementBuilder implements ViewElementBuilder<ContainerViewElement>
{
	private final AclPermissionsForm permissionsForm;
	private final AclOperations aclOperations;
	private final AclPermissionFactory permissionFactory;

	@Override
	public ContainerViewElement build( ViewElementBuilderContext viewElementBuilderContext ) {
		return null;
	}
}
