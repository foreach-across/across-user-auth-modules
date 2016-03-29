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

package com.foreach.across.modules.ldap.business;

import com.foreach.across.modules.hibernate.types.IdLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Marc Vanbrabant
 */
public enum LdapConnectorType implements IdLookup<Integer>
{
	// TODO: Make this a registry at some point
	MICROSOFT_ACTIVE_DIRECTORY( 1, "msad" ),
	OPENDS( 2, "apache15" ),
	APACHEDS_1_5( 3, "opends" );

	private int id;
	private Map<String, String> settings = Collections.emptyMap();
	private Logger LOG = LoggerFactory.getLogger( LdapConnectorType.class );

	@SuppressWarnings("unchecked")
	LdapConnectorType( int id, String identifier ) {
		this.id = id;
		try {
			settings =
					(LinkedHashMap<String, String>) new Yaml().load(
							new ClassPathResource( "activedirectorysettings/" + identifier + ".yaml" )
									.getInputStream() );
		}
		catch ( IOException e ) {
			LOG.error( "Failed to load yaml file {}", identifier );
		}
	}

	@Override
	public Integer getId() {
		return id;
	}

	public Map<String, String> getSettings() {
		return settings;
	}
}
