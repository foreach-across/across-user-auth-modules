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
package com.foreach.across.modules.entity.registry.properties;

import com.foreach.across.modules.entity.util.EntityUtils;
import com.foreach.across.modules.entity.views.support.MethodValueFetcher;
import org.springframework.core.convert.Property;
import org.springframework.core.convert.TypeDescriptor;
import org.thymeleaf.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

/**
 * @author Arne Vandamme
 */
// todo: when creating from parent, dispatch to attribute cloning
public class EntityPropertyDescriptorFactoryImpl implements EntityPropertyDescriptorFactory
{
	@Override
	public MutableEntityPropertyDescriptor create( PropertyDescriptor prop, Class<?> entityType ) {
		Method writeMethod = prop.getWriteMethod();
		Method readMethod = prop.getReadMethod();
		Property property = new Property( entityType, readMethod, writeMethod, prop.getName() );
		MutableEntityPropertyDescriptor descriptor = create( property );

		if ( StringUtils.equals( prop.getName(), prop.getDisplayName() ) ) {
			descriptor.setDisplayName( EntityUtils.generateDisplayName( prop.getName() ) );
		}
		else {
			descriptor.setDisplayName( prop.getDisplayName() );
		}
		return descriptor;
	}

	@Override
	public MutableEntityPropertyDescriptor create( Property property ) {
		SimpleEntityPropertyDescriptor descriptor = new SimpleEntityPropertyDescriptor( property.getName() );
		descriptor.setDisplayName( EntityUtils.generateDisplayName( property.getName() ) );

		descriptor.setWritable( property.getWriteMethod() != null );
		descriptor.setReadable( property.getReadMethod() != null );

		if ( !descriptor.isWritable() || !descriptor.isReadable() ) {
			descriptor.setHidden( true );
		}

		descriptor.setPropertyType( property.getType() );
		descriptor.setPropertyTypeDescriptor( new TypeDescriptor( property ) );

		if ( descriptor.isReadable() ) {
			descriptor.setValueFetcher( new MethodValueFetcher( property.getReadMethod() ) );
		}

		return descriptor;
	}

	@Override
	public MutableEntityPropertyDescriptor createWithParent( String name, EntityPropertyDescriptor parent ) {
		return new SimpleEntityPropertyDescriptor( name, parent );
	}
}
