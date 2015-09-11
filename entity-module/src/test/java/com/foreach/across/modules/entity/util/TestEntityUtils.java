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
package com.foreach.across.modules.entity.util;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import org.junit.Test;
import org.springframework.data.domain.Sort;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 */
public class TestEntityUtils
{
	@Test
	public void unmodifiedSortIsReturned() {
		Sort sort = new Sort( Arrays.asList(
				new Sort.Order( Sort.Direction.ASC, "nullsFirst" ),
				new Sort.Order( Sort.Direction.DESC, "nullsLast" ),
				new Sort.Order( Sort.Direction.ASC, "nullHandlingFixed" ),
				new Sort.Order( Sort.Direction.DESC, "ignoreCase" ),
				new Sort.Order( Sort.Direction.ASC, "unmodified" )
		) );

		EntityPropertyRegistry propertyRegistry = mock( EntityPropertyRegistry.class );
		assertEquals( sort, EntityUtils.translateSort( sort, propertyRegistry ) );
	}

	@Test
	public void translateSort() {
		Sort sort = new Sort( Arrays.asList(
				new Sort.Order( Sort.Direction.ASC, "nullsFirst" ),
				new Sort.Order( Sort.Direction.DESC, "nullsLast" ),
				new Sort.Order( Sort.Direction.ASC, "nullHandlingFixed" ),
				new Sort.Order( Sort.Direction.DESC, "ignoreCase" ),
				new Sort.Order( Sort.Direction.ASC, "unmodified" )
		) );

		EntityPropertyRegistry propertyRegistry = mock( EntityPropertyRegistry.class );
		register( propertyRegistry, "nullsFirst", new Sort.Order( "prop-nullsFirst" ) );
		register( propertyRegistry, "nullsLast", new Sort.Order( "prop-nullsLast" ) );
		register( propertyRegistry, "nullHandlingFixed", new Sort.Order( "prop-nullHandlingFixed" ).nullsLast() );
		register( propertyRegistry, "ignoreCase", new Sort.Order( "prop-ignoreCase" ).ignoreCase() );

		Sort translated = EntityUtils.translateSort( sort, propertyRegistry );

		assertEquals(
				new Sort( Arrays.asList(
						new Sort.Order( Sort.Direction.ASC, "prop-nullsFirst", Sort.NullHandling.NULLS_FIRST ),
						new Sort.Order( Sort.Direction.DESC, "prop-nullsLast", Sort.NullHandling.NULLS_LAST ),
						new Sort.Order( Sort.Direction.ASC, "prop-nullHandlingFixed", Sort.NullHandling.NULLS_LAST ),
						new Sort.Order( Sort.Direction.DESC, "prop-ignoreCase", Sort.NullHandling.NULLS_LAST )
								.ignoreCase(),
						new Sort.Order( Sort.Direction.ASC, "unmodified" )
				) ),
				translated
		);
	}

	private void register( EntityPropertyRegistry propertyRegistry, String name, Sort.Order order ) {
		EntityPropertyDescriptor descriptor = mock( EntityPropertyDescriptor.class );
		when( descriptor.getAttribute( Sort.Order.class ) ).thenReturn( order );
		when( propertyRegistry.getProperty( name ) ).thenReturn( descriptor );
	}

	@Test
	public void createDisplayName() {
		assertEquals( "Name", EntityUtils.generateDisplayName( "name" ) );
		assertEquals( "Principal name", EntityUtils.generateDisplayName( "principalName" ) );
		assertEquals( "Address street", EntityUtils.generateDisplayName( "address.street" ) );
		assertEquals( "Customer address zip code", EntityUtils.generateDisplayName( "customer.address.zipCode" ) );
		assertEquals( "Groups size", EntityUtils.generateDisplayName( "groups.size()" ) );
		assertEquals( "Text with html", EntityUtils.generateDisplayName( "textWithHTML" ) );
		assertEquals( "Members 0 length", EntityUtils.generateDisplayName( "members[0].length" ) );
		assertEquals( "Generated label", EntityUtils.generateDisplayName( "Generated label" ) );
		assertEquals( "Basic security principal", EntityUtils.generateDisplayName( "BasicSecurityPrincipal" ) );
		assertEquals( "Permission group", EntityUtils.generateDisplayName( "PermissionGroup" ) );
		assertEquals( "Some field name", EntityUtils.generateDisplayName( "_someFieldName" ) );
		assertEquals( "Test for me", EntityUtils.generateDisplayName( "_TEST_FOR_ME" ) );
		assertEquals( "OAuth2 client", EntityUtils.generateDisplayName( "OAuth2Client" ) );
	}

	@Test
	public void mergeDisplayNames() {
		assertEquals( "Name", EntityUtils.combineDisplayNames( "name" ) );
		assertEquals( "Name principal name", EntityUtils.combineDisplayNames( "name", "principalName" ) );
		assertEquals( "Address street customer address zip code", EntityUtils.combineDisplayNames( "address.street",
		                                                                                           "customer.address.zipCode" ) );
		assertEquals( "Groups size text with html members 0 length", EntityUtils.combineDisplayNames( "groups.size()",
		                                                                                              "textWithHTML",
		                                                                                              "members[0].length" ) );
		assertEquals( "Basic security principal permission group some field name test for me",
		              EntityUtils.combineDisplayNames( "BasicSecurityPrincipal", "PermissionGroup", "_someFieldName",
		                                               "_TEST_FOR_ME" ) );
	}
}
