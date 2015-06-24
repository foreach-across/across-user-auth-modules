package com.foreach.across.modules.bootstrapui.elements.builder;

import com.foreach.across.modules.bootstrapui.elements.LabelFormElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderFactory;
import com.foreach.across.test.support.AbstractViewElementBuilderTest;

public class TestLabelFormElementBuilder extends AbstractViewElementBuilderTest<LabelFormElementBuilder, LabelFormElement>
{
	@Override
	protected LabelFormElementBuilder createBuilder( ViewElementBuilderFactory builderFactory ) {
		return new LabelFormElementBuilder();
	}
}
