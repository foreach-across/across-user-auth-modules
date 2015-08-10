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
package com.foreach.across.modules.it.properties.definingmodule.repositories;

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.it.properties.definingmodule.business.EntityRevision;
import com.foreach.across.modules.properties.config.EntityPropertiesDescriptor;
import com.foreach.across.modules.properties.repositories.RevisionBasedEntityPropertiesRepository;

/**
 * @author Arne Vandamme
 */
@Exposed
public class RevisionPropertiesRepository extends RevisionBasedEntityPropertiesRepository<Long, EntityRevision>
{
	public RevisionPropertiesRepository( EntityPropertiesDescriptor configuration ) {
		super( configuration );
	}

	public void setAllowRevisionModification( boolean allowRevisionModification ) {
		super.setAllowRevisionModification( allowRevisionModification );
	}
}
