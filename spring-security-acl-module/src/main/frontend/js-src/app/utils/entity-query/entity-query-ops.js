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
import _ from "lodash";
// import isString from "lodash.isstring";
import {isNullOrUndefined} from "../utilities";

/**
 * Defines an operand that can be used in a query.
 * @see com.foreach.across.modules.entity.query.EntityQueryOps
 *
 * @author Steven Gentens
 * @since 2.2.0
 */
class EntityQueryOperand {
  constructor( opswriter, ...tokens ) {
    this.opsWriter = opswriter;
    this.tokens = tokens;
  }

  toString( propertyName, ...args ) {
    return this.opsWriter( propertyName, ...args );
  }

  getToken() {
    return this.tokens[0];
  }

  equals( that ) {
    return this.getToken() === that.getToken();
  }
}

function escapeChars( value ) {
  return value.replace( "\\", "\\\\" ).replace( "'", "\\'" );
}

function objectAsString( object ) {
  if ( isNullOrUndefined( object ) ) {
    return "NULL";
  }
  if ( _.isString( object ) || typeof object === "string" ) {
    return `'${escapeChars( object )}'`;
  }
  return object.toString();
}

function joinAsStrings( ...args ) {
  return args.map( arg => objectAsString( arg ) ).join( "," );
}

/**
 * Defines the EntityQueryToken for the grouping operands AND and OR
 * @type {{AND: string, OR: string}}
 */
const EntityQueryToken = {
  "AND": "and",
  "OR": "or"
};

/**
 * Set braces around the query expression if necessary
 * @param expr the query expression
 * @returns ({@code(query)}) if it contains {@link EntityQueryOps.AND#getToken()} or {@link EntityQueryOps.AND#getToken()}
 *          or else the query.
 */
function setBraces( expr ) {
  const query = objectAsString( expr );
  if ( query.search( ` ${EntityQueryToken.AND} ` ) !== -1 || query.search( ` ${EntityQueryToken.OR} ` ) !== -1 ) {
    return `(${query})`;
  }
  return query;
}

export const EntityQueryOps = {
    "AND": new EntityQueryOperand( ( field, ...args ) => `${args.map( arg => setBraces( arg ) ).join( " and " )}`, EntityQueryToken.AND ),
    "OR": new EntityQueryOperand( ( field, ...args ) => `${args.map( arg => setBraces( arg ) ).join( " or " )}`, EntityQueryToken.OR ),
    "EQ": new EntityQueryOperand( ( field, ...args ) => `${field} = ${args.length > 0 ? objectAsString( args[0] ) : ""}`, "=" ),
    "NEQ": new EntityQueryOperand( ( field, ...args ) => `${field} != ${args.length > 0 ? objectAsString( args[0] ) : ""}`, "!=", "<>" ),
    "CONTAINS": new EntityQueryOperand( ( field, ...args ) => `${field} contains ${objectAsString( args[0] )}`, "contains" ),
    "NOT_CONTAINS": new EntityQueryOperand( ( field, ...args ) => `${field} not contains ${objectAsString( args[0] )}`, "not contains" ),
    "IN": new EntityQueryOperand( ( field, ...args ) => `${field} in (${joinAsStrings( ...args )})`, "in" ),
    "NOT_IN": new EntityQueryOperand( ( field, ...args ) => `${field} not in (${joinAsStrings( ...args )})`, "not in" ),
    "LIKE": new EntityQueryOperand( ( field, ...args ) => `${field} like ${objectAsString( args[0] )}`, "like" ),
    "LIKE_IC": new EntityQueryOperand( ( field, ...args ) => `${field} ilike ${objectAsString( args[0] )}`, "ilike" ),
    "NOT_LIKE": new EntityQueryOperand( ( field, ...args ) => `${field} not like ${objectAsString( args[0] )}`, "not like" ),
    "NOT_LIKE_IC": new EntityQueryOperand( ( field, ...args ) => `${field} not ilike ${objectAsString( args[0] )}`, "not ilike" ),
    "GT": new EntityQueryOperand( ( field, ...args ) => `${field} > ${objectAsString( args[0] )}`, ">" ),
    "GE": new EntityQueryOperand( ( field, ...args ) => `${field} >= ${objectAsString( args[0] )}`, ">=" ),
    "LT": new EntityQueryOperand( ( field, ...args ) => `${field} < ${objectAsString( args[0] )}`
      , "<" ),
    "LE": new EntityQueryOperand( ( field, ...args ) => `${field} <= ${objectAsString( args[0] )}`, "<=" ),
    "IS_NULL": new EntityQueryOperand( ( field, ...args ) => `${field} is NULL`, "is" ),
    "IS_NOT_NULL": new EntityQueryOperand( ( field, ...args ) => `${field} is not NULL`
      , "is not" ),
    "IS_EMPTY": new EntityQueryOperand( ( field, ...args ) => `${field} is EMPTY`, "is" ),
    "IS_NOT_EMPTY": new EntityQueryOperand( ( field, ...args ) => `${field} is not EMPTY`, "is not" ),

    "forToken": function( token ) {
      const lookup = token.toLowerCase().trim();
      const values = Object.values( EntityQueryOps );
      for ( let i = 0; i < values.length; i++ ) {
        const currentValue = values[i];
        if ( currentValue instanceof EntityQueryOperand && currentValue.tokens.includes( lookup ) ) {
          return currentValue;
        }
      }
      return null;
    }

  }
;
