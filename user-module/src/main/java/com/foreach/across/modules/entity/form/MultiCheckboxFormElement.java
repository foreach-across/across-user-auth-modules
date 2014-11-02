package com.foreach.across.modules.entity.form;

import com.foreach.across.modules.entity.business.FormElement;

import java.beans.PropertyDescriptor;
import java.util.Collection;

public class MultiCheckboxFormElement implements FormElement
{
	private final PropertyDescriptor propertyDescriptor;
	private Object entity;
	private Collection<?> possibleValues;

	public MultiCheckboxFormElement( PropertyDescriptor propertyDescriptor, Collection<?> possibleValues ) {
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
		return "multi-checkbox";
	}

	public String getLabel() {
		return propertyDescriptor.getDisplayName();
	}

	public Object getValue() throws Exception {
		return entity != null ? propertyDescriptor.getReadMethod().invoke( entity ) : null;
	}

	public Collection<?> getPossibleValues() {
		return possibleValues;
	}
}
