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
import com.foreach.across.modules.entity.registry.properties.*;
import com.foreach.across.modules.entity.views.support.SpelValueFetcher;
import org.springframework.core.Ordered;

/**
 * Responsible for creating the default
 * {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry#LABEL} property.
 *
 * @author Arne Vandamme
 */
@OrderInModule(Ordered.HIGHEST_PRECEDENCE)
public class EntityPropertyRegistryLabelPropertyBuilder implements EntityPropertyRegistryBuilder
{
	@Override
	public void buildRegistry( Class<?> entityType, MutableEntityPropertyRegistry registry ) {
		SimpleEntityPropertyDescriptor label = new SimpleEntityPropertyDescriptor();
		label.setName( EntityPropertyRegistry.LABEL );
		label.setDisplayName( "Label" );
		label.setValueFetcher( new SpelValueFetcher( "toString()" ) );
		label.setReadable( true );
		label.setWritable( false );
		label.setHidden( true );

		EntityPropertyDescriptor defaultLabelProperty = findDefaultLabelProperty( registry );

		if ( defaultLabelProperty != null ) {
			copyPropertyToLabel( defaultLabelProperty, label );
		}

		registry.register( label );
	}

	private EntityPropertyDescriptor findDefaultLabelProperty( MutableEntityPropertyRegistry registry ) {
		String propertyName = "label";

		if ( registry.contains( "name" ) ) {
			propertyName = "name";
		}
		else if ( registry.contains( "title" ) ) {
			propertyName = "title";
		}

		return registry.getProperty( propertyName );
	}

	public static void copyPropertyToLabel( EntityPropertyDescriptor property, MutableEntityPropertyDescriptor label ) {
		label.setValueFetcher( property.getValueFetcher() );
		label.setAttributes( property.attributeMap() );
	}
}
