package com.foreach.across.modules.user.services;

import com.foreach.across.modules.properties.business.StringPropertiesSource;
import com.foreach.across.modules.properties.registries.EntityPropertiesRegistry;
import com.foreach.across.modules.properties.repositories.EntityPropertiesRepository;
import com.foreach.across.modules.properties.services.AbstractEntityPropertiesService;
import com.foreach.across.modules.user.business.GroupProperties;
import com.foreach.common.spring.util.PropertyTypeRegistry;
import org.springframework.core.convert.ConversionService;
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
	                                                  ConversionService conversionService,
	                                                  StringPropertiesSource source ) {
		return new GroupProperties( entityId, propertyTypeRegistry, conversionService, source );
	}
}
