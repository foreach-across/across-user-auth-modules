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

package com.foreach.across.modules.user.services;

import com.foreach.across.modules.hibernate.jpa.repositories.IdBasedEntityJpaRepository;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.Predicate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public abstract class AbstractQueryDslPredicateExecutorTest
{
	@SuppressWarnings( "unchecked" )
	protected void queryDslPredicateExecutorTest( QueryDslPredicateExecutor<?> service,
	                                              IdBasedEntityJpaRepository<?> jpaRepository ) {
		Predicate predicate = mock( Predicate.class );
		OrderSpecifier[] orderSpecifiers = new OrderSpecifier[2];
		Pageable pageable = mock( Pageable.class );

		QueryDslPredicateExecutor repository = (QueryDslPredicateExecutor) jpaRepository;

		service.findAll( predicate );
		verify( repository ).findAll( predicate );

		service.findAll( predicate, orderSpecifiers );
		verify( repository ).findAll( predicate, orderSpecifiers );

		service.findAll( predicate, pageable );
		verify( repository ).findAll( predicate, pageable );

		service.findOne( predicate );
		verify( repository ).findOne( predicate );

		service.count( predicate );
		verify( repository ).count( predicate );
	}
}
