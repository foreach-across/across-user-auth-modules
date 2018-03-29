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

package test.acl.application.domain.user;

import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.domain.Persistable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
@Entity
@Table(name = "my_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@Builder
public class User implements Persistable<Long>, SecurityPrincipal, Serializable
{
	@Id
	@GeneratedValue
	private Long id;

	@NotBlank
	@Length(max = 100)
	@Column(name = "name")
	private String name;

	@Override
	public String getPrincipalName() {
		return getName();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singleton( new SimpleGrantedAuthority( "login" ) );
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public boolean isNew() {
		return id == null || id == 0;
	}
}