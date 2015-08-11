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
package com.foreach.across.modules.entity.views.bootstrapui.elements.builder;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.modules.bootstrapui.BootstrapUiModule;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderContext;
import com.foreach.across.modules.hibernate.business.Auditable;
import com.foreach.across.modules.web.ui.ViewElementBuilderContextImpl;
import com.foreach.across.test.support.AbstractViewElementTemplateTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 */
@ContextConfiguration(classes = TestAuditablePropertyViewElementBuilder.Config.class)
public class TestAuditablePropertyViewElementBuilder extends AbstractViewElementTemplateTest
{
	private final Date dateCreated = new Date();
	private final Date dateLastModified = new Date( System.currentTimeMillis() + 1000 );

	private ConversionService conversionService;
	private Entity entity;
	private AuditablePropertyViewElementBuilder builder;
	private ViewElementBuilderContextImpl builderContext;

	@Before
	public void before() {
		builder = new AuditablePropertyViewElementBuilder();

		conversionService = mock( ConversionService.class );
		when( conversionService.convert( dateCreated, String.class ) ).thenReturn( "creationDate" );
		when( conversionService.convert( dateLastModified, String.class ) ).thenReturn( "modificationDate" );

		builder.setConversionService( conversionService );

		entity = new Entity();
		entity.setCreatedBy( "admin" );
		entity.setCreatedDate( dateCreated );
		entity.setLastModifiedBy( "system" );
		entity.setLastModifiedDate( dateLastModified );

		builderContext = new ViewElementBuilderContextImpl();
		builderContext.setAttribute( EntityViewElementBuilderContext.ENTITY, entity );
	}

	@Test
	public void creationDateWithPrincipal() {
		expect( "creationDate by admin" );
	}

	@Test
	public void modificationDateWithPrincipal() {
		builder.setForLastModifiedProperty( true );
		expect( "modificationDate by system" );
	}

	@Test
	public void creationDateWithoutPrincipal() {
		entity.setCreatedBy( null );
		expect( "creationDate" );
	}

	@Test
	public void modificationDateWithoutPrincipal() {
		builder.setForLastModifiedProperty( true );
		entity.setLastModifiedBy( null );
		expect( "modificationDate" );
	}

	private void expect( String output ) {
		renderAndExpect( builder.build( builderContext ), output );
	}

	static class Entity implements Auditable<String>
	{
		private Date createdDate, lastModifiedDate;
		private String createdBy, lastModifiedBy;

		@Override
		public Date getCreatedDate() {
			return createdDate;
		}

		@Override
		public void setCreatedDate( Date createdDate ) {
			this.createdDate = createdDate;
		}

		@Override
		public Date getLastModifiedDate() {
			return lastModifiedDate;
		}

		@Override
		public void setLastModifiedDate( Date lastModifiedDate ) {
			this.lastModifiedDate = lastModifiedDate;
		}

		@Override
		public String getCreatedBy() {
			return createdBy;
		}

		@Override
		public void setCreatedBy( String createdBy ) {
			this.createdBy = createdBy;
		}

		@Override
		public String getLastModifiedBy() {
			return lastModifiedBy;
		}

		@Override
		public void setLastModifiedBy( String lastModifiedBy ) {
			this.lastModifiedBy = lastModifiedBy;
		}
	}

	@Configuration
	protected static class Config implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			context.addModule( new BootstrapUiModule() );
		}
	}
}