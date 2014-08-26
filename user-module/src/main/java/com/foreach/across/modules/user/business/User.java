package com.foreach.across.modules.user.business;

import com.foreach.across.modules.hibernate.business.IdBasedEntity;
import com.foreach.across.modules.hibernate.repositories.Undeletable;
import com.foreach.across.modules.user.config.UserSchemaConfiguration;
import com.foreach.across.modules.user.converters.HibernateUserRestriction;
import org.hibernate.annotations.Type;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.EnumSet;
import java.util.Set;

@Entity
@DiscriminatorValue("user")
@Table(name = UserSchemaConfiguration.TABLE_USER)
public class User extends GroupedPrincipal implements IdBasedEntity, UserDetails, Undeletable
{
	@Column(nullable = false, name = "username")
	private String username;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "display_name")
	private String displayName;

	@Column(name = "email")
	private String email;

	@Column(name = "password")
	private String password;

	@Column(name = "email_confirmed", nullable = false)
	private boolean emailConfirmed;

	@Column(name = "deleted", nullable = false)
	private boolean deleted;

	@Column(name = "restrictions", nullable = true)
	@Type(type = HibernateUserRestriction.CLASS_NAME)
	private Set<UserRestriction> restrictions = EnumSet.noneOf( UserRestriction.class );

	public String getUsername() {
		return username;
	}

	public void setUsername( String username ) {
		this.username = username;
		setPrincipalName( username );
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

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted( boolean deleted ) {
		this.deleted = deleted;
	}

	public Set<UserRestriction> getRestrictions() {
		return restrictions;
	}

	public void setRestrictions( Set<UserRestriction> restrictions ) {
		this.restrictions = restrictions;
	}

	public boolean hasRestriction( UserRestriction restriction ) {
		return getRestrictions().contains( restriction );
	}

	@Override
	public boolean isAccountNonExpired() {
		return !restrictions.contains( UserRestriction.EXPIRED );
	}

	@Override
	public boolean isAccountNonLocked() {
		return !restrictions.contains( UserRestriction.LOCKED );
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return !restrictions.contains( UserRestriction.CREDENTIALS_EXPIRED );
	}

	@Override
	public boolean isEnabled() {
		return !restrictions.contains( UserRestriction.DISABLED )
				&& !restrictions.contains( UserRestriction.REQUIRES_CONFIRMATION );
	}
}
