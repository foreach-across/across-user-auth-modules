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

package com.foreach.across.modules.oauth2.config;

import com.foreach.across.core.annotations.ConditionalOnAcrossModule;
import com.foreach.across.modules.bootstrapui.BootstrapUiModule;
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiElements;
import com.foreach.across.modules.bootstrapui.elements.FormGroupElement;
import com.foreach.across.modules.bootstrapui.elements.builder.OptionFormElementBuilder;
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.EmbeddedCollectionOrMapElementBuilderFactory;
import com.foreach.across.modules.entity.views.bootstrapui.options.FixedOptionIterableBuilder;
import com.foreach.across.modules.entity.views.bootstrapui.options.OptionIterableBuilder;
import com.foreach.across.modules.entity.views.processors.EntityViewProcessorAdapter;
import com.foreach.across.modules.entity.views.processors.SingleEntityFormViewProcessor;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.oauth2.business.OAuth2Client;
import com.foreach.across.modules.oauth2.business.OAuth2ClientScope;
import com.foreach.across.modules.oauth2.business.OAuth2ClientScopeId;
import com.foreach.across.modules.oauth2.business.OAuth2Scope;
import com.foreach.across.modules.oauth2.repositories.OAuth2ClientScopeRepository;
import com.foreach.across.modules.oauth2.validators.OAuth2ClientValidator;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.HtmlViewElement;
import com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.ConfigurableConversionService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils.move;

/**
 * @author Marc Vanbrabant
 */
@Configuration
@ConditionalOnAcrossModule(allOf = { EntityModule.NAME, BootstrapUiModule.NAME })
@ComponentScan(basePackageClasses = OAuth2ClientValidator.class)
public class OAuth2EntityConfiguration implements EntityConfigurer
{
	private final OAuth2ClientScopeRepository clientScopeRepository;

	public OAuth2EntityConfiguration( OAuth2ClientScopeRepository clientScopeRepository ) {
		this.clientScopeRepository = clientScopeRepository;
	}

	@Autowired
	public void registerClientScopeIdConverters( ConfigurableConversionService mvcConversionService ) {
		mvcConversionService.addConverter( OAuth2ClientScopeId.class, String.class, source ->
				source.getOAuth2Client().getId() + "-" + source.getOAuth2Scope().getId()
		);

		mvcConversionService.addConverter( String.class, OAuth2ClientScopeId.class, source -> {
			String[] parts = source.split( "-" );
			OAuth2ClientScopeId id = new OAuth2ClientScopeId();
			id.setOAuth2Client( mvcConversionService.convert( parts[0], OAuth2Client.class ) );
			id.setOAuth2Scope( mvcConversionService.convert( parts[1], OAuth2Scope.class ) );
			return id;
		} );
	}

	@Override
	public void configure( EntitiesConfigurationBuilder configuration ) {
		configuration.withType( OAuth2ClientScope.class ).hide();

		configuration.withType( OAuth2Client.class )
		             .properties( props -> props
				             .property( "OAuth2ClientScopes" )
				             .viewElementType( ViewElementMode.CONTROL, EmbeddedCollectionOrMapElementBuilderFactory.ELEMENT_TYPE )
				             .hidden( false )
				             .controller(
						             controller -> controller.withBindingContext( Set.class )
						                                     .saveConsumer( ( bindingContext, propertyValue ) -> {
							                                     OAuth2Client original = bindingContext.getEntity();
							                                     OAuth2Client target = bindingContext.getTarget();

							                                     if ( original != null && !original.isNew() ) {
								                                     original.getOAuth2ClientScopes()
								                                             .stream()
								                                             .filter( s -> !target.getOAuth2ClientScopes().contains( s ) )
								                                             .forEach( clientScopeRepository::delete );
							                                     }
						                                     } )
				             )
				             .and()
				             .property( "OAuth2ClientScopes[]" )
				             .viewElementType( ViewElementMode.FORM_WRITE, BootstrapUiElements.FIELDSET )
				             .attribute( EntityAttributes.FIELDSET_PROPERTY_SELECTOR, EntityPropertySelector
						             .of( "OAuth2ClientScopes[].OAuth2Scope", "OAuth2ClientScopes[].autoApprove" ) )
				             .and()
				             .property( "authorizedGrantTypes" )
				             .viewElementType( ViewElementMode.CONTROL, BootstrapUiElements.MULTI_CHECKBOX )
				             .attribute( OptionIterableBuilder.class, FixedOptionIterableBuilder.of(
						             new OptionFormElementBuilder().label( "password" ).value( "password" ).rawValue( "password" ),
						             new OptionFormElementBuilder().label( "client_credentials" ).value( "client_credentials" )
						                                           .rawValue( "client_credentials" ),
						             new OptionFormElementBuilder().label( "authorization_code" ).value( "authorization_code" )
						                                           .rawValue( "authorization_code" ),
						             new OptionFormElementBuilder().label( "refresh_token" ).value( "refresh_token" ).rawValue( "refresh_token" ),
						             new OptionFormElementBuilder().label( "implicit" ).value( "implicit" ).rawValue( "implicit" )
				             ) )
		             )
		             .listView(
				             lvb -> lvb.showProperties( "clientId", "secretRequired", "resourceIds", "lastModified" )
				                       .defaultSort( "clientId" )
		             )
		             .createOrUpdateFormView(
				             fvb -> fvb
						             .properties( props -> props
								             .property( "token-validity" )
								             .displayName( "Token validity" )
								             .viewElementType( ViewElementMode.FORM_WRITE, BootstrapUiElements.FIELDSET )
								             .attribute(
										             EntityAttributes.FIELDSET_PROPERTY_SELECTOR,
										             EntityPropertySelector.of( "accessTokenValiditySeconds", "refreshTokenValiditySeconds" )
								             )
						             )
						             .showProperties( "clientId", "secretRequired", "clientSecret", "token-validity",
						                              "roles", "created", "lastModified",
						                              "authorizedGrantTypes", "resourceIds", "registeredRedirectUri",
						                              "OAuth2ClientScopes" )
						             .viewProcessor( new OAuth2ClientFormAdapter() )
		             )
		             .createFormView( fvb -> fvb.showProperties( ".", "~created", "~lastModified" ) );

		configuration.withType( OAuth2Scope.class )
		             .properties( props -> props.property( "oAuth2ClientScopes" ).hidden( true ) );
	}

	private static class OAuth2ClientFormAdapter extends EntityViewProcessorAdapter
	{
		@Override
		protected void postRender( EntityViewRequest entityViewRequest,
		                           EntityView entityView,
		                           ContainerViewElement container,
		                           ViewElementBuilderContext builderContext ) {
			addDependency( container, "clientSecret", "secretRequired" );

			move( container, "formGroup-authorizedGrantTypes", SingleEntityFormViewProcessor.RIGHT_COLUMN );
			move( container, "formGroup-resourceIds", SingleEntityFormViewProcessor.RIGHT_COLUMN );
			move( container, "formGroup-registeredRedirectUri", SingleEntityFormViewProcessor.RIGHT_COLUMN );
			move( container, "formGroup-OAuth2ClientScopes", SingleEntityFormViewProcessor.RIGHT_COLUMN );
		}

		private void addDependency( ContainerViewElement elements, String from, String to ) {
			ContainerViewElementUtils
					.find( elements, "formGroup-" + from, FormGroupElement.class )
					.ifPresent( group -> {
						Map<String, Object> qualifiers = new HashMap<>();
						qualifiers.put( "checked", true );

						group.getControl( HtmlViewElement.class )
						     .setAttribute(
								     "data-dependson",
								     Collections.singletonMap( "[id='entity." + to + "']", qualifiers )
						     );
					} );
		}
	}
}
