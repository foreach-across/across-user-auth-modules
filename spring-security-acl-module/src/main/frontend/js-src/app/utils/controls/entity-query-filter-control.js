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
/**
 * @author Steven Gentens
 * @since 2.2.0
 */
import $ from "jquery";
import EntityQueryPropertyControlFactory from "./entity-query-property-control-factory";
import EntityQuery from "../entity-query/entity-query";
import {isNullOrUndefined} from "../utilities";
import {EntityQueryOps} from "../entity-query/entity-query-ops";
import EntityQueryCondition from "../entity-query/entity-query-condition";

/**
 * Holds the EntityQuery for a set of controls and updates their corresponding EntityQueryFilter
 */
class EntityQueryFilterControl {
  /**
   * @param nodes the set of controls
   * @param eqlFilter the EntityQueryFilter
   */
  constructor( nodes, eqlFilter ) {
    this.conditions = new Map();
    this.entityQuery = null;
    this.eqlFilter = eqlFilter;
    this.initControlFactories( nodes );
  }

  resetEntityQuery() {
    this.entityQuery = EntityQuery.and( [...this.conditions.values()] );
    $( this.eqlFilter ).val( this.entityQuery.toString() );
  }

  /**
   * Sets the EntityQueryCondition for a given property.
   * If the given condition is null, it will remove the existing condition.
   * @param property to set a condition for
   * @param condition for the given property
   * @param reset whether the entityquery should be reset
   */
  setPropertyCondition( property, condition, reset ) {
    if ( !isNullOrUndefined( condition ) ) {
      let conditionToUse = condition;
      const values = condition.getArguments();
      if ( values.length === 1 && values[0].value === "NULL" ) {
        switch ( condition.getOperand() ) {
          case EntityQueryOps.EQ:
            conditionToUse = new EntityQueryCondition( property, EntityQueryOps.IS_NULL, values );
            break;
          case EntityQueryOps.NEQ:
            conditionToUse = new EntityQueryCondition( property, EntityQueryOps.IS_NOT_NULL, values );
            break;
          default:
            break;
        }
      }
      this.conditions.set( property, conditionToUse );
    }
    else if ( this.conditions.has( property ) ) {
      this.conditions.delete( property );
    }
    if ( reset ) {
      this.resetEntityQuery();
    }
  }

  initControlFactories( nodes ) {
    for ( let i = 0; i < nodes.length; i++ ) {
      EntityQueryPropertyControlFactory.createControl( nodes[i], this );
    }
  }
}

export default EntityQueryFilterControl;
