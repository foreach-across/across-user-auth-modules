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
var SortableTable = function ( element ) {
    var table = $( element );
    var id = $( element ).attr( 'data-tbl' );
    var page = table.attr( 'data-tbl-current-page' );

    this.size = table.attr( 'data-tbl-size' );
    this.totalPages = table.attr( 'data-tbl-total-pages' );
    this.sort = [];

    var currentSort = table.data( 'tbl-sort' );
    this.sort = currentSort != null ? currentSort : [];

    var tblSort = null;

    this.sortables = $( "[data-tbl='" + id + "'][data-tbl-sort-property]", table );
    this.sortables.removeClass( 'asc', 'desc' );

    for ( var i = 0; i < this.sort.length; i++ ) {
        var order = this.sort[i];

        $( "[data-tbl='" + id + "'][data-tbl-sort-property='" + order.prop + "']", table )
                .each( function () {
                           if ( i == 0 ) {
                               $( this ).addClass( order.dir == 'ASC' ? 'asc' : 'desc' );
                           }
                           // We send in the original property name
                           order.prop = $( this ).data( 'tbl-field' );
                       } );
    }

    var pager = this;

    $( "[data-tbl='" + id + "'][data-tbl-page]" ).click( function () {
        pager.moveToPage( $( this ).attr( 'data-tbl-page' ) );
        return false;
    } );

    $( "input[type='text'][data-tbl='" + id + "'][data-tbl-page-selector]" )
            .click( function ( event ) {
                        event.preventDefault();
                        $( this ).select();
                    } )
            .keypress( function ( event ) {
                           if ( event.which == 13 ) {
                               event.preventDefault();
                               var pageNumber = $( this ).val();

                               if ( isNaN( pageNumber ) ) {
                                   $( this ).addClass( 'has-error' );
                               }
                               else {
                                   $( this ).removeClass( 'has-error' );
                                   if ( pageNumber < 1 ) {
                                       pageNumber = 1;
                                   }
                                   else if ( pageNumber > pager.totalPages ) {
                                       pageNumber = pager.totalPages;
                                   }
                                   pager.moveToPage( pageNumber - 1 );
                               }
                           }
                       } );

    this.sortables.click( function () {
        pager.sortOnProperty( $( this ).data( 'tbl-field' ) );
        return false;
    } );

    this.moveToPage = function ( pageNumber ) {
        var params = {
            'page': pageNumber,
            'size': this.size
        };

        if ( this.sort != null && this.sort.length > 0 ) {
            var sortProperties = [];

            for ( var i = 0; i < this.sort.length; i++ ) {
                sortProperties.push( this.sort[i].prop + ',' + this.sort[i].dir );
            }

            params['sort'] = sortProperties;
        }

        var pathUrl = window.location.href.split( '?' )[0];
        window.location.href = pathUrl + '?' + $.param( params, true );
    };

    this.sortOnProperty = function ( propertyName ) {
        var currentIndex = -1;

        for ( var i = 0; i < this.sort.length && currentIndex < 0; i++ ) {
            if ( this.sort[i].prop == propertyName ) {
                currentIndex = i;
            }
        }

        var order = {
            'prop': propertyName,
            'dir': 'ASC'
        };

        if ( currentIndex > -1 ) {
            if ( currentIndex == 0 ) {
                order.dir = this.sort[currentIndex].dir == 'ASC' ? 'DESC' : 'ASC';
            }

            if ( this.sort.length > 1 ) {
                this.sort.splice( currentIndex, 1 );
            }
            else {
                this.sort = [];
            }
        }

        this.sort = [order].concat( this.sort );

        this.moveToPage( page );
    };
};

$( document ).ready( function () {
    $( '[data-tbl-type="paged"]' ).each( function () {
        $( this ).sortableTable = new SortableTable( $( this ) );
    } );
} );