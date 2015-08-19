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
var BootstrapUiModule = {
    /**
     * Scan for and initialize all known form element types.
     *
     * @param node optional parent to limit the scan
     */
    initializeFormElements: function ( node ) {
        /**
         * Find and activate all date time pickers.
         */
        $( '[data-bootstrapui-datetimepicker]', node ).each( function () {
            var configuration = $( this ).data( 'bootstrapui-datetimepicker' );
            var exportFormat = configuration.exportFormat;

            delete configuration.exportFormat;

            $( this ).datetimepicker( configuration )
                    .on( 'dp.change', function ( e ) {
                             var exchangeValue = e.date ? moment( e.date ).format( exportFormat ) : '';
                             $( 'input[type=hidden]', $( this ) ).attr( 'value', exchangeValue );
                         } );
        } );

        /**
         * Find an activate all autoNumeric form elements.
         */
        $( '[data-bootstrapui-numeric]', node ).each( function () {
            var configuration = $( this ).data( 'bootstrapui-numeric' );
            var name = $( this ).attr( 'name' );

            var multiplier = configuration.multiplier ? configuration.multiplier : 1;

            var multiplied;

            if ( multiplier != 1 ) {
                var currentValue = $( this ).val();
                if ( currentValue && !isNaN( currentValue ) ) {
                    multiplied = parseFloat( currentValue ) * multiplier;
                }
            }

            $( this )
                    .autoNumeric( 'init', configuration )
                    .bind( 'blur focusout keypress keyup', function () {
                               if ( name.length > 1 && name[0] == '_' ) {
                                   var val = $( this ).autoNumeric( 'get' );

                                   if ( multiplier != 1 ) {
                                       val = val / multiplier;
                                   }

                                   $( 'input[type=hidden][name="' + name.substring( 1 ) + '"]' ).val( val );
                               }
                           } );

            if ( multiplied ) {
                $( this ).autoNumeric( 'set', multiplied );
            }
        } );
    }
};

$( document ).ready( function () {
    BootstrapUiModule.initializeFormElements();
} );