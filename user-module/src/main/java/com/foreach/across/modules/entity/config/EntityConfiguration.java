package com.foreach.across.modules.entity.config;

import com.foreach.across.modules.hibernate.repositories.BasicRepository;
import org.apache.commons.lang3.StringUtils;

/**
 * Contains the forms configuration for a specific entity.
 */
public class EntityConfiguration implements Comparable<EntityConfiguration>
{
	private final Class<?> entityClass;
	private BasicRepository repository;

	public EntityConfiguration( BasicRepository repository ) {
		this.repository = repository;
		this.entityClass = repository.getEntityClass();
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public String getName() {
		return entityClass.getSimpleName();
	}

	public String getPath() {
		return StringUtils.lowerCase( getName() );
	}

	@Override
	public int compareTo( EntityConfiguration o ) {
		return getName().compareTo( o.getName() );
	}

	public BasicRepository getRepository() {
		return repository;
	}
}
