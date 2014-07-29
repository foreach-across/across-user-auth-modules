package com.foreach.across.modules.user.business;

import com.foreach.across.modules.properties.business.EntityProperties;
import com.foreach.spring.util.PropertiesSource;
import com.foreach.spring.util.PropertyTypeRegistry;
import org.springframework.core.convert.ConversionService;

/**
 * @author Arne Vandamme
 */
public class UserProperties extends EntityProperties<Long>
{
	private final long userId;

	public UserProperties( long userId,
	                       PropertyTypeRegistry<String> propertyTypeRegistry,
	                       ConversionService conversionService,
	                       PropertiesSource source ) {
		super( propertyTypeRegistry, conversionService, source );

		this.userId = userId;
	}

	@Override
	public Long getId() {
		return userId;
	}
}
