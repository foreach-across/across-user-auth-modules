package com.foreach.across.modules.user.dto;

import com.foreach.across.modules.user.business.Role;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.business.UserStatus;
import org.springframework.beans.BeanUtils;

import java.util.Set;
import java.util.TreeSet;

public class UserDto
{
	private long id;
	private String username;
    private String firstname;
    private String lastname;
    private String name;
	private String email;
	private String password;

    private boolean emailConfirmed;
    private boolean deleted;
    private Set<UserStatus> status;

	private Set<Role> roles = new TreeSet<>();

	public UserDto() {
	}

	public UserDto( User user ) {
		setFromUser( user );
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
		this.username = username;
	}

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname( String firstname ) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname( String lastname ) {
        this.lastname = lastname;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getEmail() {
		return email;
	}

	public void setEmail( String email ) {
		this.email = email;
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

    public Set<UserStatus> getStatus() {
        return status;
    }

    public void setStatus( Set<UserStatus> status ) {
        this.status = status;
    }

    public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles( Set<Role> roles ) {
		this.roles = roles;
	}

	public boolean isNewUser() {
		return getId() == 0;
	}

	public void setFromUser( User user ) {
		BeanUtils.copyProperties( user, this, "password" );
	}
}
