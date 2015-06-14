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
package com.foreach.across.modules.entity.web;

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.web.context.WebAppPathResolver;
import org.springframework.core.convert.ConversionService;

import java.io.Serializable;
import java.text.MessageFormat;

/**
 * Creates links to entity views and standard controllers.
 * By default crud and list views are included.
 */
public class EntityConfigurationLinkBuilder implements EntityLinkBuilder
{
	private final String rootPath;
	private final EntityConfiguration entityConfiguration;
	private final ConversionService conversionService;

	private String overviewPath = "{0}/{1}";
	private String createPath = "{0}/{1}/create";
	private String viewPath = "{0}/{1}/{2}";
	private String updatePath = "{0}/{1}/{2}/update";
	private String deletePath = "{0}/{1}/{2}/delete";
	private String associationsPath = "{0}/{1}/{2}/associations";

	private WebAppPathResolver webAppPathResolver;

	public EntityConfigurationLinkBuilder( String rootPath,
	                                       EntityConfiguration entityConfiguration,
	                                       ConversionService conversionService ) {
		this.rootPath = rootPath;
		this.entityConfiguration = entityConfiguration;
		this.conversionService = conversionService;
	}

	public EntityConfigurationLinkBuilder( String rootPath,
	                                       EntityConfiguration entityConfiguration,
	                                       ConversionService conversionService,
	                                       WebAppPathResolver webAppPathResolver ) {
		this.rootPath = rootPath;
		this.entityConfiguration = entityConfiguration;
		this.conversionService = conversionService;
		this.webAppPathResolver = webAppPathResolver;
	}

	WebAppPathResolver getWebAppPathResolver() {
		return webAppPathResolver;
	}

	String getOverviewPath() {
		return overviewPath;
	}

	String getCreatePath() {
		return createPath;
	}

	String getViewPath() {
		return viewPath;
	}

	String getUpdatePath() {
		return updatePath;
	}

	String getDeletePath() {
		return deletePath;
	}

	String getAssociationsPath() {
		return associationsPath;
	}

	/**
	 * Set the {@link WebAppPathResolver} to be used for this builder.  All generated links will be passed
	 * through this resolver.
	 *
	 * @param webAppPathResolver to use, can be null
	 */
	public void setWebAppPathResolver( WebAppPathResolver webAppPathResolver ) {
		this.webAppPathResolver = webAppPathResolver;
	}

	public void setOverviewPath( String overviewPath ) {
		this.overviewPath = overviewPath;
	}

	public void setCreatePath( String createPath ) {
		this.createPath = createPath;
	}

	public void setViewPath( String viewPath ) {
		this.viewPath = viewPath;
	}

	public void setUpdatePath( String updatePath ) {
		this.updatePath = updatePath;
	}

	public void setDeletePath( String deletePath ) {
		this.deletePath = deletePath;
	}

	public void setAssociationsPath( String associationsPath ) {
		this.associationsPath = associationsPath;
	}

	@Override
	public String overview() {
		return generate( overviewPath );
	}

	@Override
	public String create() {
		return generate( createPath );
	}

	@Override
	public String update( Object entity ) {
		return generate( updatePath, entity );
	}

	@Override
	public String delete( Object entity ) {
		return generate( deletePath, entity );
	}

	@Override
	public String view( Object entity ) {
		return generate( viewPath, entity );
	}

	@Override
	public String associations( Object entity ) {
		return generate( associationsPath, entity );
	}

	private String generate( String pattern ) {
		return resolve( MessageFormat.format( pattern, rootPath, getEntityConfigurationPath(), null ) );
	}

	@SuppressWarnings("unchecked")
	private String generate( String pattern, Object entity ) {
		Serializable id = entityConfiguration.getEntityModel().getId( entity );
		String idAsString = conversionService.convert( id, String.class );

		return resolve( MessageFormat.format( pattern, rootPath, getEntityConfigurationPath(), idAsString ) );
	}

	private String resolve( String path ) {
		return webAppPathResolver != null ? webAppPathResolver.path( path ) : path;
	}

	protected String getEntityConfigurationPath() {
		return entityConfiguration.getName();
	}

	/**
	 * Creates a new link builder that represents the current linkbuilder as an association from a source entity,
	 * this will use {@link com.foreach.across.modules.entity.web.EntityLinkBuilder#associations(Object)} on the
	 * source link builder for prefixing the current link builder.
	 */
	@Override
	public EntityLinkBuilder asAssociationFor( EntityLinkBuilder sourceLinkBuilder, Object sourceEntity ) {
		return new PrefixingLinkBuilder( sourceLinkBuilder.associations( sourceEntity ), this );
	}
}
