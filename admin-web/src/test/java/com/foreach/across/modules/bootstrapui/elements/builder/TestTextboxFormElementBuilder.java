package com.foreach.across.modules.bootstrapui.elements.builder;

import com.foreach.across.modules.bootstrapui.elements.TextboxFormElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderFactory;
import com.foreach.across.test.support.AbstractViewElementBuilderTest;

public class TestTextboxFormElementBuilder extends AbstractViewElementBuilderTest<TextboxFormElementBuilder, TextboxFormElement>
{
	@Override
	protected TextboxFormElementBuilder createBuilder( ViewElementBuilderFactory builderFactory ) {
		return new TextboxFormElementBuilder();
	}
}
