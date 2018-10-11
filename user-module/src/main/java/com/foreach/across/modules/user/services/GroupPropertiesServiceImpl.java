package com.foreach.across.modules.user.services;

import com.foreach.across.modules.properties.business.StringPropertiesSource;
import com.foreach.across.modules.properties.registries.EntityPropertiesRegistry;
import com.foreach.across.modules.properties.repositories.EntityPropertiesRepository;
import com.foreach.across.modules.properties.services.AbstractEntityPropertiesService;
import com.foreach.across.modules.user.business.GroupProperties;
import com.foreach.common.spring.properties.PropertyTypeRegistry;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class GroupPropertiesServiceImpl extends AbstractEntityPropertiesService<GroupProperties, Long> implements GroupPropertiesService
{
	private final EntityPropertiesRepository<Long> entityPropertiesRepository;

	public GroupPropertiesServiceImpl( EntityPropertiesRegistry entityPropertiesRegistry,
	                                   EntityPropertiesRepository<Long> entityPropertiesRepository ) {
		super( entityPropertiesRegistry, entityPropertiesRepository );
		this.entityPropertiesRepository = entityPropertiesRepository;
	}

	@Override
	protected GroupProperties createEntityProperties( Long entityId,
	                                                  PropertyTypeRegistry<String> propertyTypeRegistry,
	                                                  StringPropertiesSource source ) {
		return new GroupProperties( (Long) entityId, propertyTypeRegistry, source );
	}

	@Override
	public void saveProperties( GroupProperties entityProperties, Supplier<Long> groupId ) {
		entityPropertiesRepository.saveProperties( groupId.get(),
		                                           entityProperties.getSource() );
	}
}
