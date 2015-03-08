package com.foreach.across.modules.web.ui;

import com.foreach.across.modules.web.ui.elements.TextViewElement;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestViewElementGenerator
{
	@Test
	public void test() {
		ViewElementGenerator generator = new ViewElementGenerator();
		generator.setItems( Arrays.asList( (Object) "one", (Object) "two" ) );
		generator.setCallback( new ViewElementGenerator.GeneratorCallback()
		{
			@Override
			public ViewElement create( Object item ) {
				return new TextViewElement( (String) item );
			}
		} );

		Set<ViewElement> generated = new HashSet<>();

		for ( ViewElement element : generator ) {
			generated.add( element );

			assertTrue( element instanceof TextViewElement );
		}

		for ( ViewElement repeat : generator ) {
			assertTrue( generated.contains( repeat ) );
		}

		assertEquals( 2, generated.size() );
	}
}
