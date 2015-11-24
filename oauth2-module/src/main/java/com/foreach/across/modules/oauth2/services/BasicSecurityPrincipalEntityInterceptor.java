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
package com.foreach.across.modules.oauth2.services;

import com.foreach.across.modules.hibernate.aop.EntityInterceptorAdapter;
import com.foreach.across.modules.oauth2.OAuth2ModuleCache;
import com.foreach.across.modules.user.business.BasicSecurityPrincipal;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

/**
 * @author Andy Somers
 */
public class BasicSecurityPrincipalEntityInterceptor extends EntityInterceptorAdapter<BasicSecurityPrincipal>
{
	private Cache cache;

	public BasicSecurityPrincipalEntityInterceptor( CacheManager cacheManager ) {
		cache = cacheManager.getCache( OAuth2ModuleCache.ACCESS_TOKENS_TO_AUTHENTICATION );
	}

	@Override
	public boolean handles( Class<?> entityClass ) {
		return BasicSecurityPrincipal.class.isAssignableFrom( entityClass );
	}

	@Override
	public void afterUpdate( BasicSecurityPrincipal entity ) {
		cache.clear();
	}
}
