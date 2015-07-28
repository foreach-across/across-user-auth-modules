/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.foreach.across.modules.entity.registry.builders;

import com.foreach.across.core.annotations.OrderInModule;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.meta.PropertyPersistenceMetadata;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.context.MappingContext;

import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Adds {@link org.springframework.data.mapping.PersistentProperty} and {@link PropertyPersistenceMetadata} attributes
 * to entity properties, based on the configured {@link MappingContext}s.
 *
 * @author Arne Vandamme
 */
@OrderInModule(2)
public class EntityPropertyRegistryPersistenceMetadataBuilder implements EntityPropertyRegistryBuilder
{
	private Set<MappingContext> mappingContexts = new LinkedHashSet<>();

	public void addMappingContext( MappingContext mappingContext ) {
		mappingContexts.add( mappingContext );
	}

	public void addMappingContexts( Collection<MappingContext> contexts ) {
		mappingContexts.addAll( contexts );
	}

	@Override
	public void buildRegistry( Class<?> entityType, MutableEntityPropertyRegistry registry ) {
		for ( MappingContext<?, ?> mappingContext : mappingContexts ) {
			if ( mappingContext.hasPersistentEntityFor( entityType ) ) {
				PersistentEntity entity = mappingContext.getPersistentEntity( entityType );

				for ( EntityPropertyDescriptor descriptor : registry.getRegisteredDescriptors() ) {
					PersistentProperty persistentProperty = entity.getPersistentProperty( descriptor.getName() );

					if ( persistentProperty != null ) {
						MutableEntityPropertyDescriptor mutable = registry.getMutableProperty( descriptor.getName() );

						if ( mutable != null ) {
							mutable.setAttribute( PersistentProperty.class, persistentProperty );

							PropertyPersistenceMetadata metadata = new PropertyPersistenceMetadata();
							metadata.setEmbedded( isEmbedded( persistentProperty ) );

							mutable.setAttribute( PropertyPersistenceMetadata.class, metadata );

							if ( mutable.isHidden() && mutable.isReadable() && metadata.isEmbedded() ) {
								mutable.setHidden( false );
							}
						}
					}
				}

				// Only process using the first MappingContext
				return;
			}
		}
	}

	private boolean isEmbedded( PersistentProperty persistentProperty ) {
		return persistentProperty.isAnnotationPresent( Embedded.class )
				|| persistentProperty.isAnnotationPresent( EmbeddedId.class )
				|| persistentProperty.isAnnotationPresent( ElementCollection.class );
	}
}
