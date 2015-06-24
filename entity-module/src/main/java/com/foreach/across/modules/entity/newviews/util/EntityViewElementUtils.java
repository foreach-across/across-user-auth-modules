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
package com.foreach.across.modules.entity.newviews.util;

import com.foreach.across.modules.entity.newviews.EntityViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.IteratorViewElementBuilderContext;

/**
 * Contains utility methods related to view elements and view building in an entity context.
 *
 * @author Arne Vandamme
 */
public class EntityViewElementUtils
{
	protected EntityViewElementUtils() {
	}

	/**
	 * <p>Retrieve the current entity being processed in the builder context.  In case of a
	 * {@link IteratorViewElementBuilderContext} the entity of the iterator will be returned,
	 * in all other cases the attribute {@link EntityViewElementBuilderContext#ENTITY}.</p>
	 * <p>Will return null if no entity can be found.</p>
	 *
	 * @param builderContext curret builder context
	 * @return entity or null if none found
	 */
	public static Object currentEntity( ViewElementBuilderContext builderContext ) {
		if ( builderContext == null ) {
			return null;
		}

		if ( builderContext instanceof IteratorViewElementBuilderContext ) {
			return ( (IteratorViewElementBuilderContext) builderContext ).getItem();
		}

		return builderContext.getAttribute( EntityViewElementBuilderContext.ENTITY );
	}
}
