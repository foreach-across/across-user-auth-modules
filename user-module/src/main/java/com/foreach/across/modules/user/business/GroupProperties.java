package com.foreach.across.modules.user.business;

import com.foreach.across.modules.properties.business.EntityProperties;
import com.foreach.common.spring.properties.PropertiesSource;
import com.foreach.common.spring.properties.PropertyTypeRegistry;

public class GroupProperties extends EntityProperties<Long>
{
	private final long groupId;

	public GroupProperties( long groupId,
	                        PropertyTypeRegistry<String> propertyTypeRegistry,
	                        PropertiesSource source ) {
		super( propertyTypeRegistry, source );

		this.groupId = groupId;
	}

	@Override
	public Long getId() {
		return groupId;
	}
}
