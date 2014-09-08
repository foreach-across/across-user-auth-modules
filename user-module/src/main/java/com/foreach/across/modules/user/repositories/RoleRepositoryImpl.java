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

import com.foreach.across.modules.user.business.Role;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Repository
public class RoleRepositoryImpl implements RoleRepository
{
	@Autowired
	private SessionFactory sessionFactory;

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public Collection<Role> getAll() {
		return (Collection<Role>) sessionFactory.getCurrentSession().createCriteria( Role.class ).setResultTransformer(
				Criteria.DISTINCT_ROOT_ENTITY ).list();
	}

	@Transactional(readOnly = true)
	@Override
	public Role getRole( String name ) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria( Role.class );
		criteria.add( Restrictions.eq( "name", name ) );

		return (Role) criteria.uniqueResult();
	}

	@Transactional
	@Override
	public void delete( Role role ) {
		sessionFactory.getCurrentSession().delete( role );
	}

	@Transactional
	@Override
	public void save( Role role ) {
		sessionFactory.getCurrentSession().saveOrUpdate( role );
	}
}
