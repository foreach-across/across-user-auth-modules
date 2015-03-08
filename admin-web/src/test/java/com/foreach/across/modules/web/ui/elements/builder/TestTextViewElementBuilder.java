package com.foreach.across.modules.web.ui.elements.builder;

import com.foreach.across.modules.web.ui.AbstractViewElementBuilderTest;
import com.foreach.across.modules.web.ui.ViewElementBuilderFactory;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestTextViewElementBuilder extends AbstractViewElementBuilderTest<TextViewElementBuilder, TextViewElement>
{
	@Override
	protected TextViewElementBuilder createBuilder( ViewElementBuilderFactory factory ) {
		return new TextViewElementBuilder();
	}

	@Test
	public void defaults() {
		build();

		assertNull( element.getText() );
		assertTrue( element.isEscapeXml() );
	}

	@Test
	public void text() {
		builder.escapeXml( false ).text( "some text" );

		build();

		assertEquals( "some text", element.getText() );
		assertTrue( element.isEscapeXml() );
	}

	@Test
	public void html() {
		builder.escapeXml( true ).html( "some text" );

		build();

		assertEquals( "some text", element.getText() );
		assertFalse( element.isEscapeXml() );
	}

	@Test
	public void xml() {
		builder.escapeXml( true ).xml( "some text" );

		build();

		assertEquals( "some text", element.getText() );
		assertFalse( element.isEscapeXml() );
	}

	@Test
	public void content() {
		builder.escapeXml( false ).content( "some text" );
		build();
		assertEquals( "some text", element.getText() );
		assertFalse( element.isEscapeXml() );

		reset();

		builder.escapeXml( true ).content( "some text" );
		build();
		assertEquals( "some text", element.getText() );
		assertTrue( element.isEscapeXml() );
	}

	@Test
	public void escapeXmlSpecified() {
		builder.escapeXml( false );

		build();

		assertFalse( element.isEscapeXml() );
	}
}
