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
package com.foreach.across.modules.entity.views.util;

import com.foreach.across.modules.entity.views.EntityViewElementBuilderContext;
import com.foreach.across.modules.web.ui.IteratorViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;

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
		return currentEntity( builderContext, Object.class );
	}

	/**
	 * <p>Retrieve the current entity being processed in the builder context.  In case of a
	 * {@link IteratorViewElementBuilderContext} the entity of the iterator will be returned,
	 * in all other cases the attribute {@link EntityViewElementBuilderContext#ENTITY}.</p>
	 * <p>Will return null if no entity can be found or if the entity is not of the expected type.</p>
	 *
	 * @param builderContext curret builder context
	 * @return entity or null if none found or not of the expected type
	 */
	public static <U> U currentEntity( ViewElementBuilderContext builderContext, Class<U> expectedType ) {
		if ( builderContext == null ) {
			return null;
		}

		Object value = null;

		if ( builderContext instanceof IteratorViewElementBuilderContext ) {
			value = ( (IteratorViewElementBuilderContext) builderContext ).getItem();
		}
		else {
			value = builderContext.getAttribute( EntityViewElementBuilderContext.ENTITY );
		}

		return expectedType.isInstance( value ) ? (U) value : null;
	}
}
