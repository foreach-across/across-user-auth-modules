package com.foreach.across.modules.entity.generators.id;

import com.foreach.across.modules.entity.generators.EntityIdGenerator;
import com.foreach.across.modules.hibernate.business.IdBasedEntity;

import java.io.Serializable;

public class DefaultIdGenerator implements EntityIdGenerator<Object>
{
	@Override
	public Serializable getId( Object entity ) {
		if ( entity instanceof IdBasedEntity ) {
			return ( (IdBasedEntity) entity ).getId();
		}
		else if ( entity instanceof Enum ) {
			return ( (Enum) entity ).name();
		}

		return entity.toString();
	}
}
