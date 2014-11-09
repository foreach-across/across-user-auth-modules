package com.foreach.across.modules.entity.util;

import java.beans.PropertyDescriptor;

public class EntityUtils
{
	public static Object getPropertyValue( PropertyDescriptor descriptor, Object instance ) {
		try {
			return descriptor.getReadMethod().invoke( instance );
		}
		catch ( Exception e ) {
			return null;
		}
	}
}
