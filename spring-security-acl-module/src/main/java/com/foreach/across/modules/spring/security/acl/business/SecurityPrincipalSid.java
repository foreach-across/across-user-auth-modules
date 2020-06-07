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
package com.foreach.across.modules.spring.security.acl.business;

import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipalId;
import org.springframework.security.acls.domain.PrincipalSid;

/**
 * @author Arne Vandamme
 */
public class SecurityPrincipalSid extends PrincipalSid
{
	private SecurityPrincipalSid( SecurityPrincipalId principalId ) {
		super( principalId.toString() );
	}

	public static SecurityPrincipalSid of( SecurityPrincipal principal ) {
		return of( principal.getSecurityPrincipalId() );
	}

	public static SecurityPrincipalSid of( SecurityPrincipalId principalId ) {
		return new SecurityPrincipalSid( principalId );
	}
}
