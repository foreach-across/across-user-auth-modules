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

package com.foreach.across.modules.oauth2.validators;

import com.foreach.across.modules.entity.validators.EntityValidatorSupport;
import com.foreach.across.modules.oauth2.business.OAuth2Client;
import com.foreach.across.modules.oauth2.repositories.OAuth2ClientRepository;
import com.foreach.across.modules.user.services.support.DefaultUserDirectoryStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Component
public class OAuth2ClientValidator extends EntityValidatorSupport<OAuth2Client>
{
	private final OAuth2ClientRepository clientRepository;
	private final DefaultUserDirectoryStrategy defaultUserDirectoryStrategy;

	@Autowired
	public OAuth2ClientValidator( OAuth2ClientRepository clientRepository,
	                              DefaultUserDirectoryStrategy defaultUserDirectoryStrategy ) {
		this.clientRepository = clientRepository;
		this.defaultUserDirectoryStrategy = defaultUserDirectoryStrategy;
	}

	@Override
	public boolean supports( Class<?> clazz ) {
		return OAuth2Client.class.isAssignableFrom( clazz );
	}

	@Override
	protected void preValidation( OAuth2Client entity, Errors errors ) {
		defaultUserDirectoryStrategy.apply( entity );
	}

	@Override
	protected void postValidation( OAuth2Client entity, Errors errors ) {
		if ( !errors.hasFieldErrors( "clientId" ) ) {
			OAuth2Client other = clientRepository.findOneByPrincipalName( entity.getPrincipalName() );
			if ( other != null && !other.equals( entity ) ) {
				errors.rejectValue( "clientId", "alreadyExists" );
			}
		}
	}
}

