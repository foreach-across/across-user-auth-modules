package com.foreach.across.modules.user.business;

import com.foreach.across.modules.properties.business.EntityProperties;
import com.foreach.common.spring.util.PropertiesSource;
import com.foreach.common.spring.util.PropertyTypeRegistry;
import org.springframework.core.convert.ConversionService;

public class GroupProperties extends EntityProperties<Long>
{
	private final long groupId;

	public GroupProperties( long groupId,
	                        PropertyTypeRegistry<String> propertyTypeRegistry,
	                        ConversionService conversionService,
	                        PropertiesSource source ) {
		super( propertyTypeRegistry, conversionService, source );

		this.groupId = groupId;
	}

	@Override
	public Long getId() {
		return groupId;
	}
}
