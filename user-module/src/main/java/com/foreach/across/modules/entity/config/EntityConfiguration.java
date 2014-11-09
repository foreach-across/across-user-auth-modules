package com.foreach.across.modules.entity.config;

import com.foreach.across.modules.entity.generators.EntityIdGenerator;
import com.foreach.across.modules.entity.generators.EntityLabelGenerator;
import com.foreach.across.modules.entity.business.EntityWrapper;
import com.foreach.across.modules.entity.generators.id.DefaultIdGenerator;
import com.foreach.across.modules.entity.generators.label.ToStringLabelGenerator;
import com.foreach.across.modules.hibernate.repositories.BasicRepository;
import org.apache.commons.lang3.StringUtils;

/**
 * Contains the forms configuration for a specific entity.
 */
public class EntityConfiguration implements Comparable<EntityConfiguration>
{
	private final Class<?> entityClass;
	private BasicRepository repository;

	private EntityLabelGenerator labelGenerator;
	private EntityIdGenerator idGenerator;

	public EntityConfiguration( Class<?> entityClass ) {
		this.entityClass = entityClass;

		labelGenerator = new ToStringLabelGenerator();
		idGenerator = new DefaultIdGenerator();
	}

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

	public EntityLabelGenerator getLabelGenerator() {
		return labelGenerator;
	}

	public void setLabelGenerator( EntityLabelGenerator labelGenerator ) {
		this.labelGenerator = labelGenerator;
	}

	public EntityIdGenerator getIdGenerator() {
		return idGenerator;
	}

	public void setIdGenerator( EntityIdGenerator idGenerator ) {
		this.idGenerator = idGenerator;
	}

	@Override
	public int compareTo( EntityConfiguration o ) {
		return getName().compareTo( o.getName() );
	}

	public BasicRepository getRepository() {
		return repository;
	}

	/**
	 * Wraps an entity with an access wrapper configured according to the configuration.
	 *
	 * @param entity instance to wrap
	 */
	public EntityWrapper wrap( Object entity ) {
		return new EntityWrapper( this, entity );
	}
}
