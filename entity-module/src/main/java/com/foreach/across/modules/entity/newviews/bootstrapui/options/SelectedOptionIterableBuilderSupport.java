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
package com.foreach.across.modules.entity.newviews.bootstrapui.options;

import com.foreach.across.modules.entity.newviews.util.EntityViewElementUtils;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;

/**
 * Helper supporting a {@link com.foreach.across.modules.entity.views.support.ValueFetcher} for retrieving
 * the selected values of the options built.
 *
 * @author Arne Vandamme
 */
public abstract class SelectedOptionIterableBuilderSupport implements OptionIterableBuilder
{
	private ValueFetcher<Object> valueFetcher;

	public ValueFetcher<Object> getValueFetcher() {
		return valueFetcher;
	}

	/**
	 * @param valueFetcher to be used to fetch the selected value if an entity is present
	 */
	@SuppressWarnings("unchecked")
	public void setValueFetcher( ValueFetcher valueFetcher ) {
		this.valueFetcher = valueFetcher;
	}

	/**
	 * Retrieve the selected values from the entity currently being built.  If no {@link ValueFetcher}
	 * is set or no entity present, an empty collection will be returned.
	 *
	 * @param builderContext containing the entity being built
	 * @return collection of selected values
	 */
	protected Collection retrieveSelected( ViewElementBuilderContext builderContext ) {
		Object entity = EntityViewElementUtils.currentEntity( builderContext );

		if ( entity != null && valueFetcher != null ) {
			Object selected = valueFetcher.getValue( entity );

			if ( selected != null ) {
				if ( selected instanceof Collection ) {
					return (Collection) selected;
				}
				else if ( selected.getClass().isArray() ) {
					return CollectionUtils.arrayToList( selected );
				}
				else {
					return Collections.singleton( selected );
				}
			}
		}

		return Collections.emptyList();
	}
}
