package com.foreach.across.modules.user.business;

import com.foreach.across.core.database.AcrossSchemaConfiguration;
import com.foreach.across.modules.user.config.UserSchemaConfiguration;
import com.foreach.across.modules.user.converters.HibernateUserStatus;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Type;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = UserSchemaConfiguration.TABLE_USER)
public class User implements UserDetails
{
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "seq_um_user_id")
	@TableGenerator(name = "seq_um_user_id", table = AcrossSchemaConfiguration.TABLE_SEQUENCES,
	                pkColumnName = AcrossSchemaConfiguration.SEQUENCE_NAME,
	                valueColumnName = AcrossSchemaConfiguration.SEQUENCE_VALUE, pkColumnValue = "seq_um_user_id",
	                allocationSize = 10)
	private long id;

	@Column(nullable = false, name = "username")
	private String username;

    @Column( name = "firstname")
    private String firstName;

    @Column( name = "lastname")
    private String lastName;

    @Column( name = "displayname")
    private String displayName;

	@Column
	private String email;

	@Column
	private String password;

    @Column( nullable = false )
    private boolean emailConfirmed;

    @Column( nullable = false )
    private boolean deleted;

    @Column( nullable = true )
    @Type( type= HibernateUserStatus.CLASS_NAME )
    private Set<UserStatus> status = EnumSet.noneOf( UserStatus.class );

	@ManyToMany(fetch = FetchType.EAGER)
	@BatchSize(size = 50)
	@JoinTable(
			name = UserSchemaConfiguration.TABLE_USER_ROLE,
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new TreeSet<>();

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

	public boolean hasRole( String name ) {
		return hasRole( new Role( name ) );
	}

	public boolean hasRole( Role role ) {
		return getRoles().contains( role );
	}

	public boolean hasPermission( String name ) {
		return hasPermission( new Permission( name ) );
	}

	public boolean hasPermission( Permission permission ) {
		for ( Role role : getRoles() ) {
			if ( role.hasPermission( permission ) ) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> authorities = new HashSet<>();

		for ( Role role : getRoles() ) {
			authorities.add( new SimpleGrantedAuthority( role.getName() ) );
			for ( Permission permission : role.getPermissions() ) {
				authorities.add( new SimpleGrantedAuthority( permission.getName() ) );
			}
		}
		return authorities;
	}

	@Override
	public boolean isAccountNonExpired() {
		return status.contains( UserStatus.NON_EXPIRED );
	}

	@Override
	public boolean isAccountNonLocked() {
		return status.contains( UserStatus.NON_LOCKED );
	}

	@Override
	public boolean isCredentialsNonExpired() {
        return status.contains( UserStatus.CREDENTIALS_NON_EXPIRED );
	}

	@Override
	public boolean isEnabled() {
		return status.contains( UserStatus.ACTIVE );
	}
}
