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
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.web.ui.IteratorItemStatsImpl;
import com.foreach.across.modules.web.ui.IteratorViewElementBuilderContext;
import org.junit.Test;
import org.springframework.ui.ModelMap;

import static com.foreach.across.modules.entity.newviews.util.EntityViewElementUtils.currentEntity;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * @author Arne Vandamme
 */
public class TestEntityViewElementUtils
{
	private final static Object SOME_ENTITY = "someEntity";

	@Test
	public void currentEntityForNullContext() {
		assertNull( currentEntity( null ) );
	}

	@Test
	public void currentEntityForIterator() {
		IteratorViewElementBuilderContext ctx = new IteratorViewElementBuilderContext<>(
				new IteratorItemStatsImpl<>( SOME_ENTITY, 0, false )
		);

		assertSame( SOME_ENTITY, currentEntity( ctx ) );
		assertSame( SOME_ENTITY, currentEntity( ctx, String.class ) );
		assertNull( currentEntity( ctx, Integer.class ) );
	}

	@Test
	public void currentEntityForEntityView() {
		EntityView view = new EntityView( new ModelMap() );

		EntityViewElementBuilderContext ctx = new EntityViewElementBuilderContext<>( view );
		assertNull( currentEntity( ctx ) );

		view.setEntity( SOME_ENTITY );
		ctx = new EntityViewElementBuilderContext<>( view );

		assertSame( SOME_ENTITY, currentEntity( ctx ) );
		assertSame( SOME_ENTITY, currentEntity( ctx, String.class ) );
		assertNull( currentEntity( ctx, Integer.class ) );
	}
}
