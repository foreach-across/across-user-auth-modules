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
import {isNullOrUndefined} from "../utilities";

/**
 * Holds a control builder with a priority.
 */
class EntityQueryPropertyControlItem {
  constructor( entityQueryPropertyControlBuilder, priority ) {
    this.entityQueryPropertyControlBuilder = entityQueryPropertyControlBuilder;
    this.priority = priority;
    if ( isNullOrUndefined( this.priority ) ) {
      this.priority = 0;
    }
  }

  getControlBuilder() {
    return this.entityQueryPropertyControlBuilder;
  }

  getPriority() {
    return this.priority;
  }
}

/**
 * Holds the registered EntityQueryPropertyControlBuilders
 */
class EntityQueryPropertyControlFactory {
  constructor() {
    this.entityQueryPropertyControlBuilders = [];
  }

  /**
   * Registers an EntityQueryPropertyControlBuilder with a given priority.
   * If the priority is not given, it will default to 0.
   * @param propertyControlBuilder
   * @param priority
   */
  register( propertyControlBuilder, priority ) {
    this.entityQueryPropertyControlBuilders.push( new EntityQueryPropertyControlItem( propertyControlBuilder, priority ) );
    this.entityQueryPropertyControlBuilders.sort( ( a, b ) => a.getProperty - b.getPriority );
  }

  /**
   * Creates a control for a given node and queryFilterControl
   * @param node to create a control for
   * @param queryFilterControl that needs to be updated by the control
   * @returns {boolean} true if a control is created.
   */
  createControl( node, queryFilterControl ) {
    for ( let i = 0; i < this.entityQueryPropertyControlBuilders.length; i++ ) {
      const control = this.entityQueryPropertyControlBuilders[i].getControlBuilder()( node, $( node ).find( "[name^='extensions']" ), queryFilterControl );
      if ( control ) {
        return true;
      }
    }
    return false;
  }
}

const entityQueryControlFactory = new EntityQueryPropertyControlFactory();
export default entityQueryControlFactory;
