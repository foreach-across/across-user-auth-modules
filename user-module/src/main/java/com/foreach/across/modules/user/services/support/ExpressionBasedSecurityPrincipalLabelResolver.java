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

package com.foreach.across.modules.user.services.support;

import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalLabelResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.Assert;

import java.util.Optional;

/**
 * Uses a Spring Expression Language expression to transform a {@link SecurityPrincipal} of a required type
 * into a descriptive label.  If the principal passed is not of the type configured, no label will be resolved.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class ExpressionBasedSecurityPrincipalLabelResolver implements SecurityPrincipalLabelResolver
{
	private static final ExpressionParser PARSER = new SpelExpressionParser();

	private final Class<? extends SecurityPrincipal> requiredPrincipalType;
	private Expression labelExpression;

	public ExpressionBasedSecurityPrincipalLabelResolver( Class<? extends SecurityPrincipal> requiredPrincipalType,
	                                                      String labelExpression ) {
		Assert.notNull( requiredPrincipalType );

		this.requiredPrincipalType = requiredPrincipalType;
		setLabelExpression( labelExpression );

	}

	/**
	 * Set the SpEL expression to be used for creating the label.
	 *
	 * @param labelExpression SpEL expression
	 */
	public void setLabelExpression( String labelExpression ) {
		Assert.notNull( labelExpression );
		this.labelExpression = PARSER.parseExpression( labelExpression );
	}

	@Override
	public Optional<String> resolvePrincipalLabel( SecurityPrincipal principal ) {
		if ( requiredPrincipalType.isInstance( principal ) ) {
			return Optional.ofNullable( labelExpression.getValue( principal, String.class ) );
		}

		return Optional.empty();
	}
}
