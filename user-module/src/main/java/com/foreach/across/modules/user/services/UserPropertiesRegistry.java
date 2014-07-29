package com.foreach.across.modules.user.services;

import com.foreach.across.modules.properties.registries.EntityPropertiesRegistry;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

/**
 * @author Arne Vandamme
 */
@Service
public class UserPropertiesRegistry extends EntityPropertiesRegistry
{
	public UserPropertiesRegistry( ConversionService conversionService ) {
		super( conversionService );
	}
}
