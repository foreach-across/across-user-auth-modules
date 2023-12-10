/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.foreach.across.modules.ldap.services;

import com.foreach.across.modules.hibernate.business.SettableIdBasedEntity;
import com.foreach.across.modules.ldap.config.LdapPropertyConstants;
import com.foreach.across.modules.properties.business.EntityProperties;
import com.foreach.across.modules.properties.services.EntityPropertiesService;
import com.foreach.across.modules.user.business.Group;
import com.foreach.across.modules.user.services.GroupPropertiesService;
import com.foreach.across.modules.user.services.UserPropertiesService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextAdapter;

/**
 * @author Marc Vanbrabant
 */
public class LdapPropertiesServiceImpl implements LdapPropertiesService
{
	@Autowired
	private UserPropertiesService userPropertiesService;

	@Autowired
	private GroupPropertiesService groupPropertiesService;

	@SuppressWarnings("unchecked")
	public void saveLdapProperties( SettableIdBasedEntity entity,
	                                DirContextAdapter adapter ) {
		String distinguishedName = adapter.getStringAttribute( "distinguishedname" );
		Object objectGuid = adapter.getObjectAttribute( "objectGUID" );
		EntityPropertiesService service = entity instanceof Group ? groupPropertiesService : userPropertiesService;

		EntityProperties<Long> properties = service.getProperties( entity.getId() );
		if ( StringUtils.isNotEmpty( distinguishedName ) ) {
			properties.set( LdapPropertyConstants.DISTINGUISHED_NAME, distinguishedName );
		}
		if ( objectGuid instanceof byte[] ) {
			String objectGuidAsString = getGUID( (byte[]) objectGuid );
			properties.set( LdapPropertyConstants.OBJECT_GUID, objectGuidAsString );
		}
		service.saveProperties( properties );
	}

	/**
	 * Converts the GUID to a readable string format
	 *
	 * @return the formatted GUID
	 */
	private static String getGUID( byte[] inArr ) {
		StringBuilder guid = new StringBuilder();
		for ( byte anInArr : inArr ) {
			StringBuilder dblByte = new StringBuilder( Integer.toHexString( anInArr & 0xff ) );
			if ( dblByte.length() == 1 ) {
				guid.append( "0" );
			}
			guid.append( dblByte );
		}
		return guid.toString();
	}
}
