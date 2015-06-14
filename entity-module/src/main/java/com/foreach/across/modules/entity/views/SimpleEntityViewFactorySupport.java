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
package com.foreach.across.modules.entity.views;

import com.foreach.across.modules.entity.controllers.EntityViewCommand;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.entity.web.EntityLinkBuilder;
import org.springframework.context.MessageSource;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.validation.DataBinder;

import java.util.Collection;
import java.util.Collections;

/**
 * Base support class for {@link EntityViewFactory} implementations that supports the following features:
 * <ul>
 * <li>template specification</li>
 * <li>message source and message code resolving</li>
 * <li>custom entity link builders</li>
 * <li>custom view processors for customizing the view generation</li>
 * </ul>
 *
 * @author Arne Vandamme
 */
public abstract class SimpleEntityViewFactorySupport<V extends ViewCreationContext, T extends EntityView> implements EntityViewFactory<V>
{
	public static final String CONTAINER = "_entityView";

	private Collection<EntityViewProcessor<V, T>> processors = Collections.emptyList();

	private String template;
	private MessageSource messageSource;
	private EntityMessageCodeResolver messageCodeResolver;
	private EntityLinkBuilder entityLinkBuilder;
	private String[] messagePrefixes = new String[] { "entityViews" };

	/**
	 * @param template Template that should be used to render this view.
	 */
	public void setTemplate( String template ) {
		this.template = template;
	}

	public String getTemplate() {
		return template;
	}

	/**
	 * Set the message source that should be used for this view.  If none is specified, the default
	 * messagesource will be used.
	 *
	 * @param messageSource MessagesSource instance or null to use the default.
	 */
	public void setMessageSource( MessageSource messageSource ) {
		this.messageSource = messageSource;
	}

	/**
	 * Set the {@link com.foreach.across.modules.entity.support.EntityMessageCodeResolver} that should be
	 * used for looking up all the messages.  This resolver will first be prefixed with the configured
	 * prefixes and will use the message source configured on the view if there is one.
	 * <p/>
	 * If no resolver is specified, the default resolver from the set EntityConfiguration will be used.
	 *
	 * @param messageCodeResolver EntityMessageCodeResolver instance or null to use the entity default.
	 */
	public void setMessageCodeResolver( EntityMessageCodeResolver messageCodeResolver ) {
		this.messageCodeResolver = messageCodeResolver;
	}

	/**
	 * Set the prefixes that should be applied to the message keys.  These prefixes will be applied to
	 * the configured {@link com.foreach.across.modules.entity.support.EntityMessageCodeResolver}.
	 *
	 * @param messagePrefixes One or more message key prefixes.
	 */
	public void setMessagePrefixes( String... messagePrefixes ) {
		this.messagePrefixes = messagePrefixes;
	}

	/**
	 * Set the EntityLinkBuilder that should be used for this view.
	 *
	 * @param entityLinkBuilder EntityLinkBuilder to use or null to use the entity default.
	 */
	public void setEntityLinkBuilder( EntityLinkBuilder entityLinkBuilder ) {
		this.entityLinkBuilder = entityLinkBuilder;
	}

	public Collection<EntityViewProcessor<V, T>> getProcessors() {
		return processors;
	}

	/**
	 * Set the {@link EntityViewProcessor}s that should be called when building the {@link EntityView}.
	 *
	 * @param processors A Collection of ViewPreProcessors
	 */
	public void setProcessors( Collection<EntityViewProcessor<V, T>> processors ) {
		this.processors = processors;
	}

	@Override
	public void prepareModelAndCommand( String viewName,
	                                    V creationContext,
	                                    EntityViewCommand command,
	                                    ModelMap model ) {
		registerLinkBuilder( creationContext, model );

		for ( EntityViewProcessor<V, T> processor : processors ) {
			processor.prepareModelAndCommand( viewName, creationContext, command, model );
		}
	}

	@Override
	public void prepareDataBinder( String viewName,
	                               V creationContext,
	                               EntityViewCommand command,
	                               DataBinder dataBinder ) {
		for ( EntityViewProcessor<V, T> processor : processors ) {
			processor.prepareDataBinder( viewName, creationContext, command, dataBinder );
		}
	}

	@Override
	public EntityView create( String viewName, V creationContext, ModelMap model ) {
		EntityConfiguration entityConfiguration = creationContext.getEntityConfiguration();
		Assert.notNull( entityConfiguration );

		T view = createEntityView( model );
		view.setName( viewName );
		view.setTemplate( template );
		view.setEntityConfiguration( entityConfiguration );

		EntityMessageCodeResolver codeResolver = createMessageCodeResolver( entityConfiguration );
		view.setEntityMessages( createEntityMessages( codeResolver ) );

		preProcessEntityView( creationContext, view );

		buildViewModel( creationContext, entityConfiguration, codeResolver, view );

		postProcessEntityView( creationContext, view );

		return view;
	}

	/**
	 * This registers the {@link com.foreach.across.modules.entity.web.EntityConfigurationLinkBuilder} in the model so it can
	 * be used for redirect actions without rendering the actual view.
	 */
	protected void registerLinkBuilder( V creationContext, ModelMap model ) {
		EntityLinkBuilder linkBuilder = null;

		if ( entityLinkBuilder != null ) {
			linkBuilder = entityLinkBuilder;
		}
		else {
			// If association, see if there is a link builder configured on the association
			if ( creationContext.isForAssociation() ) {
				linkBuilder = creationContext.getEntityAssociation().getAttribute( EntityLinkBuilder.class );
			}

			// Use the link builder available on the entity configuration (usually there is one)
			if ( linkBuilder == null ) {
				linkBuilder = creationContext.getEntityConfiguration().getAttribute( EntityLinkBuilder.class );
			}
		}

		if ( linkBuilder != null && creationContext.isForAssociation() ) {
			// todo: only scope if so configured
			// Scope the link builder to the parent
			EntityConfiguration source = creationContext.getEntityAssociation().getSourceEntityConfiguration();
			EntityLinkBuilder sourceLinkBuilder = source.getAttribute( EntityLinkBuilder.class );
			Object sourceEntity = model.get( EntityFormView.ATTRIBUTE_PARENT_ENTITY );

			if ( sourceEntity != null ) {
				linkBuilder = linkBuilder.asAssociationFor( sourceLinkBuilder, sourceEntity );
			}
		}

		model.addAttribute( EntityView.ATTRIBUTE_ENTITY_LINKS, linkBuilder );
	}

	protected void preProcessEntityView( V creationContext, T view ) {
		for ( EntityViewProcessor<V, T> processor : processors ) {
			processor.preProcess( creationContext, view );
		}
	}

	protected void postProcessEntityView( V creationContext, T view ) {
		for ( EntityViewProcessor<V, T> processor : processors ) {
			processor.postProcess( creationContext, view );
		}
	}

	protected EntityMessages createEntityMessages( EntityMessageCodeResolver codeResolver ) {
		return new EntityMessages( codeResolver );
	}

	protected EntityMessageCodeResolver createMessageCodeResolver( EntityConfiguration entityConfiguration ) {
		EntityMessageCodeResolver codeResolver = messageCodeResolver;

		if ( codeResolver == null ) {
			codeResolver = entityConfiguration.getEntityMessageCodeResolver();
		}

		codeResolver = codeResolver.prefixedResolver( messagePrefixes );

		if ( messageSource != null ) {
			codeResolver.setMessageSource( messageSource );
		}

		return codeResolver;
	}

	protected abstract T createEntityView( ModelMap model );

	protected abstract void buildViewModel( V viewCreationContext,
	                                        EntityConfiguration entityConfiguration,
	                                        EntityMessageCodeResolver codeResolver,
	                                        T view );
}
