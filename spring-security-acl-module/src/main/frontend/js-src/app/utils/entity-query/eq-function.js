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
import EQType from "./eq-type";
import {arrayEquals, assertNotNull, convertArgsToArray, isNullOrUndefined} from "../utilities";

/**
 * Represents a function value. A function is identified by a name and optional a set of arguments.
 * @see com.foreach.across.modules.entity.query.EQFunction
 *
 * @author Steven Gentens
 * @since 2.2.0
 */
class EQFunction extends EQType {
  /**
   * The arguments may be:
   *  - an array containing the arguments: [arg1, arg2, arg3]
   *  - a single item: arg1
   *  - a set of items listed separately: arg1, arg2, arg3
   * @param name
   * @param args the arguments of the function.
   */
  constructor( name, ...args ) {
    super();
    assertNotNull( "EQFunction", "name", name );
    this.name = name;
    this.args = isNullOrUndefined( args ) ? [] : convertArgsToArray( args );
  }

  getName() {
    return this.name;
  }

  getArguments() {
    return this.args;
  }

  toString() {
    return `${this.name}(${this.args.join( "," )})`;
  }

  equals( that ) {
    if ( this.valueOf() === that.valueOf() ) {
      return true;
    }
    if ( isNullOrUndefined( that ) ) {
      return false;
    }
    return this.name === that.name && arrayEquals( this.args, that.args );
  }
}

export default EQFunction;
