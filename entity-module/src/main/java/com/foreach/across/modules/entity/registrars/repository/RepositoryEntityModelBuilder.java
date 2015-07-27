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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryInformation;
import org.springframework.data.repository.support.CrudInvokerUtils;
import org.springframework.format.Printer;

import java.util.Locale;

/**
 * Builds an {@link com.foreach.across.modules.entity.registry.EntityModel} for a Spring data repository.
 */
public class RepositoryEntityModelBuilder
{
	@Autowired
	private ConversionService mvcConversionService;

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
		return new ConvertedValuePrinter(
				mvcConversionService, propertyRegistry.getProperty( EntityPropertyRegistry.LABEL )
		);
	}

	private class ConvertedValuePrinter implements Printer
	{
		private final ConversionService conversionService;
		private final EntityPropertyDescriptor descriptor;

		public ConvertedValuePrinter( ConversionService conversionService,
		                              EntityPropertyDescriptor descriptor ) {
			this.conversionService = conversionService;
			this.descriptor = descriptor;
		}

		@SuppressWarnings("unchecked")
		@Override
		public String print( Object object, Locale locale ) {
			Object value = descriptor.getValueFetcher().getValue( object );
			return value != null ? conversionService.convert( value, String.class ) : "";
		}
	}

}
