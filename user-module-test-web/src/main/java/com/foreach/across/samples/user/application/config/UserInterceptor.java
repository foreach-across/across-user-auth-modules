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

package com.foreach.across.samples.user.application.config;

import com.foreach.across.modules.hibernate.aop.EntityInterceptorAdapter;
import com.foreach.across.modules.hibernate.services.HibernateSessionHolder;
import com.foreach.across.modules.user.business.User;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserInterceptor extends EntityInterceptorAdapter<User>
{
	@Autowired
	private HibernateSessionHolder sessionHolder;

	@Override
	public boolean handles( Class<?> entityClass ) {
		return entityClass.isAssignableFrom( User.class );
	}

	@Override
	public void afterCreate( User entity ) {
		flushSessionWhenDirty();
	}

	/**
	 * When userService.save has been called, it might be possible that the records are not flushed to the database yet.
	 * This would lead to an referential integrity constraint when saving the userProperties.
	 * This occurs because userServices uses hibernate orm, but userPropertiesRepositories uses a jdbcTemplate.
	 * We only flush if the session is dirty for performance reasons.
	 */
	private void flushSessionWhenDirty() {
		Session session = sessionHolder.getCurrentSession();
		if ( session != null && session.isDirty() ) {
			// The userService.save has been called, but the records are not flushed to the database yet

			session.flush();
		}
	}
}
