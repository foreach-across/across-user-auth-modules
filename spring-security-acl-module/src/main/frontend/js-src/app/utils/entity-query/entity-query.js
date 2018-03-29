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

import {arrayEquals, assertNotNull, convertArgsToArray, isNullOrUndefined} from "../utilities";
import {EntityQueryOps} from "./entity-query-ops";

/**
 * Defines an query expression. An EntityQuery is formed by a base operand and query expressions and optionally a sort.
 * The query expressions can be {@link EntityQueryCondition}s and other EntityQueries.
 * @see com.foreach.across.modules.entity.query.EntityQuery;
 *
 * @author Steven Gentens
 * @since 2.2.0
 */
class EntityQuery {
  constructor( operand ) {
    this.expressions = [];
    this.operand = EntityQueryOps.AND;
    this.sort = null;
    if ( !isNullOrUndefined( operand ) ) {
      this.operand = operand;
    }
  }

  setSort( sort ) {
    this.sort = sort;
  }

  getSort() {
    return this.sort;
  }

  add( expression ) {
    this.expressions.push( expression );
  }

  getOperand() {
    return this.operand;
  }

  getExpressions() {
    return this.expressions;
  }

  /**
   * The expressions may be:
   *  - an array containing the arguments: [arg1, arg2, arg3]
   *  - a single item: arg1
   *  - a set of items listed separately: arg1, arg2, arg3
   * @param expressions for the entity query
   */
  setExpressions( ...expressions ) {
    assertNotNull( "EntityQuery.setExpressions", "expressions", expressions );
    this.expressions = convertArgsToArray( expressions );
  }

  equals( that ) {
    if ( this.valueOf() === that.valueOf() ) {
      return true;
    }
    if ( isNullOrUndefined( that ) || !(that instanceof EntityQuery) ) {
      return false;
    }
    if ( !isNullOrUndefined( this.expressions ) ? !arrayEquals( this.expressions, that.expressions ) : !isNullOrUndefined( that.expressions ) ) {
      return false;
    }
    if ( this.operand !== that.operand ) {
      return false;
    }
    if ( !isNullOrUndefined( this.sort ) ? !this.sort.equals( that.sort ) : !isNullOrUndefined( that.sort ) ) {
      return false;
    }
    return true;
  }

  parseSort() {
    return isNullOrUndefined( this.sort ) ? "" :
      ` ${this.sort.toString()}`;
  }

  toString() {
    return `${this.operand.toString( null, ...this.expressions )}${this.parseSort()}`.trim();
  }

  static all( sort ) {
    const query = new EntityQuery();
    if ( !isNullOrUndefined( sort ) ) {
      query.setSort( sort );
    }
    return query;
  }

  static create( operand, ...expressions ) {
    const query = new EntityQuery( operand );
    convertArgsToArray( expressions ).forEach( expr => query.add( expr ) );
    return query;
  }

  static and( ...expressions ) {
    return this.create( EntityQueryOps.AND, ...expressions );
  }

  static or( ...expressions ) {
    return this.create( EntityQueryOps.OR, ...expressions );
  }
}

export default EntityQuery;
