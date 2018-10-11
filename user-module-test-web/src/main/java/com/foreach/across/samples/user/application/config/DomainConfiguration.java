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

package com.foreach.across.samples.user.application.config;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.convert.StringToDateTimeConverter;
import com.foreach.across.modules.entity.bind.EntityPropertyBindingType;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.config.builders.EntityPropertyRegistryBuilder;
import com.foreach.across.modules.entity.registry.properties.DefaultEntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyController;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.business.UserProperties;
import com.foreach.across.modules.user.services.UserPropertiesRegistry;
import com.foreach.across.modules.user.services.UserPropertiesService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.GenericConversionService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author Marc Vanbrabant
 * @since 3.1.0
 */
@Configuration
@RequiredArgsConstructor
public class DomainConfiguration implements EntityConfigurer
{
	private final UserPropertiesService userPropertiesService;
	private final UserPropertiesRegistry userPropertiesRegistry;

	@Autowired
	public void registerCustomProperties( UserPropertiesRegistry userPropertiesRegistry,
	                                      StringToDateTimeConverter converter,
	                                      AcrossModule currentModule ) {
		userPropertiesRegistry.register( currentModule, "thumbnail",
		                                 TypeDescriptor.valueOf( String.class ) );

		// Specify how we serialize our property
		GenericConversionService birthdayConversionService = new GenericConversionService();
		birthdayConversionService.addConverter( LocalDate.class, String.class,
		                                        source -> String.valueOf( source.format(
				                                        DateTimeFormatter.ofPattern( "yyyy-MM-dd" ) ) ) );
		birthdayConversionService.addConverter( converter );

		userPropertiesRegistry.register( currentModule, "birthdate",
		                                 TypeDescriptor.valueOf( LocalDate.class ), null, birthdayConversionService );
	}

	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		DefaultEntityPropertyRegistry nestedRegistry = new DefaultEntityPropertyRegistry();
		EntityPropertyRegistryBuilder entityPropertyRegistryBuilder = new EntityPropertyRegistryBuilder();
		userPropertiesRegistry.getRegisteredProperties()
		                      .forEach( p -> entityPropertyRegistryBuilder
				                      .property( p )
				                      .propertyType( userPropertiesRegistry
						                                     .getTypeForProperty(
								                                     p ) )
				                      .readable( true )
				                      .writable( true )
				                      .hidden( false )
				                      .controller( c -> c.withTarget(
						                      UserProperties.class, Object.class )
				                                         .valueFetcher( userProperties -> userProperties.getValue( p ) )
				                                         .applyValueConsumer(
						                                         ( userProperties, property ) ->
								                                         userProperties.set( p,
								                                                             property.getNewValue() )
				                                         )
				                      ) );
		entityPropertyRegistryBuilder.apply( nestedRegistry );

		entities.withType( User.class )
		        .createOrUpdateFormView( fv -> {
			        fv.showProperties( ".",
			                           "userProperties.thumbnail",
			                           "userProperties.birthdate" );

		        } )
		        .properties( props -> {
			                     props
					                     .property( "userProperties" )
					                     .propertyType( UserProperties.class )
					                     .controller( c -> c.withTarget( User.class, UserProperties.class )
					                                        .order( EntityPropertyController.AFTER_ENTITY )
					                                        .valueFetcher(
							                                        user -> userPropertiesService.getProperties( user.getId() ) )
					                                        .saveConsumer( ( user, userProperties ) -> {
						                                        userPropertiesService.saveProperties( userProperties.getNewValue(),
						                                                                              user::getId );
					                                        } )
					                     )
					                     .hidden( true )
					                     // Use the nestedRegistry to resolve parent descriptors inside userProperties.*
					                     .attribute( EntityPropertyRegistry.class, nestedRegistry )
					                     // Treat the UserProperties as a single value, not as a map
					                     .attribute( EntityPropertyBindingType.class, EntityPropertyBindingType.SINGLE_VALUE );
					                     /*
					                     .and()
					                     .property( "userProperties.picture" )
					                     //TODO: Fix the usage of .parent()
					                     .parent( "userProperties" )
					                     .propertyType( String.class )
					                     .displayName( "Picture" )
					                     .readable( true )
					                     .writable( true )
					                     .hidden( false )
					                     .controller( c -> c.withTarget( UserProperties.class, String.class )
					                                        .valueFetcher( userProperties -> userProperties.getValue( "picture" ) )
					                                        .applyValueConsumer(
							                                        ( userProperties, thumbnail ) ->
									                                        userProperties.set( "picture",
									                                                            thumbnail.getNewValue() )
					                                        )
					                     );
					                     */
		                     }
		        );
	}

}
