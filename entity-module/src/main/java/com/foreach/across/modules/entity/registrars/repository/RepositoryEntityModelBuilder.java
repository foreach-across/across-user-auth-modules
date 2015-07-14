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
package com.foreach.across.modules.entity.registrars.repository;

import com.foreach.across.modules.entity.registry.EntityModelImpl;
import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import com.foreach.across.modules.entity.registry.PersistentEntityFactory;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.SimpleEntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.support.SpelValueFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryInformation;
import org.springframework.data.repository.support.CrudInvokerUtils;
import org.springframework.format.Printer;
import org.springframework.format.support.DefaultFormattingConversionService;

import javax.annotation.PostConstruct;
import java.util.Locale;

/**
 * Builds an {@link com.foreach.across.modules.entity.registry.EntityModel} for a Spring data repository.
 */
public class RepositoryEntityModelBuilder
{
	private static final Logger LOG = LoggerFactory.getLogger( RepositoryEntityModelBuilder.class );

	private static final Printer<Object> DEFAULT_PRINTER = new Printer<Object>()
	{
		@Override
		public String print( Object object, Locale locale ) {
			return object != null ? object.toString() : "";
		}
	};

	@Autowired(required = false)
	private ConversionService mvcConversionService;

	@PostConstruct
	protected void createDefaultConversionService() {
		if ( mvcConversionService == null ) {
			LOG.info(
					"No ConversionService found for the EntityModule - creating default conversion service for entity labels." );
			mvcConversionService = new DefaultFormattingConversionService();
		}
	}

	@SuppressWarnings("unchecked")
	public <T> void buildEntityModel( MutableEntityConfiguration<T> entityConfiguration ) {
		RepositoryFactoryInformation<T, ?> repositoryFactoryInformation
				= entityConfiguration.getAttribute( RepositoryFactoryInformation.class );
		Repository<T, ?> repository = entityConfiguration.getAttribute( Repository.class );

		EntityModelImpl entityModel = new EntityModelImpl<>();
		entityModel.setCrudInvoker(
				CrudInvokerUtils.createCrudInvoker( repositoryFactoryInformation.getRepositoryInformation(),
				                                    repository )
		);
		entityModel.setEntityFactory(
				new PersistentEntityFactory( repositoryFactoryInformation.getPersistentEntity() )
		);
		entityModel.setEntityInformation( repositoryFactoryInformation.getEntityInformation() );
		entityModel.setLabelPrinter( createLabelPrinter( entityConfiguration.getPropertyRegistry() ) );

		entityConfiguration.setEntityModel( entityModel );
	}

	private Printer createLabelPrinter( EntityPropertyRegistry propertyRegistry ) {
		EntityPropertyDescriptor descriptor = null;

		if ( propertyRegistry != null ) {
			descriptor = propertyRegistry.getProperty( "#generatedLabel" );
			if ( descriptor == null ) {
				descriptor = propertyRegistry.getProperty( "name" );
			}
			if ( descriptor == null ) {
				descriptor = propertyRegistry.getProperty( "title" );
			}

			if ( descriptor == null ) {
				SimpleEntityPropertyDescriptor label = new SimpleEntityPropertyDescriptor();
				label.setName( "#generatedLabel" );
				label.setDisplayName( "Generated label" );
				label.setValueFetcher( new SpelValueFetcher( "toString()" ) );

				propertyRegistry.register( label );

				descriptor = label;
			}
		}

		if ( descriptor != null ) {
			return new ConvertedValuePrinter( descriptor );
		}

		return DEFAULT_PRINTER;
	}

	private class ConvertedValuePrinter implements Printer
	{

		private final EntityPropertyDescriptor descriptor;

		ConvertedValuePrinter( EntityPropertyDescriptor descriptor ) {
			this.descriptor = descriptor;
		}

		@SuppressWarnings("unchecked")
		@Override
		public String print( Object object, Locale locale ) {
			Object value = descriptor.getValueFetcher().getValue( object );

			return mvcConversionService.convert( value, String.class );
		}
	}

}
