package com.foreach.across.modules.user.services;

import com.foreach.across.modules.properties.business.StringPropertiesSource;
import com.foreach.across.modules.properties.registries.EntityPropertiesRegistry;
import com.foreach.across.modules.properties.repositories.EntityPropertiesRepository;
import com.foreach.across.modules.properties.services.AbstractEntityPropertiesService;
import com.foreach.across.modules.user.business.GroupProperties;
import com.foreach.common.spring.properties.PropertyTypeRegistry;
import org.springframework.stereotype.Service;

@Service
public class GroupPropertiesServiceImpl extends AbstractEntityPropertiesService<GroupProperties, Long> implements GroupPropertiesService
{
	public GroupPropertiesServiceImpl( EntityPropertiesRegistry entityPropertiesRegistry,
	                                   EntityPropertiesRepository<Long> entityPropertiesRepository ) {
		super( entityPropertiesRegistry, entityPropertiesRepository );
	}

	@Override
	protected GroupProperties createEntityProperties( Long entityId,
	                                                  PropertyTypeRegistry<String> propertyTypeRegistry,
	                                                  StringPropertiesSource source ) {
		return new GroupProperties( entityId, propertyTypeRegistry, source );
	}
}
