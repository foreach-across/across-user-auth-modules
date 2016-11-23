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

package com.foreach.across.modules.user.ui.support;

import com.foreach.across.modules.entity.query.EQString;
import com.foreach.across.modules.user.business.QRole;
import com.foreach.across.modules.user.business.Role;
import com.foreach.across.modules.user.repositories.RoleRepository;
import org.springframework.core.convert.converter.Converter;

/**
 * Converts string value to Role in an EQL {@link com.foreach.across.modules.entity.query.EntityQuery}.
 * Value is expected to be the name of the {@link Role}.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class EQStringToRoleConverter implements Converter<EQString, Role>
{
	private final RoleRepository roleRepository;

	public EQStringToRoleConverter( RoleRepository roleRepository ) {
		this.roleRepository = roleRepository;
	}

	@Override
	public Role convert( EQString source ) {
		return roleRepository.findOne( QRole.role.name.equalsIgnoreCase( source.getValue() ) );
	}
}
