package com.foreach.across.modules.bootstrapui.elements;

import org.junit.Test;

import static com.foreach.across.modules.bootstrapui.elements.Grid.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestGridClassGeneration
{
	@Test
	public void width() {
		assertEquals( "col-xs-1", Device.XS.width( 1 ).toString() );
		assertEquals( "col-md-4", Device.MD.width( Columns.FOUR ).asWidth().toString() );
		assertEquals( "col-lg-6", Device.LARGE.width( Width.HALF ).asWidth().toString() );
	}

	@Test
	public void offset() {
		assertEquals( "col-lg-offset-6", Device.LG.width( 6 ).asOffset().toString() );
	}

	@Test
	public void pull() {
		assertEquals( "col-md-pull-3", Device.MD.width( 3 ).asPull().toString() );
	}

	@Test
	public void push() {
		assertEquals( "col-sm-push-12", Device.SM.width( 12 ).asPush().toString() );
	}

	@Test
	public void hidden() {
		assertEquals( "hidden-xs", Device.XS.hidden().toString() );
		assertEquals( "hidden-md", Device.MD.hidden().toString() );
		assertEquals( "hidden-lg", Device.LG.hidden().toString() );
		assertEquals( "hidden-sm", Device.SM.hidden().toString() );
	}

	@Test
	public void visible() {
		assertEquals( "visible-md-block", Device.MD.visible().toString() );
		assertEquals( "visible-xs-block", Device.XS.visible().block().toString() );
		assertEquals( "visible-sm-inline", Device.SM.visible().inline().toString() );
		assertEquals( "visible-lg-inline-block", Device.LG.visible().inlineBlock().toString() );
	}

	@Test
	public void positionAsOffset() {
		Grid.Position position = Grid.position(
				Device.MD.width( 2 ), Device.SM.hidden(), Device.LARGE.width( 6 ).asPull(), Device.XS.width( 5 )
		);

		assertEquals( "col-md-offset-2 col-xs-offset-5", position.asOffset().toString() );
	}

	@Test
	public void createGrid() {
		Grid grid = Grid.create(
				Grid.position( Device.MD.hidden(), Device.LG.width( Width.QUARTER ).asOffset() ),
				Grid.position( Device.SM.width( Width.THREE_QUARTERS ) )
		);

		assertEquals(
				"Grid{[hidden-md col-lg-offset-3],[col-sm-9]}",
				grid.toString()
		);
	}

	@Test
	public void equality() {
		assertEquals( Device.MD.visible(), Device.MD.visible() );
		assertEquals( Device.LG.width( 3 ), Device.LG.width( 3 ) );
		assertNotEquals( Device.LG.width( 3 ), Device.LG.width( 4 ) );
		assertNotEquals( Device.LG.width( 3 ), Device.XS.width( 3 ) );

		Grid gridOne = Grid.create(
				Grid.position( Device.MD.hidden(), Device.LG.width( Width.QUARTER ).asOffset() ),
				Grid.position( Device.SM.width( Width.THREE_QUARTERS ) )
		);

		Grid gridTwo = Grid.create(
				Grid.position( Device.MD.hidden(), Device.LG.width( Width.QUARTER ).asOffset() ),
				Grid.position( Device.SM.width( Width.THREE_QUARTERS ) )
		);

		assertEquals( gridOne, gridTwo );
	}
}
