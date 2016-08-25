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

import com.foreach.across.modules.ldap.business.LdapConnector;
import com.foreach.across.modules.ldap.business.LdapConnectorSettings;
import com.foreach.across.modules.ldap.services.support.LdapContextSourceHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.HardcodedFilter;

import javax.naming.directory.SearchControls;

/**
 * @author Marc Vanbrabant
 */
public class LdapSearchServiceImpl implements LdapSearchService
{
	@Override
	public void performSearch( LdapConnector connector,
	                           String additionalBase,
	                           Filter filter,
	                           ContextMapper<String> ctx ) {
		SearchControls controls = new SearchControls();
		controls.setSearchScope( SearchControls.SUBTREE_SCOPE );
		controls.setTimeLimit( connector.getSearchTimeout() );
		controls.setCountLimit( 0 );
		controls.setReturningObjFlag( true );
		controls.setReturningAttributes( null );

		//TODO: detect if connector is pageable, or store the page size on the connector?
		PagedResultsDirContextProcessor processor = new PagedResultsDirContextProcessor( 20 );

		LdapTemplate ldapTemplate = LdapContextSourceHelper.createLdapTemplate( connector );

		if ( additionalBase == null ) {
			additionalBase = StringUtils.EMPTY;
		}

		do {
			ldapTemplate.search( additionalBase, filter.encode(), controls, ctx, processor );
			processor = new PagedResultsDirContextProcessor( processor.getPageSize(), processor.getCookie() );
		}
		while ( processor.getCookie().getCookie() != null );
	}

	@Override
	public AndFilter getUserFilter( LdapConnectorSettings ldapConnectorSettings ) {
		AndFilter andFilter = new AndFilter();
		andFilter.and( new EqualsFilter( "objectclass", ldapConnectorSettings.getUserObjectClass() ) );
		andFilter.and( new HardcodedFilter( ldapConnectorSettings.getUserObjectFilter() ) );
		return andFilter;
	}

	@Override
	public AndFilter getGroupFilter( LdapConnectorSettings ldapConnectorSettings ) {
		AndFilter andFilter = new AndFilter();
		andFilter.and( new EqualsFilter( "objectclass", ldapConnectorSettings.getGroupObjectClass() ) );
		andFilter.and( new HardcodedFilter( ldapConnectorSettings.getGroupObjectFilter() ) );

		return andFilter;
	}
}
