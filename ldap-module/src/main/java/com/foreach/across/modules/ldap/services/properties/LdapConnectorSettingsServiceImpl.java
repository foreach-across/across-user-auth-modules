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

package com.foreach.across.modules.ldap.services.properties;

import com.foreach.across.modules.ldap.business.LdapConnectorSettings;
import com.foreach.across.modules.properties.business.StringPropertiesSource;
import com.foreach.across.modules.properties.registries.EntityPropertiesRegistry;
import com.foreach.across.modules.properties.repositories.EntityPropertiesRepository;
import com.foreach.across.modules.properties.services.AbstractEntityPropertiesService;
import com.foreach.common.spring.properties.PropertyTypeRegistry;

/**
 * @author Arne Vandamme
 * @since Mar 2016
 */
public class LdapConnectorSettingsServiceImpl
		extends AbstractEntityPropertiesService<LdapConnectorSettings, Long> implements LdapConnectorSettingsService

{
	public LdapConnectorSettingsServiceImpl( EntityPropertiesRegistry entityPropertiesRegistry,
	                                         EntityPropertiesRepository<Long> entityPropertiesRepository ) {
		super( entityPropertiesRegistry, entityPropertiesRepository );
	}

	@Override
	protected LdapConnectorSettings createEntityProperties( Long entityId,
	                                                        PropertyTypeRegistry<String> propertyTypeRegistry,
	                                                        StringPropertiesSource source ) {
		return new LdapConnectorSettings( entityId, propertyTypeRegistry, source );
	}
}

