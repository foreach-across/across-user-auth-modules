package com.foreach.across.modules.user.dto;

import com.foreach.across.modules.user.business.Role;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.business.UserRestriction;
import com.foreach.across.modules.user.converters.FieldUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

public class UserDto
{
	private long id;
	private String username;
	private String firstName;
	private String lastName;
	private String displayName;
	private String email;
	private String password;

	private boolean emailConfirmed;
	private boolean deleted;
	private Set<UserRestriction> restrictions = EnumSet.noneOf( UserRestriction.class );

	private Set<Role> roles = new TreeSet<>();

	private Boolean newUser;

	public UserDto() {
	}

	public UserDto( User user ) {
		copyFrom( user );
	}

	public long getId() {
		return id;
	}

	public void setId( long id ) {
		this.id = id;
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
		if( restrictions == null ) {
			this.restrictions = Collections.emptySet();
		} else {
			this.restrictions = EnumSet.copyOf( restrictions );
		}
	}

	public boolean hasRestriction( UserRestriction restriction ) {
		return restrictions.contains( restriction );
	}

	public boolean hasRestrictions() {
		return !CollectionUtils.isEmpty( restrictions );
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles( Set<Role> roles ) {
		this.roles = roles;
	}

	public void setNewUser( boolean newUser ) {
		this.newUser = newUser;
	}

	public boolean isNewUser() {
		return newUser != null ? newUser : getId() == 0;
	}

	public void copyFrom( User user ) {
		BeanUtils.copyProperties( user, this, "password" );
	}
}
