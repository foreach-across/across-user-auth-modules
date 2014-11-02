package com.foreach.across.modules.entity.form;

import com.foreach.across.modules.entity.business.FormElement;

import java.beans.PropertyDescriptor;

public class HiddenFormElement implements FormElement
{
	private final PropertyDescriptor propertyDescriptor;
	private Object entity;

	public HiddenFormElement( PropertyDescriptor propertyDescriptor ) {
		this.propertyDescriptor = propertyDescriptor;
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
		return "hidden";
	}

	public Object getValue() throws Exception {
		return entity != null ? propertyDescriptor.getReadMethod().invoke( entity ) : null;
	}
}
