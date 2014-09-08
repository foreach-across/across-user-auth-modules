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
package com.foreach.across.modules.user.repositories;

import com.foreach.across.modules.hibernate.repositories.BasicRepositoryImpl;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.converters.FieldUtils;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserRepositoryImpl extends BasicRepositoryImpl<User> implements UserRepository
{
	@Transactional(readOnly = true)
	@Override
	public User getByEmail( String email ) {
		return (User) distinct()
				.add( Restrictions.eq( "email", FieldUtils.lowerCase( email ) ) )
				.uniqueResult();
	}

	@Transactional(readOnly = true)
	@Override
	public User getByUsername( String userName ) {
		return (User) distinct()
				.add( Restrictions.eq( "username", FieldUtils.lowerCase( userName ) ) )
				.uniqueResult();
	}
}
