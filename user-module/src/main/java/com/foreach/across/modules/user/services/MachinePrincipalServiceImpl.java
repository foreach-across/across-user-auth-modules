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

package com.foreach.across.modules.user.services;

import com.foreach.across.modules.hibernate.util.BasicServiceHelper;
import com.foreach.across.modules.spring.security.SpringSecurityModuleCache;
import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalService;
import com.foreach.across.modules.user.business.BasicSecurityPrincipal;
import com.foreach.across.modules.user.business.MachinePrincipal;
import com.foreach.across.modules.user.business.UserDirectory;
import com.foreach.across.modules.user.repositories.MachinePrincipalRepository;
import com.foreach.across.modules.user.services.support.DefaultUserDirectoryStrategy;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

/**
 * @author Arne Vandamme
 */
@Service
public class MachinePrincipalServiceImpl implements MachinePrincipalService
{
	@Autowired
	private MachinePrincipalRepository machinePrincipalRepository;

	@Autowired
	private SecurityPrincipalService securityPrincipalService;

	@Autowired
	private DefaultUserDirectoryStrategy defaultUserDirectoryStrategy;

	@Override
	public Collection<MachinePrincipal> getMachinePrincipals() {
		return machinePrincipalRepository.findAll( new Sort( Sort.Direction.ASC, "name" ) );
	}

	@Cacheable(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, unless = SpringSecurityModuleCache.UNLESS_NULLS_ONLY)
	@Override
	public Optional<MachinePrincipal> getMachinePrincipalById( long id ) {
		return machinePrincipalRepository.findById( id );
	}

	@Override
	public Optional<MachinePrincipal> getMachinePrincipalByName( String name ) {
		return getMachinePrincipalByName( name, defaultUserDirectoryStrategy.getDefaultUserDirectory() );
	}

	@Override
	public Optional<MachinePrincipal> getMachinePrincipalByName( String name, UserDirectory userDirectory ) {
		String principalName = BasicSecurityPrincipal.uniquePrincipalName( name, userDirectory );
		return securityPrincipalService.getPrincipalByName( principalName );
	}

	@Override
	public MachinePrincipal save( MachinePrincipal machinePrincipalDto ) {
		defaultUserDirectoryStrategy.apply( machinePrincipalDto );
		return BasicServiceHelper.save( machinePrincipalDto, machinePrincipalRepository );
	}

	@Override
	public Optional<MachinePrincipal> findOne( Predicate predicate ) {
		return machinePrincipalRepository.findOne( predicate );
	}

	@Override
	public Collection<MachinePrincipal> findAll( Predicate predicate ) {
		return (Collection<MachinePrincipal>) machinePrincipalRepository.findAll( predicate );
	}

	@Override
	public Iterable<MachinePrincipal> findAll( Predicate predicate, Sort sort ) {
		return machinePrincipalRepository.findAll( predicate, sort );
	}

	@Override
	public Collection<MachinePrincipal> findAll( Predicate predicate, OrderSpecifier<?>... orderSpecifiers ) {
		return (Collection<MachinePrincipal>) machinePrincipalRepository.findAll( predicate, orderSpecifiers );
	}

	@Override
	public Iterable<MachinePrincipal> findAll( OrderSpecifier<?>... orders ) {
		return machinePrincipalRepository.findAll( orders );
	}

	@Override
	public Page<MachinePrincipal> findAll( Predicate predicate, Pageable pageable ) {
		return machinePrincipalRepository.findAll( predicate, pageable );
	}

	@Override
	public long count( Predicate predicate ) {
		return machinePrincipalRepository.count( predicate );
	}

	@Override
	public boolean exists( Predicate predicate ) {
		return machinePrincipalRepository.exists( predicate );
	}
}
