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

function isSingleNullArgument( args ) {
  return args.length === 1 && args[0].toString() === "'NULL'";
}

/**
 * Defines an entity query expression for a single property.
 * @see com.foreach.across.modules.entity.query.EntityQueryCondition
 *
 * @author Steven Gentens
 * @since 2.2.0
 */
class EntityQueryCondition {
  /**
   * The arguments may be:
   *  - an array containing the arguments: [arg1, arg2, arg3]
   *  - a single item: arg1
   *  - a set of items listed separately: arg1, arg2, arg3
   * @param property to query on
   * @param operand to use in the condition
   * @param args the arguments of the condition.
   */
  constructor( property, operand, ...args ) {
    this.prop = property;
    this.operand = operand;
    this.args = convertArgsToArray( args );

    if ( isSingleNullArgument( this.args ) ) {
      if ( EntityQueryOps.EQ.equals( operand ) ) {
        this.operand = EntityQueryOps.IS_NULL;
      }
      else if ( EntityQueryOps.NEQ.equals( operand ) ) {
        this.operand = EntityQueryOps.IS_NOT_NULL;
      }
    }
  }

  getProperty() {
    return this.prop;
  }

  setProperty( property ) {
    this.prop = property;
  }

  getOperand() {
    return this.operand;
  }

  setOperand( operand ) {
    this.operand = operand;
  }

  getArguments() {
    return this.args;
  }

  setArguments( args ) {
    assertNotNull( "EntityQueryCondition.setArguments", "args", args );
    this.args = args;
  }

  hasArguments() {
    return this.args.length > 0;
  }

  getFirstArgument() {
    return this.args.length > 0 ? this.args[0] : null;
  }

  equals( that ) {
    if ( this.valueOf() === that.valueOf() ) {
      return true;
    }
    if ( isNullOrUndefined( that ) || !(that instanceof EntityQueryCondition) ) {
      return false;
    }
    if ( !arrayEquals( this.args, that.expressions ) ) {
      return false;
    }
    if ( this.operand !== that.operand ) {
      return false;
    }
    if ( !isNullOrUndefined( this.prop )
        ? !this.prop.equals( that.prop )
        : !isNullOrUndefined( that.prop ) ) {
      return false;
    }
    return true;
  }

  toString() {
    return this.operand.toString( this.prop, ...this.args );
  }
}

export default EntityQueryCondition;
