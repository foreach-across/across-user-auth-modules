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

package com.foreach.across.modules.spring.security.acl.support;

import com.foreach.across.modules.hibernate.business.IdBasedEntity;
import com.foreach.across.modules.spring.security.acl.business.SecurityPrincipalSid;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipalId;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.ClassUtils;

import java.io.Serializable;

/**
 * Helper utility class for creating ACL related identities for common types.
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
@UtilityClass
public class AclUtils
{
	public static Sid sid( Authentication authentication ) {
		return new PrincipalSid( authentication );
	}

	public static Sid sidForAuthority( String authority ) {
		return new GrantedAuthoritySid( authority );
	}

	public static Sid sidForPrincipal( String principal ) {
		return new PrincipalSid( principal );
	}

	public static Sid sid( GrantedAuthority authority ) {
		return new GrantedAuthoritySid( authority );
	}

	public static Sid sid( SecurityPrincipal principal ) {
		return SecurityPrincipalSid.of( principal );
	}

	public static Sid sid( SecurityPrincipalId principalId ) {
		return SecurityPrincipalSid.of( principalId );
	}

	public static ObjectIdentity objectIdentity( IdBasedEntity entity ) {
		return entity != null ? objectIdentity( ClassUtils.getUserClass( entity.getClass() ), entity.getId() ) : null;
	}

	public static ObjectIdentity objectIdentity( @NonNull Class<?> objectType, @NonNull Serializable id ) {
		return new ObjectIdentityImpl( ClassUtils.getUserClass( objectType ), id );
	}

	public static ObjectIdentity objectIdentity( Object object ) {
		return object != null ? new ObjectIdentityImpl( object ) : null;
	}
}
