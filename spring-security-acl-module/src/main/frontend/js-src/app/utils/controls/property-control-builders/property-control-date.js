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
import {EntityQueryOps} from "../../entity-query/entity-query-ops";
import EntityQueryCondition from "../../entity-query/entity-query-condition";
import {isEmptyArray, isNullOrUndefined} from "../../utilities";

function setCondition( controlItem, control, filterControl, reset = true ) {
  const property = $( controlItem ).data( "entity-query-property" );
  const value = $( control ).attr( "value" );
  let condition = null;

  if ( !isNullOrUndefined( value ) && value.trim() !== "" ) {
    const operand = EntityQueryOps[$( controlItem ).data( "entity-query-operand" )];
    condition = new EntityQueryCondition( property, operand, value );
  }

  filterControl.setPropertyCondition( property, condition, reset );
}

/**
 * Allows creation of an EntityQueryPropertyDateControl for a given node and EntityQueryFilterControl
 * if the given node has the "bootstrapui-datetimepicker" attribute and has inputs with type "text" as children.
 * @param node to create a control for
 * @param filterControl to receive the condition from the control
 * @returns {boolean} true if a control has been made.
 */
export function createDateControl( node, control, filterControl ) {
  if ( !isEmptyArray( $( node ).has( "[data-bootstrapui-datetimepicker]" ) ) && $( node ).has( "input[type=text]" ) ) {
    setCondition( node, control, filterControl, false );
    $( node ).focusout( {"formGroup": $( node ), "item": $( control ), "filter": filterControl},
                        event => setCondition( event.data.formGroup, event.data.item, event.data.filter ) );
    return true;
  }
  return false;
}
