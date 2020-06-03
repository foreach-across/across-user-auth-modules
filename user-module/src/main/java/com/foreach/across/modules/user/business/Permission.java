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

import com.foreach.across.modules.hibernate.business.SettableIdBasedEntity;
import com.foreach.across.modules.hibernate.id.AcrossSequenceGenerator;
import com.foreach.across.modules.user.config.UserSchemaConfiguration;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * A single permission that can be checked against.
 * Note that this class implements {@link GrantedAuthority} and can be used in Spring security
 * {@code hasAuthority(String)} checks but not {@code hasPermission()} checks.  The latter in Spring security
 * refers to ACL based security.
 */
@Entity
@Table(name = UserSchemaConfiguration.TABLE_PERMISSION)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Permission
		extends SettableIdBasedEntity<Permission>
		implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "seq_um_permission_id")
	@GenericGenerator(
			name = "seq_um_permission_id",
			strategy = AcrossSequenceGenerator.STRATEGY,
			parameters = {
					@org.hibernate.annotations.Parameter(name = "sequenceName", value = "seq_um_permission_id"),
					@org.hibernate.annotations.Parameter(name = "allocationSize", value = "5")
			}
	)
	private long id;

	@NotNull
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "permission_group_id")
	private PermissionGroup group;

	@NotBlank
	@Size(max = 255)
	@Column(name = "name", nullable = false, unique = true)
	private String name;

	@Size(max = 2000)
	@Column(name = "description")
	private String description;

	public Permission() {
	}

	public Permission( String name ) {
		setName( name );
	}

	public Permission( String name, String description ) {
		setName( name );
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId( Long id ) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public String getAuthority() {
		return authorityString( getName() );
	}

	public String getDescription() {
		return description;
	}

	public void setDescription( String description ) {
		this.description = description;
	}

	public PermissionGroup getGroup() {
		return group;
	}

	public void setGroup( PermissionGroup group ) {
		this.group = group;
	}

	@Override
	public String toString() {
		return getName();
	}

	private void writeObject( ObjectOutputStream oos ) throws IOException {
		oos.writeObject( name );
	}

	private void readObject( ObjectInputStream ois ) throws IOException, ClassNotFoundException {
		name = (String) ois.readObject();
	}

	/**
	 * Convert this permission to a Spring security {@link GrantedAuthority} for use in authentication.
	 *
	 * @return granted authority
	 */
	public GrantedAuthority toGrantedAuthority() {
		return new SimpleGrantedAuthority( getAuthority() );
	}

	/**
	 * Generate the authority string for a permission name.  In this case the authority string is
	 * identical to the permission name.  Method is provided to be in line with {@link Role} and
	 * {@link Group} authorities.
	 *
	 * @param permissionName name of the permission
	 * @return authority string
	 */
	public static String authorityString( String permissionName ) {
		return permissionName;
	}
}
