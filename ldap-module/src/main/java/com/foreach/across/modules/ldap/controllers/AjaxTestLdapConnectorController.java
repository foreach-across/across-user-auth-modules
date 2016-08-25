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

package com.foreach.across.modules.ldap.controllers;

import com.foreach.across.modules.adminweb.annotations.AdminWebController;
import com.foreach.across.modules.entity.controllers.EntityViewRequest;
import com.foreach.across.modules.entity.controllers.entity.EntityControllerSupport;
import com.foreach.across.modules.entity.controllers.entity.EntityListController;
import com.foreach.across.modules.entity.controllers.entity.EntityViewController;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.views.EntityFormView;
import com.foreach.across.modules.ldap.business.LdapConnector;
import com.foreach.across.modules.ldap.business.LdapConnectorSettings;
import com.foreach.across.modules.ldap.business.LdapUserDirectory;
import com.foreach.across.modules.ldap.services.LdapSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ldap.filter.PresentFilter;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Marc Vanbrabant
 * @since 1.0.0
 */
@AdminWebController
@RequestMapping(value = { EntityListController.PATH + "/test", EntityViewController.PATH + "/test" })
public class AjaxTestLdapConnectorController extends EntityControllerSupport
{
	@Autowired
	private LdapSearchService ldapSearchService;

	@ModelAttribute(VIEW_REQUEST)
	public Object buildViewRequest(
			@PathVariable(VAR_ENTITY) EntityConfiguration entityConfiguration,
			NativeWebRequest request,
			ModelMap model ) {
		return super.buildViewRequest( entityConfiguration, true, true, null, request, model );
	}

	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public
	@ResponseBody
	ResponseEntity<TestResponse> updateProcessedStatus( @ModelAttribute(VIEW_REQUEST) EntityViewRequest viewRequest ) throws Exception {
		LdapConnector ldapConnector = (LdapConnector) viewRequest.getEntity();
		LdapUserDirectory ldapUserDirectory = new LdapUserDirectory();
		ldapUserDirectory.setLdapConnector( ldapConnector );
		AtomicInteger numberOfUsers = new AtomicInteger();
		AtomicInteger numberOfGroups = new AtomicInteger();
		DummyLdapConnectorSettings ldapConnectorSettings = new DummyLdapConnectorSettings( ldapConnector );
		try {
			ldapSearchService.performSearch( ldapConnector, ldapConnector.getAdditionalUserDn(),
			                                 ldapSearchService.getUserFilter( ldapConnectorSettings )
			                                                  .and( new PresentFilter( "cn" ) ),
			                                 ctx -> {
				                                 numberOfUsers.incrementAndGet();
				                                 return null;
			                                 } );
		}
		catch ( Exception e ) {
			return ResponseEntity.ok( new TestResponse( "<pre>" + e.getMessage() + "</pre>" ) );
		}
		try {
			ldapSearchService.performSearch( ldapConnector, ldapConnector.getAdditionalGroupDn(),
			                                 ldapSearchService.getGroupFilter( ldapConnectorSettings )
			                                                  .and( new PresentFilter( "cn" ) ),
			                                 ctx -> {
				                                 numberOfGroups.incrementAndGet();
				                                 return null;
			                                 } );
		}
		catch ( Exception e ) {
			return ResponseEntity.ok( new TestResponse( "<pre>" + e.getMessage() + "</pre>" ) );
		}

		return ResponseEntity.ok(
				new TestResponse( "Found " + numberOfUsers + " users and " + numberOfGroups + " groups." ) );
	}

	@Override
	protected String getDefaultViewName() {
		return EntityFormView.CREATE_VIEW_NAME;
	}

	public static final class TestResponse
	{
		private final String response;

		public TestResponse( String response ) {
			this.response = response;
		}

		public String getResponse() {
			return response;
		}
	}

	private static class DummyLdapConnectorSettings extends LdapConnectorSettings
	{
		private final LdapConnector ldapConnector;

		DummyLdapConnectorSettings( LdapConnector ldapConnector ) {
			super( 0, null, null );
			this.ldapConnector = ldapConnector;
		}

		@Override
		public String getUserObjectClass() {
			return ldapConnector.getLdapConnectorType().getSettings().get( "ldapUserObjectClass" );
		}

		@Override
		public String getGroupObjectClass() {
			return ldapConnector.getLdapConnectorType().getSettings().get( "ldapGroupObjectClass" );
		}

		@Override
		public String getUserObjectFilter() {
			return ldapConnector.getLdapConnectorType().getSettings().get( "ldapUserObjectFilter" );
		}

		@Override
		public String getGroupObjectFilter() {
			return ldapConnector.getLdapConnectorType().getSettings().get( "ldapGroupObjectFilter" );
		}
	}
}
