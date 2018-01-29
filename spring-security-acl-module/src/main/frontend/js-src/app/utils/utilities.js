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
import {NullOrUndefinedException} from "./exceptions";
import EQValue from "./entity-query/eq-value";
import EQString from "./entity-query/eq-string";

/**
 * Checks whether the incoming object is {@code null} or is {@code undefined}.
 * @param object
 * @returns (boolean) whether or not the object is null or undefined.
 */
export function isNullOrUndefined( object ) {
  return (object === null || typeof object === "undefined");
}

/**
 * Checks whether two arrays are the same based on their length and the elements at their corresponding positions
 * @param array1
 * @param array2
 * @returns {boolean}
 */
export function arrayEquals( array1, array2 ) {
  const length = array1.length;
  if ( length !== array2.length ) {
    return false;
  }

  for ( let i = 0; i < length; i++ ) {
    const o1 = array1[i];
    const o2 = array2[i];
    if ( !(isNullOrUndefined( o1 ) ? isNullOrUndefined( o2 ) : o1.equals( o2 )) ) {
      return false;
    }
  }
  return true;
}

/**
 * Converts an array of values (as a result of a spread operator) to a useable array.
 * Returns the following:
 *    - if the array is empty, returns the array
 *    - If the array contains multiple values, returns the array.
 *    - If the array contains a single value, which is also an array,
 *      returns the inner array.
 *    - If the array contains a single value that is not null or empty,
 *      returns the value wrapped in an array.
 * @param values an array of values
 * @returns the result as an array.
 * @throws NullOrUndefinedException if the values contains a single element that is null or undefined
 */
export function convertArgsToArray( values ) {
  if ( values.length === 0 ) {
    return values;
  }
  else if ( values.length > 1 ) {
    return [...values];
  }
  else if ( values[0] instanceof Array ) {
    return [...values[0]];
  }
  else if ( !isNullOrUndefined( values[0] ) ) {
    return [values[0]];
  }
  throw new NullOrUndefinedException( null, null, values );
}

/**
 * Validates whether an object is null or undefined
 * @param location  of the validation
 * @param property name of the object
 * @param object to validate
 * @throws NullOrUndefinedException if the object is {@code null} or {@code undefined}
 */
export function assertNotNull( location, property, object ) {
  if ( isNullOrUndefined( object ) ) {
    throw new NullOrUndefinedException( location, property, object );
  }
}

export function convertToTypedValue( value ) {
  if ( value.startsWith( "'" ) && value.endsWith( "'" ) ) {
    return new EQString( value.substring( 1, value.length - 1 ) );
  }
  return new EQValue( value );
}

/**
 * Checks whether an array is empty.
 * @param array to check
 * @returns {boolean} whether or not it is empty
 */
export function isEmptyArray( array ) {
  return isNullOrUndefined( array ) || isNullOrUndefined( array.length ) || array.length === 0;
}
