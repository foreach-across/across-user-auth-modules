package com.foreach.across.modules.bootstrapui.elements.builder;

import com.foreach.across.modules.bootstrapui.elements.ButtonViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderFactory;
import com.foreach.across.test.support.AbstractViewElementBuilderTest;
import org.junit.Test;

import static org.junit.Assert.assertNull;

public class TestButtonViewElementBuilder extends AbstractViewElementBuilderTest<ButtonViewElementBuilder, ButtonViewElement>
{
	@Override
	protected ButtonViewElementBuilder createBuilder( ViewElementBuilderFactory builderFactory ) {
		return new ButtonViewElementBuilder();
	}
}
