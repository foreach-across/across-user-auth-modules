package com.foreach.across.modules.entity.generators;

import java.io.Serializable;

public interface EntityIdGenerator<T>
{
	Serializable getId( T entity );
}
