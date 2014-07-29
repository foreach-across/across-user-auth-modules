package com.foreach.across.modules.user.repositories;

import com.foreach.across.modules.properties.config.EntityPropertiesDescriptor;
import com.foreach.across.modules.properties.repositories.EntityPropertiesRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Arne Vandamme
 */
@Repository
public class UserPropertiesRepository extends EntityPropertiesRepository<Long>
{
	public UserPropertiesRepository( EntityPropertiesDescriptor configuration ) {
		super( configuration );
	}
}
