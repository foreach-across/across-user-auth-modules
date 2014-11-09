package com.foreach.across.modules.entity.config;

public interface EntityConfigurer
{
	boolean accepts( Class<?> entityClass );

	void configure( EntityConfiguration configuration );
}
