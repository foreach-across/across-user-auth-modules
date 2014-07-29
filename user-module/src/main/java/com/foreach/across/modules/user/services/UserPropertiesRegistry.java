package com.foreach.across.modules.user.services;

import com.foreach.across.modules.properties.registries.EntityPropertiesRegistry;
import com.foreach.across.modules.user.UserModule;
import com.foreach.across.modules.user.repositories.UserPropertiesRepository;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

/**
 * @author Arne Vandamme
 */
@Service
public class UserPropertiesRegistry extends EntityPropertiesRegistry
{
	public static final String ID = UserModule.NAME + ".UserProperties";

	public UserPropertiesRegistry( UserPropertiesRepository repository, ConversionService conversionService ) {
		super( ID, repository, conversionService );
	}
}
