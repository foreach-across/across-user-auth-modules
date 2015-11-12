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
package com.foreach.across.modules.user.repositories;

import com.foreach.across.modules.properties.business.StringPropertiesSource;
import com.foreach.across.modules.properties.config.EntityPropertiesDescriptor;
import com.foreach.across.modules.properties.repositories.EntityPropertiesRepository;
import com.foreach.across.modules.user.UserModuleCache;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Arne Vandamme
 */
@Repository
public class GroupPropertiesRepository extends EntityPropertiesRepository<Long>
{
	public GroupPropertiesRepository( EntityPropertiesDescriptor configuration ) {
		super( configuration );
	}

	@Cacheable(UserModuleCache.GROUP_PROPERTIES)
	@Transactional(readOnly = true)
	@Override
	public StringPropertiesSource loadProperties( Long entityId ) {
		return super.loadProperties( entityId );
	}

	@CacheEvict(value = UserModuleCache.GROUP_PROPERTIES, key = "#entityId")
	@Transactional
	@Override
	public void saveProperties( Long entityId, StringPropertiesSource properties ) {
		super.saveProperties( entityId, properties );
	}

	@CacheEvict(value = UserModuleCache.GROUP_PROPERTIES, key = "#entityId")
	@Transactional
	@Override
	public void deleteProperties( Long entityId ) {
		super.deleteProperties( entityId );
	}
}
