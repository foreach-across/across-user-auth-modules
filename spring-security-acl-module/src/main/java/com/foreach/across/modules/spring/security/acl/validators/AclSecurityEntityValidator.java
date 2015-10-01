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
package com.foreach.across.modules.spring.security.acl.validators;

import com.foreach.across.modules.entity.validators.EntityValidatorSupport;
import com.foreach.across.modules.spring.security.acl.business.AclSecurityEntity;
import com.foreach.across.modules.spring.security.acl.services.AclSecurityEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

/**
 * Ensures name uniqueness of {@link AclSecurityEntity} instances.
 *
 * @author Arne Vandamme
 */
public class AclSecurityEntityValidator extends EntityValidatorSupport<AclSecurityEntity>
{
	private AclSecurityEntityService aclSecurityEntityService;

	@Autowired
	public AclSecurityEntityValidator( AclSecurityEntityService aclSecurityEntityService ) {
		this.aclSecurityEntityService = aclSecurityEntityService;
	}

	@Override
	public boolean supports( Class<?> clazz ) {
		return AclSecurityEntity.class.isAssignableFrom( clazz );
	}

	@Override
	protected void postValidation( AclSecurityEntity entity, Errors errors ) {
		if ( !errors.hasFieldErrors( "name" ) ) {
			AclSecurityEntity other = aclSecurityEntityService.getSecurityEntityByName( entity.getName() );

			if ( other != null && !other.equals( entity ) ) {
				errors.rejectValue( "name", "alreadyExists" );
			}
		}

		if ( !errors.hasFieldErrors( "parent" ) && !entity.isNew() ) {
			AclSecurityEntity parent = entity.getParent();

			while ( parent != null ) {
				if ( parent.equals( entity ) ) {
					errors.rejectValue( "parent", "recursiveParents" );
					parent = null;
				}
				else {
					parent = aclSecurityEntityService.getSecurityEntityByName( parent.getName() ).getParent();
				}
			}
		}
	}
}
