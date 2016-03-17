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

package com.foreach.across.modules.user.business;

import com.foreach.across.modules.hibernate.business.Auditable;
import com.foreach.across.modules.hibernate.business.SettableIdBasedEntity;
import com.foreach.across.modules.hibernate.id.AcrossSequenceGenerator;
import com.foreach.across.modules.user.config.UserSchemaConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;

import javax.annotation.concurrent.NotThreadSafe;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Represents a security principal that can be assigned one or more roles.
 *
 * @author Arne Vandamme
 */
@NotThreadSafe
@Entity
@Table(name = UserSchemaConfiguration.TABLE_PRINCIPAL)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(
		name = "principal_type",
		discriminatorType = DiscriminatorType.STRING
)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public abstract class BasicSecurityPrincipal<T extends SettableIdBasedEntity<?>>
		extends SettableIdBasedEntity<T>
		implements IdBasedSecurityPrincipal, Auditable<String>
{
	private static final Pattern DIRECTORY_PREFIXED = Pattern.compile( "^-?\\d+," );

	@Id
	@GeneratedValue(generator = "seq_um_principal_id")
	@GenericGenerator(
			name = "seq_um_principal_id",
			strategy = AcrossSequenceGenerator.STRATEGY,
			parameters = {
					@org.hibernate.annotations.Parameter(name = "sequenceName", value = "seq_um_principal_id"),
					@org.hibernate.annotations.Parameter(name = "allocationSize", value = "10")
			}
	)
	private Long id;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "user_directory_id")
	private UserDirectory userDirectory;

	@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@ManyToMany(fetch = FetchType.EAGER)
	@BatchSize(size = 50)
	@JoinTable(
			name = UserSchemaConfiguration.TABLE_PRINCIPAL_ROLE,
			joinColumns = @JoinColumn(name = "principal_id"),
			inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new TreeSet<>();

	@Column(name = "created_by", nullable = true)
	private String createdBy;

	@Column(name = "created_date", nullable = true)
	private Date createdDate;

	@Column(name = "last_modified_by", nullable = true)
	private String lastModifiedBy;

	@Column(name = "last_modified_date", nullable = true)
	private Date lastModifiedDate;

	@Column(name = "principal_name")
	private String principalName;

	@Override
	public String getPrincipalName() {
		return StringUtils.lowerCase( principalName );
	}

	protected final void setPrincipalName( String principalName ) {
		this.principalName = StringUtils.lowerCase( principalName );
		prefixPrincipalName();
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId( Long id ) {
		this.id = id;
	}

	public UserDirectory getUserDirectory() {
		return userDirectory;
	}

	public final void setUserDirectory( UserDirectory userDirectory ) {
		this.userDirectory = userDirectory;
		prefixPrincipalName();
	}

	private void prefixPrincipalName() {
		if ( !StringUtils.isBlank( principalName ) ) {
			principalName = DIRECTORY_PREFIXED.matcher( principalName ).replaceFirst( "" );
			if ( userDirectory != null
					&& !Objects.equals( userDirectory.getId(), UserDirectory.DEFAULT_INTERNAL_DIRECTORY_ID ) ) {
				principalName = userDirectory.getId() + "," + principalName;
			}
		}
	}

	@Override
	public String getCreatedBy() {
		return createdBy;
	}

	@Override
	public void setCreatedBy( String createdBy ) {
		this.createdBy = createdBy;
	}

	@Override
	public Date getCreatedDate() {
		return createdDate;
	}

	@Override
	public void setCreatedDate( Date createdDate ) {
		this.createdDate = createdDate;
	}

	@Override
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	@Override
	public void setLastModifiedBy( String lastModifiedBy ) {
		this.lastModifiedBy = lastModifiedBy;
	}

	@Override
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	@Override
	public void setLastModifiedDate( Date lastModifiedDate ) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles( Collection<Role> roles ) {
		getRoles().clear();
		if ( roles != null ) {
			getRoles().addAll( roles );
		}
	}

	public boolean hasRole( String authority ) {
		return hasRole( new Role( authority ) );
	}

	public boolean hasRole( Role role ) {
		return getRoles().contains( role );
	}

	public void addRole( Role role ) {
		getRoles().add( role );
	}

	public void removeRole( Role role ) {
		getRoles().remove( role );
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

	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> authorities = new LinkedHashSet<>();

		for ( Role role : getRoles() ) {
			authorities.add( role );
			for ( Permission permission : role.getPermissions() ) {
				authorities.add( permission );
			}
		}

		return authorities;
	}

	@Override
	public final String toString() {
		return getPrincipalName();
	}
}
