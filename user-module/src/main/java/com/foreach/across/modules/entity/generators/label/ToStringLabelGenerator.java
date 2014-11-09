package com.foreach.across.modules.entity.generators.label;

import com.foreach.across.modules.entity.generators.EntityLabelGenerator;

public class ToStringLabelGenerator implements EntityLabelGenerator
{
	@Override
	public String getLabel( Object entity ) {
		return entity != null ? entity.toString() : null;
	}
}
