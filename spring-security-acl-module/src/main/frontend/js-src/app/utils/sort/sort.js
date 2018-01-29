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
import {arrayEquals, convertArgsToArray, isNullOrUndefined} from "../utilities";
import SortOrder from "./sort-order";

/**
 * Defines a sort option for queries. A sort is build of {@link SortOrder}s.
 *
 * @author Steven Gentens
 * @since 2.2.0
 */
class Sort {
  constructor( ...orders ) {
    this.orders = convertArgsToArray( orders );
  }

  getOrders() {
    return this.orders;
  }

  /**
   * Adds an {@link SortOrder} to the current orders.
   * If there is an order present for the same property, the existing order will be removed
   * and the new order will be placed at the start of the current orders.
   * @param order to add
   */
  add( order ) {
    if ( isNullOrUndefined( order ) ) {
      return;
    }

    for ( let i = 0; i < this.orders.length; i++ ) {
      const currOrder = this.orders[i];
      if ( currOrder.equals( order ) ) {
        this.orders.splice( i, 1 );
      }
    }

    this.orders.splice( 0, 0, order );
  }

  equals( that ) {
    if ( this.valueOf() === that.valueOf() ) {
      return true;
    }
    if ( isNullOrUndefined( that ) || !(that instanceof SortOrder) ) {
      return false;
    }
    if ( !arrayEquals( this.orders, that.orders ) ) {
      return false;
    }
    return true;
  }

  toString() {
    return `order by ${this.orders
      .map( order => order.toString() )
      .join( ", " )}`;
  }
}

export default Sort;
