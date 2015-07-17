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
package com.foreach.across.modules.entity.query.jpa;

import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.query.EntityQueryExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Implementation of {@link EntityQueryExecutor} that runs against a {@link JpaSpecificationExecutor} instance.
 *
 * @author Arne Vandamme
 */
public class EntityQueryJpaExecutor<T> implements EntityQueryExecutor<T>
{
	private final JpaSpecificationExecutor<T> jpaSpecificationExecutor;

	public EntityQueryJpaExecutor( JpaSpecificationExecutor<T> jpaSpecificationExecutor ) {
		this.jpaSpecificationExecutor = jpaSpecificationExecutor;
	}

	@Override
	public Page<T> findAll( EntityQuery query, Pageable pageable ) {
		if ( pageable == null ) {
			return new PageImpl<>( findAll( query ) );
		}

		return jpaSpecificationExecutor.findAll( EntityQueryJpaUtils.<T>toSpecification( query ), pageable );
	}

	@Override
	public List<T> findAll( EntityQuery query ) {
		return jpaSpecificationExecutor.findAll( EntityQueryJpaUtils.<T>toSpecification( query ) );
	}
}
