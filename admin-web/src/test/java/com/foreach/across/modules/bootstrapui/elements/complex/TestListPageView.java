package com.foreach.across.modules.bootstrapui.elements.complex;

import com.foreach.across.modules.bootstrapui.elements.AbstractBootstrapViewElementTest;
import com.foreach.across.modules.web.ui.ViewElement;
import org.junit.Test;

public class TestListPageView extends AbstractBootstrapViewElementTest
{
	@Test
	public void buildListManually() {

	}

	@Test
	public void buildListThroughBuilders() {

	}

	private void verify( ViewElement element ) {
		renderAndExpect(
				element,
				""
		);
	}
}
