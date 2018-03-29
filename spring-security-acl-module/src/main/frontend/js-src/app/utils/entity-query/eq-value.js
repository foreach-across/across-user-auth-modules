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
import {assertNotNull, isNullOrUndefined} from "../utilities";

/**
 * Represents a value in an {@link EntityQuery} or {@link EntityQueryCondition}
 * @see com.foreach.across.modules.entity.query.EQValue
 *
 * @author Steven Gentens
 * @since 2.2.0
 */
class EQValue extends EQType {
  constructor( value ) {
    super();
    assertNotNull( "EQValue", "value", value );
    this.value = value;
  }

  getValue() {
    return this.value;
  }

  toString() {
    return this.value.toString();
  }

  equals( that ) {
    if ( this.valueOf() === that.valueOf() ) {
      return true;
    }
    if ( isNullOrUndefined( that ) ) {
      return false;
    }
    return this.toString() === that.toString();
  }
}

export default EQValue;
