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
(function ( $ ) {
    $( document ).ready( function () {

        function selectValue( container )
        {
            var ctrl = container.find( 'select' );

            if ( ctrl.length ) {
                return {id: ctrl.val(), label: ctrl.find( 'option:selected' ).text()};
            }
            else {
                ctrl = container.find( 'input[type=text]' );

                if ( ctrl.length ) {
                    return {id: ctrl.val(), label: ctrl.val()};
                }
            }
        }

        $( '.acl-permissions-form-section button:last-child' ).on( 'click', function () {
            var selectedValue = selectValue( $( this ).closest( 'div' ) );

            var section = $( this ).closest( '.acl-permissions-form-section' );

            if ( section.find( 'input[name$=".id"][value="' + selectedValue.id + '"]' ).length === 0 ) {
                var templateRow = $( 'tr.hidden', section ).clone();
                templateRow.removeClass( 'hidden' );

                var index = Date.now();
                $( 'input', templateRow ).each( function () {
                    var control = $( this );
                    control.attr( 'name', control.attr( 'name' ).replace( '{{itemIndex}}', index ) )
                            .attr( 'id', control.attr( 'name' ) )
                            .removeAttr( 'disabled' );
                } );
                templateRow.find( 'input[name$=".id"]' ).val( selectedValue.id );
                $( 'td:first', templateRow ).text( selectedValue.label );

                $( 'table tbody', section ).append( templateRow );
            }
        } );
    } );
}( jQuery ));