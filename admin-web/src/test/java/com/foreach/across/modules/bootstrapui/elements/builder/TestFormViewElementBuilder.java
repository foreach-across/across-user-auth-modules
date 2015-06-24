package com.foreach.across.modules.bootstrapui.elements.builder;

import com.foreach.across.modules.bootstrapui.elements.FormViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderFactory;
import com.foreach.across.test.support.AbstractViewElementBuilderTest;

public class TestFormViewElementBuilder extends AbstractViewElementBuilderTest<FormViewElementBuilder, FormViewElement>
{
	@Override
	protected FormViewElementBuilder createBuilder( ViewElementBuilderFactory builderFactory ) {
		return new FormViewElementBuilder();
	}
}
