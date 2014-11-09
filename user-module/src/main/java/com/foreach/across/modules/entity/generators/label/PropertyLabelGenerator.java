package com.foreach.across.modules.entity.generators.label;

import com.foreach.across.modules.entity.generators.EntityLabelGenerator;
import com.foreach.across.modules.entity.util.EntityUtils;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;

/**
 * Use the property value as a label.
 */
public class PropertyLabelGenerator implements EntityLabelGenerator
{
	private PropertyDescriptor propertyDescriptor;

	public PropertyLabelGenerator( PropertyDescriptor propertyDescriptor ) {
		this.propertyDescriptor = propertyDescriptor;
	}

	@Override
	public String getLabel( Object entity ) {
		Object value = EntityUtils.getPropertyValue( propertyDescriptor, entity );
		return value != null ? value.toString() : "";
	}

	public static PropertyLabelGenerator forProperty( Class<?> clazz, String propertyName ) {
		PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor( clazz, propertyName );

		if ( descriptor != null ) {
			return new PropertyLabelGenerator( descriptor );
		}

		return null;
	}
}
