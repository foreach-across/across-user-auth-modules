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
package com.foreach.across.modules.it.properties.definingmodule.services;

import com.foreach.across.modules.it.properties.definingmodule.business.EntityRevision;
import com.foreach.across.modules.it.properties.definingmodule.business.RevisionProperties;
import com.foreach.across.modules.it.properties.definingmodule.registry.RevisionPropertyRegistry;
import com.foreach.across.modules.it.properties.definingmodule.repositories.RevisionPropertiesRepository;
import com.foreach.across.modules.properties.business.StringPropertiesSource;
import com.foreach.across.modules.properties.services.AbstractRevisionBasedEntityPropertiesService;
import com.foreach.common.spring.properties.PropertyTypeRegistry;

/**
 * @author Arne Vandamme
 */
public class RevisionPropertyService extends AbstractRevisionBasedEntityPropertiesService<RevisionProperties, Long, EntityRevision>
{
	public RevisionPropertyService( RevisionPropertyRegistry revisionPropertyRegistry,
	                                RevisionPropertiesRepository revisionPropertiesRepository ) {
		super( revisionPropertyRegistry, revisionPropertiesRepository );
	}

	@Override
	protected RevisionProperties createEntityProperties( Long entityId,
	                                                     PropertyTypeRegistry<String> propertyTypeRegistry,
	                                                     StringPropertiesSource source ) {
		return new RevisionProperties( entityId, propertyTypeRegistry, source );
	}
}
