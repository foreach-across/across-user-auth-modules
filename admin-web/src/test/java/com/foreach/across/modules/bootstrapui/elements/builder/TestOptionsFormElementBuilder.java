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
package com.foreach.across.modules.bootstrapui.elements.builder;

import com.foreach.across.modules.web.ui.ViewElementBuilderFactory;
import com.foreach.across.modules.web.ui.elements.NodeViewElementSupport;
import com.foreach.across.test.support.AbstractViewElementBuilderTest;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 */
public class TestOptionsFormElementBuilder extends AbstractViewElementBuilderTest<OptionsFormElementBuilder, NodeViewElementSupport>
{
	@Override
	protected OptionsFormElementBuilder createBuilder( ViewElementBuilderFactory builderFactory ) {
		return new OptionsFormElementBuilder();
	}

	@Override
	protected Collection<String> nonBuilderReturningMethods() {
		return Arrays.asList( "option" );
	}

	@Test(expected = IllegalStateException.class)
	public void noNestingOfOptionsFormElementBuilders() {
		when( builderContext.hasAttribute( OptionsFormElementBuilder.class ) ).thenReturn( true );

		build();
	}

	@Test(expected = IllegalStateException.class)
	public void optionCanOnlyBeUsedWithinOptionsBuilder() {
		builder.option().build( builderContext );
	}
}
