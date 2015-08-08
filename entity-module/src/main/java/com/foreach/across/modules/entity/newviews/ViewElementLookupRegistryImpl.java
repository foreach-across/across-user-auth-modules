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
package com.foreach.across.modules.entity.newviews;

import com.foreach.across.modules.web.ui.ViewElementBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Arne Vandamme
 */
public class ViewElementLookupRegistryImpl implements ViewElementLookupRegistry
{
	private final Map<ViewElementMode, String> viewElementTypes = new HashMap<>();
	private final Map<ViewElementMode, ViewElementBuilder> viewElementBuilderCache = new HashMap<>();
	private final Map<ViewElementMode, ViewElementBuilder> fixedViewElementBuilders = new HashMap<>();
	private final Map<ViewElementMode, Boolean> cacheableStatus = new HashMap<>();

	private boolean defaultCacheable = true;

	public boolean isDefaultCacheable() {
		return defaultCacheable;
	}

	public void setDefaultCacheable( boolean defaultCacheable ) {
		this.defaultCacheable = defaultCacheable;
	}

	@Override
	public void setViewElementBuilder( ViewElementMode mode, ViewElementBuilder builder ) {
		if ( builder != null ) {
			fixedViewElementBuilders.put( mode, builder );
		}
		else {
			fixedViewElementBuilders.remove( mode );
		}
	}

	@Override
	public boolean cacheViewElementBuilder( ViewElementMode mode, ViewElementBuilder builder ) {
		if ( isCacheable( mode ) ) {
			viewElementBuilderCache.put( mode, builder );
			return true;
		}

		return false;
	}

	@Override
	public String getViewElementType( ViewElementMode mode ) {
		return viewElementTypes.get( mode );
	}

	@Override
	public void setViewElementType( ViewElementMode mode, String viewElementType ) {
		viewElementTypes.put( mode, viewElementType );
	}

	@Override
	public void setCacheable( ViewElementMode mode, boolean cacheable ) {
		cacheableStatus.put( mode, cacheable );
		if ( !cacheable ) {
			reset( mode );
		}
	}

	@Override
	public void reset( ViewElementMode mode ) {
		viewElementBuilderCache.remove( mode );
	}

	@Override
	public ViewElementBuilder getViewElementBuilder( ViewElementMode mode ) {
		ViewElementBuilder fixed = fixedViewElementBuilders.get( mode );
		return fixed != null ? fixed : viewElementBuilderCache.get( mode );
	}

	@Override
	public boolean isCacheable( ViewElementMode mode ) {
		return cacheableStatus.containsKey( mode )
				? Boolean.TRUE.equals( cacheableStatus.get( mode ) ) : defaultCacheable;
	}

	/**
	 * Merge the current values into an already existing registry.
	 *
	 * @param existing to merge the values in
	 */
	public void mergeInto( ViewElementLookupRegistry existing ) {
		for ( Map.Entry<ViewElementMode, Boolean> type : cacheableStatus.entrySet() ) {
			existing.setCacheable( type.getKey(), type.getValue() );
		}
		for ( Map.Entry<ViewElementMode, String> type : viewElementTypes.entrySet() ) {
			existing.setViewElementType( type.getKey(), type.getValue() );
		}
		for ( Map.Entry<ViewElementMode, ViewElementBuilder> type : fixedViewElementBuilders.entrySet() ) {
			existing.setViewElementBuilder( type.getKey(), type.getValue() );
		}
		for ( Map.Entry<ViewElementMode, ViewElementBuilder> type : viewElementBuilderCache.entrySet() ) {
			existing.cacheViewElementBuilder( type.getKey(), type.getValue() );
		}
	}
}
