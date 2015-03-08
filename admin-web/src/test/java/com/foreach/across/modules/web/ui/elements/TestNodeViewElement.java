package com.foreach.across.modules.web.ui.elements;

import com.foreach.across.modules.web.ui.AbstractViewElementTemplateTest;
import org.junit.Test;

public class TestNodeViewElement extends AbstractViewElementTemplateTest
{
	@Test
	public void simpleNodeWithoutContentOrAttributes() {
		NodeViewElement node = new NodeViewElement();
		node.setTagName( "div" );

		renderAndExpect( node, "<div></div>" );
	}

	@Test
	public void nodeWithChildren() {
		NodeViewElement node = NodeViewElement.forTag( "ul" );
		node.add( NodeViewElement.forTag( "li" ) );
		node.add( new TextViewElement( "child text" ) );
		node.add( NodeViewElement.forTag( "li" ) );

		renderAndExpect( node, "<ul><li></li>child text<li></li></ul>" );
	}

	@Test
	public void nodeWithAttributes() {
		NodeViewElement node = NodeViewElement.forTag( "div" );
		node.setAttribute( "class", "test-class test" );
		node.setAttribute( "data-something", "1236" );

		renderAndExpect( node, "<div class='test-class test' data-something='1236'></div>" );
	}

	@Test
	public void nestedNodesWithAttributes() {
		NodeViewElement node = NodeViewElement.forTag( "div" );
		node.setAttribute( "class", "some-class" );

		NodeViewElement paragraph = NodeViewElement.forTag( "p" );
		paragraph.setAttribute( "class", "main-paragraph" );
		paragraph.add( new TextViewElement( "paragraph text" ) );

		node.add( paragraph );

		renderAndExpect( node, "<div class='some-class'><p class='main-paragraph'>paragraph text</p></div>" );
	}
}
