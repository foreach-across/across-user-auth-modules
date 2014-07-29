package com.foreach.across.modules.user.repositories;

import com.foreach.across.modules.properties.repositories.EntityPropertiesRepository;
import com.foreach.across.modules.user.config.UserSchemaConfiguration;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

/**
 * @author Arne Vandamme
 */
@Repository
public class UserPropertiesRepository extends EntityPropertiesRepository<Long>
{
	public UserPropertiesRepository( DataSource dataSource, String table ) {
		super( dataSource, table, UserSchemaConfiguration.COLUMN_USER_ID );
	}
}
