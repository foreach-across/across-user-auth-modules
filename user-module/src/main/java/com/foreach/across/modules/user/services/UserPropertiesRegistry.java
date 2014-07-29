package com.foreach.across.modules.user.services;

import com.foreach.across.modules.properties.config.EntityPropertiesDescriptor;
import com.foreach.across.modules.properties.registries.EntityPropertiesRegistry;
import org.springframework.stereotype.Service;

/**
 * @author Arne Vandamme
 */
@Service
public class UserPropertiesRegistry extends EntityPropertiesRegistry
{
	public UserPropertiesRegistry( EntityPropertiesDescriptor descriptor ) {
		super( descriptor );
	}
}
