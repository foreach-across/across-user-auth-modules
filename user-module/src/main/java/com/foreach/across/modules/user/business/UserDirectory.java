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
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.annotation.concurrent.NotThreadSafe;
import javax.persistence.*;

/**
 * @author Arne Vandamme
 * @since 1.2.0
 */
@NotThreadSafe
@Entity
@Table(name = UserSchemaConfiguration.TABLE_USER_DIRECTORY)
public class UserDirectory extends SettableIdAuditableEntity<UserDirectory>
{
	/**
	 * Unique id of the default internal directory. This directory should always exist.
	 */
	public static final Long DEFAULT_DIRECTORY_ID = 1L;

	@Id
	@GeneratedValue(generator = "seq_um_user_directory_id")
	@GenericGenerator(
			name = "seq_um_user_directory_id",
			strategy = AcrossSequenceGenerator.STRATEGY,
			parameters = {
					@org.hibernate.annotations.Parameter(name = "sequenceName", value = "seq_um_user_directory_id"),
					@org.hibernate.annotations.Parameter(name = "allocationSize", value = "1"),
					@org.hibernate.annotations.Parameter(name = "initialValue", value = "2")
			}
	)
	private Long id;

	@NotBlank
	@Length(max = 255)
	@Column(name = "name")
	private String name;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId( Long id ) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}
}
