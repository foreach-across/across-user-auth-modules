package com.foreach.across.modules.entity.form;

import com.foreach.across.modules.entity.business.FormElement;
import com.foreach.across.modules.entity.services.EntityRegistry;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.Collection;

public class SelectFormElement implements FormElement
{
	private final EntityRegistry registry;
	private final PropertyDescriptor propertyDescriptor;
	private Object entity;
	private Collection<?> possibleValues;

	public SelectFormElement( EntityRegistry registry,
	                          PropertyDescriptor propertyDescriptor,
	                          Collection<?> possibleValues ) {
		this.registry = registry;
		this.propertyDescriptor = propertyDescriptor;
		this.possibleValues = possibleValues;
	}

	@Override
	public String getName() {
		return propertyDescriptor.getName();
	}

	@Override
	public void setEntity( Object entity ) {
		this.entity = entity;
	}

	@Override
	public String getElementType() {
		return "select";
	}

	public String getLabel() {
		return propertyDescriptor.getDisplayName();
	}

	public Object getValue() throws Exception {
		return entity != null ? propertyDescriptor.getReadMethod().invoke( entity ) : null;
	}

	public String entityLabel( Object entity ) {
		return registry.wrap( entity ).getEntityLabel();
	}

	public Serializable entityId( Object entity ) {
		return registry.wrap( entity ).getId();
	}

	public Collection<?> getPossibleValues() {
		return possibleValues;
	}
}
