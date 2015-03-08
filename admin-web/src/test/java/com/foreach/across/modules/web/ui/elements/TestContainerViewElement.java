package com.foreach.across.modules.web.ui.elements;

import com.foreach.across.modules.web.ui.AbstractViewElementTemplateTest;
import org.junit.Test;

public class TestContainerViewElement extends AbstractViewElementTemplateTest
{
	@Test
	public void multipleTextElements() {
		ContainerViewElement container = new ContainerViewElement();
		container.add( new TextViewElement( "one, " ) );
		container.add( new TextViewElement( "two, " ) );
		container.add( new TextViewElement( "three" ) );

		renderAndExpect( container, "one, two, three" );
	}

	@Test
	public void nestedContainerElements() {
		ContainerViewElement container = new ContainerViewElement();
		container.add( new TextViewElement( "one, " ) );

		ContainerViewElement subContainer = new ContainerViewElement();
		subContainer.add( new TextViewElement( "two, " ) );
		subContainer.add( new TextViewElement( "three" ) );

		container.add( subContainer );
		renderAndExpect( container, "one, two, three" );
	}

	@Test
	public void customTemplateWithoutNesting() {
		ContainerViewElement container = new ContainerViewElement();
		container.setCustomTemplate( "th/test/elements/container" );
		container.add( new TextViewElement( "one" ) );
		container.add( new TextViewElement( "two" ) );
		container.add( new TextViewElement( "three" ) );

		renderAndExpect( container, "<ul><li>one</li><li>two</li><li>three</li></ul>" );
	}

	@Test
	public void customTemplateWithNesting() {
		ContainerViewElement container = new ContainerViewElement();
		container.setCustomTemplate( "th/test/elements/container :: nested(${component})" );
		container.add( new TextViewElement( "one" ) );

		ContainerViewElement subContainer = new ContainerViewElement();
		subContainer.add( new TextViewElement( "two, " ) );
		subContainer.add( new TextViewElement( "three" ) );

		ContainerViewElement otherSubContainer = new ContainerViewElement();
		otherSubContainer.setCustomTemplate( "th/test/elements/container" );
		otherSubContainer.add( new TextViewElement( "four" ) );
		otherSubContainer.add( new TextViewElement( "five" ) );

		subContainer.add( otherSubContainer );

		container.add( subContainer );

		renderAndExpect( container,
		                 "<ol><li>one</li><li>two, three<ul><li>four</li><li>five</li></ul></li></ol>" );
	}

}
