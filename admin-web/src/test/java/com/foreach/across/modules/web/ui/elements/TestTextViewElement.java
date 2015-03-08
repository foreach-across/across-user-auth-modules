package com.foreach.across.modules.web.ui.elements;

import com.foreach.across.modules.web.ui.AbstractViewElementTemplateTest;
import org.junit.Test;

public class TestTextViewElement extends AbstractViewElementTemplateTest
{
	@Test
	public void nonHtmlText() {
		renderAndExpect(
				new TextViewElement( "<strong>simple</strong> text" ),
				"&lt;strong&gt;simple&lt;/strong&gt; text"
		);
	}

	@Test
	public void htmlText() {
		renderAndExpect(
				new TextViewElement( "<strong>test</strong> text", false ),
				"<strong>test</strong> text"
		);
	}

	@Test
	public void customTemplateWithoutFragment() {
		TextViewElement text = new TextViewElement( "text content" );
		text.setCustomTemplate( "th/test/elements/text" );

		renderAndExpect( text, "<h3 class=\"page-header\">text content</h3>" );
	}

	@Test
	public void customTemplateWithFragmentButNoVariables() {
		TextViewElement text = new TextViewElement( "text content" );
		text.setCustomTemplate( "th/test/elements/text :: randomText" );

		renderAndExpect( text, "Some random text instead..." );
	}

	@Test
	public void customTemplateWithFragmendAndVariables() {
		TextViewElement text = new TextViewElement( "text content" );
		text.setCustomTemplate( "th/test/elements/text :: otherTemplate(${component})" );

		renderAndExpect( text, "<div>Received text: text content</div>" );
	}
}
