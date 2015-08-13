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

import com.foreach.across.core.support.AttributeOverridingSupport;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.Assert;

public class SimpleEntityPropertyDescriptor extends AttributeOverridingSupport implements MutableEntityPropertyDescriptor
{
	private final EntityPropertyDescriptor parent;

	private String name, displayName;
	private Boolean readable, writable, hidden;

	private ValueFetcher valueFetcher;
	private Class<?> propertyType;
	private TypeDescriptor propertyTypeDescriptor;
	private EntityPropertyRegistry propertyRegistry;

	public SimpleEntityPropertyDescriptor( String name ) {
		this( name, null );
	}

	public SimpleEntityPropertyDescriptor( String name, EntityPropertyDescriptor parent ) {
		Assert.notNull( name );
		this.name = name;
		this.parent = parent;

		setParent( parent );
	}

	/**
	 * @return Property name.
	 */
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDisplayName() {
		return displayName != null ? displayName : ( parent != null ? parent.getDisplayName() : null );
	}

	@Override
	public void setDisplayName( String displayName ) {
		this.displayName = displayName;
	}

	@Override
	public boolean isReadable() {
		return readable != null ? readable : ( parent != null && parent.isReadable() );
	}

	@Override
	public void setReadable( boolean readable ) {
		this.readable = readable;
	}

	@Override
	public boolean isWritable() {
		return writable != null ? writable : ( parent != null && parent.isWritable() );
	}

	@Override
	public void setWritable( boolean writable ) {
		this.writable = writable;
	}

	@Override
	public boolean isHidden() {
		return hidden != null ? hidden : ( parent != null && parent.isHidden() );
	}

	@Override
	public void setHidden( boolean hidden ) {
		this.hidden = hidden;
	}

	@Override
	public Class<?> getPropertyType() {
		return propertyType != null ? propertyType : ( parent != null ? parent.getPropertyType() : null );
	}

	@Override
	public void setPropertyType( Class<?> propertyType ) {
		this.propertyType = propertyType;
	}

	public TypeDescriptor getPropertyTypeDescriptor() {
		return propertyTypeDescriptor != null
				? propertyTypeDescriptor : ( parent != null ? parent.getPropertyTypeDescriptor() : null );
	}

	public void setPropertyTypeDescriptor( TypeDescriptor propertyTypeDescriptor ) {
		this.propertyTypeDescriptor = propertyTypeDescriptor;
	}

	@Override
	public ValueFetcher getValueFetcher() {
		return valueFetcher != null
				? valueFetcher : ( parent != null ? parent.getValueFetcher() : null );
	}

	@Override
	public void setValueFetcher( ValueFetcher<?> valueFetcher ) {
		this.valueFetcher = valueFetcher;

		if ( readable == null && parent == null && valueFetcher != null ) {
			readable = true;
		}
	}

	@Override
	public EntityPropertyRegistry getPropertyRegistry() {
		return propertyRegistry;
	}

	@Override
	public void setPropertyRegistry( EntityPropertyRegistry propertyRegistry ) {
		this.propertyRegistry = propertyRegistry;
	}
}
