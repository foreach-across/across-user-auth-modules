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

package com.foreach.across.modules.user.validators;

import com.foreach.across.modules.entity.validators.EntityValidatorSupport;
import com.foreach.across.modules.user.business.QRole;
import com.foreach.across.modules.user.business.Role;
import com.foreach.across.modules.user.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class RoleValidator extends EntityValidatorSupport<Role>
{
	private final RoleRepository roleRepository;

	@Autowired
	public RoleValidator( RoleRepository roleRepository ) {
		this.roleRepository = roleRepository;
	}

	@Override
	public boolean supports( Class<?> aClass ) {
		return Role.class.isAssignableFrom( aClass );
	}

	@Override
	protected void postValidation( Role entity, Errors errors ) {
		if ( !errors.hasFieldErrors( "name" ) ) {
			Role other = roleRepository.findOne( QRole.role.name.equalsIgnoreCase( entity.getName() ) )
			                           .orElse( null );

			if ( other != null && !other.equals( entity ) ) {
				errors.rejectValue( "name", "alreadyExists" );
			}
		}

		if ( !errors.hasFieldErrors( "authority" ) ) {
			Role other = roleRepository.findOne( QRole.role.authority.equalsIgnoreCase( entity.getAuthority() ) )
			                           .orElse( null );

			// because equals has been overridden ensure id based check
			if ( other != null && !other.equals( entity ) ) {
				errors.rejectValue( "authority", "alreadyExists" );
			}
		}
	}
}
