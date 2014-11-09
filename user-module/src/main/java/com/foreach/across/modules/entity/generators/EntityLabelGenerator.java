package com.foreach.across.modules.entity.generators;

public interface EntityLabelGenerator
{
	/**
	 * Generates the default display label for an entity.
	 *
	 * @param entity Entity instance for which the label should be generated.
	 * @return Generated label.
	 */
	String getLabel( Object entity );
}
