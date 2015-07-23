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
import com.foreach.across.modules.user.business.MachinePrincipal;
import com.foreach.across.modules.user.business.QMachinePrincipal;
import com.foreach.across.modules.user.repositories.MachinePrincipalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

/**
 * @author Arne Vandamme
 */
public class MachinePrincipalValidator extends EntityValidatorSupport<MachinePrincipal>
{
	private MachinePrincipalRepository machinePrincipalRepository;

	@Autowired
	public MachinePrincipalValidator( MachinePrincipalRepository machinePrincipalRepository ) {
		this.machinePrincipalRepository = machinePrincipalRepository;
	}

	@Override
	public boolean supports( Class<?> clazz ) {
		return MachinePrincipal.class.isAssignableFrom( clazz );
	}

	@Override
	protected void preValidation( MachinePrincipal entity, Errors errors ) {
		if ( !errors.hasFieldErrors( "name" ) ) {
			MachinePrincipal other = machinePrincipalRepository.findOne(
					QMachinePrincipal.machinePrincipal.name.equalsIgnoreCase( entity.getName() )
			);

			if ( other != null && !other.equals( entity ) ) {
				errors.rejectValue( "name", "alreadyExists" );
			}
		}
	}
}
