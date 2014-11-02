package com.foreach.across.modules.entity.services;

import com.foreach.across.core.annotations.PostRefresh;
import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.modules.entity.config.EntityConfiguration;
import com.foreach.across.modules.hibernate.repositories.BasicRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Contains the registered entity definitions that are manageable.
 */
@Service
public class EntityRegistry
{
	private static final Logger LOG = LoggerFactory.getLogger( EntityRegistry.class );

	@Autowired
	private AcrossContextBeanRegistry beanRegistry;

	private Collection<EntityConfiguration> entityConfigurations;

	public Collection<EntityConfiguration> getEntities() {
		return entityConfigurations;
	}

	@PostRefresh
	protected void buildRegistry() {
		// Scan all repositories
		Collection<BasicRepository> repositories = beanRegistry.getBeansOfType( BasicRepository.class, true );

		// Register the entity classes
		List<EntityConfiguration> entities = new ArrayList<>( repositories.size() );

		for ( BasicRepository repository : repositories ) {
			if ( isAllowedEntity( repository.getEntityClass() ) ) {
				entities.add( new EntityConfiguration( repository ) );
			}
		}

		Collections.sort( entities );

		this.entityConfigurations = entities;
	}

	private boolean isAllowedEntity( Class<?> clazz ) {
		return !Modifier.isInterface( clazz.getModifiers() ) && !Modifier.isAbstract( clazz.getModifiers() );
	}

	public EntityConfiguration getEntityByPath( String entityType ) {
		for ( EntityConfiguration configuration : entityConfigurations ) {
			if ( StringUtils.equals( entityType, configuration.getPath() ) ) {
				return configuration;
			}
		}

		return null;
	}

	public EntityConfiguration getEntityByClass( Class entityType ) {
		for ( EntityConfiguration configuration : entityConfigurations ) {
			if ( configuration.getEntityClass().equals( entityType ) ) {
				return configuration;
			}
		}

		return null;
	}
}
