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
package com.foreach.across.modules.entity.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Simple abstraction interface for defining simple generic queries (in the form of {@link EntityQuery})
 * that can be used to fetch one or more entities.  Used as an additional abstraction for the specific
 * JPA, QueryDsl repositories.
 *
 * @author Arne Vandamme
 * @see com.foreach.across.modules.entity.query.jpa.EntityQueryJpaExecutor
 * @see com.foreach.across.modules.entity.query.querydsl.EntityQueryQueryDslExecutor
 */
public interface EntityQueryExecutor<T>
{
	List<T> findAll( EntityQuery query );

	Page<T> findAll( EntityQuery query, Pageable pageable );
}
