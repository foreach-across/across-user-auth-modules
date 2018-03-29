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
import {assertNotNull, isNullOrUndefined} from "../utilities";

export const Direction = {
  "ASC": "ASC",
  "DESC": "DESC"
};

/**
 * Defines the order of a sort. A SortOrder has a property and a direction.
 * The default direction is {@link Direction.ASC}.
 *
 * @author Steven Gentens
 * @since 2.2.0
 */
class SortOrder {
  constructor( property, direction ) {
    assertNotNull( "SortOrder", "property", property );
    this.property = property;
    this.direction = direction;
    if ( isNullOrUndefined( direction ) ) {
      this.direction = Direction.ASC;
    }
  }

  getProperty() {
    return this.property;
  }

  getDirection() {
    return this.direction;
  }

  equals( that ) {
    if ( this.valueOf() === that.valueOf() ) {
      return true;
    }
    if ( isNullOrUndefined( that ) || !(that instanceof SortOrder) ) {
      return false;
    }
    if ( this.property !== that.property ) {
      return false;
    }
    return true;
  }

  toString() {
    return `${this.property} ${this.direction}`;
  }
}

export default SortOrder;
