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
package com.foreach.across.modules.user.dto;

import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.business.UserRestriction;
import com.foreach.across.modules.user.converters.FieldUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.EnumSet;
import java.util.Set;

public class UserDto extends GroupedPrincipalDto<User>
{
	private String username;
	private String firstName;
	private String lastName;
	private String displayName;
	private String email;
	private String password;

	private boolean emailConfirmed;
	private boolean deleted;
	private Set<UserRestriction> restrictions = EnumSet.noneOf( UserRestriction.class );

	public UserDto() {
	}

	public UserDto( User user ) {
		copyFrom( user );
	}

	public String getUsername() {
		return username;
	}

	public void setUsername( String username ) {
		this.username = FieldUtils.lowerCase( username );
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName( String firstName ) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName( String lastName ) {
		this.lastName = lastName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName( String displayName ) {
		this.displayName = displayName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail( String email ) {
		this.email = FieldUtils.lowerCase( email );
	}

	public String getPassword() {
		return password;
	}

	public void setPassword( String password ) {
		this.password = password;
	}

	public boolean getEmailConfirmed() {
		return emailConfirmed;
	}

	public void setEmailConfirmed( boolean emailConfirmed ) {
		this.emailConfirmed = emailConfirmed;
	}

	public boolean getDeleted() {
		return deleted;
	}

	public void setDeleted( boolean deleted ) {
		this.deleted = deleted;
	}

	public Set<UserRestriction> getRestrictions() {
		return restrictions;
	}

	public void setRestrictions( Set<UserRestriction> restrictions ) {
		if ( CollectionUtils.isEmpty( restrictions ) ) {
			this.restrictions = EnumSet.noneOf( UserRestriction.class );
		}
		else {
			this.restrictions = EnumSet.copyOf( restrictions );
		}
	}

	public boolean hasRestriction( UserRestriction restriction ) {
		return restrictions.contains( restriction );
	}

	public boolean hasRestrictions() {
		return !CollectionUtils.isEmpty( restrictions );
	}

	public void copyFrom( User user ) {
		BeanUtils.copyProperties( user, this, "password" );
	}
}
