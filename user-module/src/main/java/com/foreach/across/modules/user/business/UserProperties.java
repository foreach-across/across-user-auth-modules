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
package com.foreach.across.modules.user.business;

import com.foreach.across.modules.properties.business.EntityProperties;
import com.foreach.common.spring.util.PropertiesSource;
import com.foreach.common.spring.util.PropertyTypeRegistry;

/**
 * @author Arne Vandamme
 */
public class UserProperties extends EntityProperties<Long>
{
	private final long userId;

	public UserProperties( long userId,
	                       PropertyTypeRegistry<String> propertyTypeRegistry,
	                       PropertiesSource source ) {
		super( propertyTypeRegistry, source );

		this.userId = userId;
	}

	@Override
	public Long getId() {
		return userId;
	}
}
