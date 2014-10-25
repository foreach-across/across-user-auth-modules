package com.foreach.across.modules.user.services;

import com.foreach.across.modules.properties.config.EntityPropertiesDescriptor;
import com.foreach.across.modules.properties.registries.EntityPropertiesRegistry;
import org.springframework.stereotype.Service;

@Service
public class GroupPropertiesRegistry extends EntityPropertiesRegistry
{
	public GroupPropertiesRegistry( EntityPropertiesDescriptor descriptor ) {
		super( descriptor );
	}
}
