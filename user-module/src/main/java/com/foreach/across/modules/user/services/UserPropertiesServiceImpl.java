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
package com.foreach.across.modules.user.services;

import com.foreach.across.modules.properties.business.StringPropertiesSource;
import com.foreach.across.modules.properties.registries.EntityPropertiesRegistry;
import com.foreach.across.modules.properties.repositories.EntityPropertiesRepository;
import com.foreach.across.modules.properties.services.AbstractEntityPropertiesService;
import com.foreach.across.modules.user.business.UserProperties;
import com.foreach.common.spring.util.PropertyTypeRegistry;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

/**
 * @author Arne Vandamme
 */
@Service
public class UserPropertiesServiceImpl extends AbstractEntityPropertiesService<UserProperties, Long> implements UserPropertiesService
{
	public UserPropertiesServiceImpl( EntityPropertiesRegistry entityPropertiesRegistry,
	                                  EntityPropertiesRepository<Long> entityPropertiesRepository ) {
		super( entityPropertiesRegistry, entityPropertiesRepository );
	}

	@Override
	protected UserProperties createEntityProperties( Long entityId,
	                                                 PropertyTypeRegistry<String> propertyTypeRegistry,
	                                                 ConversionService conversionService,
	                                                 StringPropertiesSource source ) {
		return new UserProperties( entityId, propertyTypeRegistry, conversionService, source );
	}
}
