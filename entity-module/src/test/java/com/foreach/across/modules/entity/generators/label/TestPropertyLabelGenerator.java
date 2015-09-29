package com.foreach.across.modules.entity.generators.label;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestPropertyLabelGenerator
{
	protected static class Domain
	{
		private int id;
		private String name;

		public Domain( int id, String name ) {
			this.id = id;
			this.name = name;
		}

		public int getId() {
			return id;
		}

		public void setId( int id ) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName( String name ) {
			this.name = name;
		}
	}

	private Domain one = new Domain( 1, "nameOne" );
	private Domain two = new Domain( 2, "nameTwo" );

	@Test
	public void nameProperty() {
		PropertyLabelGenerator generator = PropertyLabelGenerator.forProperty( Domain.class, "name" );

		assertEquals( "nameOne", generator.getLabel( one ) );
		assertEquals( "nameTwo", generator.getLabel( two ) );
	}

	@Test
	public void idProperty() {
		PropertyLabelGenerator generator = PropertyLabelGenerator.forProperty( Domain.class, "id" );

		assertEquals( "1", generator.getLabel( one ) );
		assertEquals( "2", generator.getLabel( two ) );
	}

	@Test
	public void unknownPropertyResultsInNullGenerator() {
		PropertyLabelGenerator generator = PropertyLabelGenerator.forProperty( Domain.class, "title" );

		assertNull( generator );
	}
}
