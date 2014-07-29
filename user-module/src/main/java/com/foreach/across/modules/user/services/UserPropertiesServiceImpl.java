package com.foreach.across.modules.user.services;

import com.foreach.across.modules.properties.business.StringPropertiesSource;
import com.foreach.across.modules.properties.registries.EntityPropertiesRegistry;
import com.foreach.across.modules.properties.repositories.EntityPropertiesRepository;
import com.foreach.across.modules.properties.services.AbstractEntityPropertiesService;
import com.foreach.across.modules.user.business.UserProperties;
import com.foreach.spring.util.PropertyTypeRegistry;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

/**
 * @author Arne Vandamme
 */
@Service
public class UserPropertiesServiceImpl extends AbstractEntityPropertiesService<UserProperties, Long> implements UserPropertiesService
{
	public UserPropertiesServiceImpl( EntityPropertiesRegistry entityPropertiesRegistry,
	                                  EntityPropertiesRepository<Long> entityPropertiesRepository ) {
		super( entityPropertiesRegistry, entityPropertiesRepository );
	}

	@Override
	protected UserProperties createEntityProperties( Long entityId,
	                                                 PropertyTypeRegistry<String> propertyTypeRegistry,
	                                                 ConversionService conversionService,
	                                                 StringPropertiesSource source ) {
		return new UserProperties( entityId, propertyTypeRegistry, conversionService, source );
	}
}
