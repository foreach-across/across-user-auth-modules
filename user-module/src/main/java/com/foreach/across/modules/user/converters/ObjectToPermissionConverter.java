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
package com.foreach.across.modules.user.converters;

import com.foreach.across.modules.user.business.Permission;
import com.foreach.across.modules.user.services.PermissionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;

public class ObjectToPermissionConverter implements Converter<Object, Permission>
{
	private final ConversionService conversionService;
	private final PermissionService permissionService;

	public ObjectToPermissionConverter( ConversionService conversionService, PermissionService permissionService ) {
		this.conversionService = conversionService;
		this.permissionService = permissionService;
	}

	@Override
	public Permission convert( Object source ) {
		if ( source instanceof Permission ) {
			return (Permission) source;
		}

		String permissionName = conversionService.convert( source, String.class );

		if ( !StringUtils.isBlank( permissionName ) ) {
			return permissionService.getPermission( permissionName );
		}

		return null;
	}
}
