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
package com.foreach.across.modules.entity.query.querydsl;

import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.query.EntityQueryExecutor;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.util.EntityUtils;
import com.mysema.query.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import java.util.List;

/**
 * Implementation of {@link EntityQueryExecutor} that runs against a {@link QueryDslPredicateExecutor} instance.
 *
 * @author Arne Vandamme
 */
public class EntityQueryQueryDslExecutor<T> implements EntityQueryExecutor<T>
{
	private final QueryDslPredicateExecutor<T> queryDslPredicateExecutor;
	private final EntityConfiguration entityConfiguration;

	public EntityQueryQueryDslExecutor( QueryDslPredicateExecutor<T> queryDslPredicateExecutor,
	                                    EntityConfiguration entityConfiguration ) {
		this.queryDslPredicateExecutor = queryDslPredicateExecutor;
		this.entityConfiguration = entityConfiguration;
	}

	@Override
	public List<T> findAll( EntityQuery query ) {
		return EntityUtils.asList( queryDslPredicateExecutor.findAll( predicate( query ) ) );
	}

	@Override
	public Page<T> findAll( EntityQuery query, Pageable pageable ) {
		Predicate predicate = predicate( query );

		if ( pageable == null ) {
			return EntityUtils.asPage( queryDslPredicateExecutor.findAll( predicate ) );
		}

		return queryDslPredicateExecutor.findAll( predicate, pageable );
	}

	private Predicate predicate( EntityQuery query ) {
		return EntityQueryQueryDslUtils.toPredicate( query, entityConfiguration );
	}
}
