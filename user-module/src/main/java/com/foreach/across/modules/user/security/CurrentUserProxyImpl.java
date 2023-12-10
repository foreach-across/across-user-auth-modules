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

package com.foreach.across.modules.user.security;

import com.foreach.across.modules.spring.security.infrastructure.services.CurrentSecurityPrincipalProxy;
import com.foreach.across.modules.user.business.Group;
import com.foreach.across.modules.user.business.Permission;
import com.foreach.across.modules.user.business.Role;
import com.foreach.across.modules.user.business.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Deprecated
public class CurrentUserProxyImpl implements CurrentUserProxy
{
	@Autowired
	private CurrentSecurityPrincipalProxy currentSecurityPrincipalProxy;

	@Override
	public Long getId() {
		return Optional.ofNullable( getUser() ).map( User::getId ).orElse( null );
	}

	@Override
	public String getEmail() {
		return Optional.ofNullable( getUser() ).map( User::getEmail ).orElse( null );
	}

	@Override
	public String getUsername() {
		return Optional.ofNullable( getUser() ).map( User::getUsername ).orElse( null );
	}

	@Override
	public boolean isMemberOf( Group group ) {
		return Optional.ofNullable( getUser() ).map( u -> u.isMemberOf( group ) ).orElse( false );
	}

	@Override
	public boolean hasRole( String authority ) {
		return Optional.ofNullable( getUser() ).map( u -> u.hasRole( authority ) ).orElse( false );
	}

	@Override
	public boolean hasRole( Role role ) {
		return Optional.ofNullable( getUser() ).map( u -> u.hasRole( role ) ).orElse( false );
	}

	@Override
	public boolean hasPermission( String name ) {
		return Optional.ofNullable( getUser() ).map( u -> u.hasPermission( name ) ).orElse( false );
	}

	@Override
	public boolean hasPermission( Permission permission ) {
		return Optional.ofNullable( getUser() ).map( u -> u.hasPermission( permission ) ).orElse( false );
	}

	@Override
	public boolean hasAuthority( String authority ) {
		return currentSecurityPrincipalProxy.hasAuthority( authority );
	}

	@Override
	public boolean hasAuthority( GrantedAuthority authority ) {
		return currentSecurityPrincipalProxy.hasAuthority( authority );
	}

	@Override
	public User getUser() {
		return currentSecurityPrincipalProxy.getPrincipal( User.class );
	}

	@Override
	public boolean isAuthenticated() {
		return currentSecurityPrincipalProxy.isAuthenticated();
	}
}
