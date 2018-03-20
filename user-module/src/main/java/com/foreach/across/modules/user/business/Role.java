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

import com.foreach.across.modules.hibernate.business.SettableIdAuditableEntity;
import com.foreach.across.modules.hibernate.id.AcrossSequenceGenerator;
import com.foreach.across.modules.user.config.UserSchemaConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import javax.annotation.concurrent.NotThreadSafe;
import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A role represents a number of permissions that can be applied for a principal.
 * The role is by itself also a {@link GrantedAuthority} that can be used for security checking.
 */
@NotThreadSafe
@Entity
@Table(name = UserSchemaConfiguration.TABLE_ROLE)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Role extends SettableIdAuditableEntity<Role>
		implements GrantedAuthority, Serializable
{
	private static final long serialVersionUID = 1L;
	public static final String AUTHORITY_PREFIX = "ROLE_";

	@Id
	@GeneratedValue(generator = "seq_um_role_id")
	@GenericGenerator(
			name = "seq_um_role_id",
			strategy = AcrossSequenceGenerator.STRATEGY,
			parameters = {
					@org.hibernate.annotations.Parameter(name = "sequenceName", value = "seq_um_role_id"),
					@org.hibernate.annotations.Parameter(name = "allocationSize", value = "1")
			}
	)
	private Long id;

	@NotBlank
	@Size(max = 255)
	@Column(name = "authority", nullable = false, unique = true)
	@Pattern(regexp = "^ROLE_[0-9A-Z_].*$", flags = Pattern.Flag.CASE_INSENSITIVE)
	private String authority;

	@NotBlank
	@Size(max = 255)
	@Column(name = "name", nullable = false, unique = true)
	private String name;

	@Size(max = 2000)
	@Column(name = "description")
	private String description;

	@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@ManyToMany(fetch = FetchType.EAGER)
	@BatchSize(size = 50)
	@JoinTable(
			name = UserSchemaConfiguration.TABLE_ROLE_PERMISSION,
			joinColumns = @JoinColumn(name = "role_id"),
			inverseJoinColumns = @JoinColumn(name = "permission_id"))
	private Set<Permission> permissions = new HashSet<>();

	public Role() {
	}

	public Role( String authority ) {
		this( authority, authority );
	}

	public Role( String authority, String name ) {
		setAuthority( authority );
		setName( name );
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId( Long id ) {
		this.id = id;
	}

	public String getAuthority() {
		return authorityString( authority );
	}

	public void setAuthority( String authority ) {
		this.authority = authorityString( authority );
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription( String description ) {
		this.description = description;
	}

	public Set<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions( Collection<Permission> permissions ) {
		getPermissions().clear();
		if ( permissions != null ) {
			getPermissions().addAll( permissions );
		}
	}

	public void addPermission( Permission... permissions ) {
		for ( Permission permission : permissions ) {
			Assert.notNull( permission, "Permission cannot be null" );
			getPermissions().add( permission );
		}
	}

	/**
	 * Does the user have a permission with the requested authority string.
	 *
	 * @param authority string to check
	 * @return {@code true} if permission is present
	 */
	public boolean hasPermission( String authority ) {
		String authorityString = Permission.authorityString( authority );
		for ( Permission p : getPermissions() ) {
			if ( StringUtils.equals( p.getAuthority(), authorityString ) ) {
				return true;
			}
		}
		return false;
	}

	public boolean hasPermission( Permission permission ) {
		return getPermissions().contains( permission );
	}

	@Override
	public String toString() {
		return getAuthority();
	}

	/**
	 * Generate the authority string for a role, applies the prefix if is not yet present.
	 *
	 * @param role authority name of the role
	 * @return authority string
	 */
	public static String authorityString( String role ) {
		return StringUtils.isBlank( role ) ? null : AUTHORITY_PREFIX + StringUtils.removeStartIgnoreCase( role,
		                                                                                                  AUTHORITY_PREFIX );
	}
}
